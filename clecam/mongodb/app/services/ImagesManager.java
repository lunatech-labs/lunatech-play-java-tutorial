package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.BingResult;
import models.ImageContent;
import models.Product;
import play.Configuration;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import repository.ProductDAO;

import javax.inject.Inject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ImagesManager implements ImagesServices {

    private WSClient ws;
    private Configuration configuration;
    private play.Logger.ALogger logger = play.Logger.of(getClass());
    private ProductDAO productDao;

    @Inject
    public ImagesManager(WSClient ws, Configuration configuration, ProductDAO productDao) {
        this.ws = ws;
        this.configuration = configuration;
        this.productDao = productDao;
    }

    // curl --header "Ocp-Apim-Subscription-Key:3a3c89917c9c47728adfc58a6a63097d" "https://api.cognitive.microsoft.com/bing/v7.0/images/search?q=furniture"
    // FIXME: subscriptionKey -> provide your key
    @Override
    public CompletionStage<BingResult> searchImageService(String q) {
        final String subscriptionKey = "<CHANGE-ME>";
        final String host = "http://api.cognitive.microsoft.com";
        final String path = "/bing/v7.0/images/search";
        WSRequest request = null;

        try {
            //logger.warn(host + path + "?q=" + URLEncoder.encode(q, "UTF-8"));
            request = ws.url(host + path + "?q=" + URLEncoder.encode(q, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        request.setHeader("Accept", "application/json");
        request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
        request.setRequestTimeout(5000);
        return request.get().thenApply(response -> {
            BingResult bingResult = new BingResult();
            bingResult.setStatus(response.getStatus());
            ObjectMapper mapper = new ObjectMapper();
            if (response.getStatus() != 200)
                bingResult.setMessage(response.getStatusText());
            else {
                JsonNode jsonNode = response.asJson().findPath("value");
                java.util.List<JsonNode> urls = jsonNode.findValues("contentUrl");
                bingResult.setUrls(urls);
            }
            return bingResult;
        });
    }

    private boolean fileExist(String fullname) {
        java.io.File f = new java.io.File(fullname);
        return (f.exists() && !f.isDirectory());
    }

    public String normalize(String q) {

        return q.toLowerCase().replace(" ", "_")
                .replace("/", "_")
                .replace("'", "_")
                .replace("(", "_")
                .replace(")", "_");
    }

    private String getFullname(String q, String rep, String ext) {
        String filename = normalize(q);
        return configuration.getString("file.image.path") + rep + filename + "." + ext;
    }

    /************************
     * Search Image Service *
     ************************/

    @Override
    // FIXME: only 1 block try/catch
    public String saveUrl(String json, String q) {
        java.io.File file = new java.io.File(getFullname(q, "", "json"));
        java.io.FileOutputStream is = null;
        try {
            is = new java.io.FileOutputStream(file);
        } catch (java.io.FileNotFoundException e) {
            return "file not found";
        }
        try (java.io.OutputStreamWriter osw = new java.io.OutputStreamWriter(is)) {
            java.io.Writer w = new java.io.BufferedWriter(osw);
            //w.write(json.toString());
            w.write(json.toString());
            w.close();
        } catch (Exception e) {
            logger.error("File " + file.getAbsolutePath() + " write error", e);
            return "exception";
        }
        try {
            is.close();
        } catch (Exception e) {
            return "exception is.close";
        }
        return "success";
    }

    /***************************
     * Show url from json file *
     ***************************/

    public String loadUrlsFromJsonFile(String q) {
        String absoluteName = getFullname(q, "", "json");
        String s = "error";
        try {
            s = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(absoluteName)));
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return s;
    }

    /************************
     * Load images from url *
     ************************/

    /*
     * Load all url from file relative to the q description parameter
     * and then save each image to the file system with _<number>.[jpg, png...]
     */

    @Override
    public CompletionStage<String> loadImagesFromUrls(String q) {
        return CompletableFuture.supplyAsync(() -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                // get all url for q keyword
                JsonNode valuesNode = mapper.readTree(loadUrlsFromJsonFile(q)).get("url");
                java.util.List<String> lUrl = new java.util.ArrayList<String>();
                for (JsonNode node : valuesNode)
                    lUrl.add(node.asText());

                if (lUrl.size() == 0)
                    return "error: no url.";
                int i = 0;
                // TODO: parallelisation
                for (String url : lUrl) {
                    CompletionStage<ImageContent> imgTodo = downloadImage(url);
                    // blocking !
                    ImageContent img = imgTodo.toCompletableFuture().exceptionally(throwable -> {
                        logger.error(url + " -> " + throwable.getMessage());
                        return null;
                    }).get();
                    if (img != null) {
                        String fullname = getFullname(q + "_" + i, "images/", img.getContentType());
                        saveImageToFile(img, fullname);
                        i = i + 1;
                    }
                }

            } catch (Exception e) {
                logger.error("", e);
                return "error";
            }
            return "success";
        });
    }

    private void saveImageToFile(ImageContent img, String fullname) {
        if (!fileExist(fullname)) {
            try {
                java.io.FileOutputStream is = new java.io.FileOutputStream(fullname);
                is.write(img.getContent());
                is.close();
            } catch (Exception e) {
                logger.error("File " + fullname + " write error", e);
            }
        }
    }

    /*********************
     * Retrieve all urls *
     *********************/

    private boolean fileIsEmpty(String q) {
        String s = loadUrlsFromJsonFile(q);
        JsonNode jsonNode = play.libs.Json.parse(s).findPath("url");
        return (jsonNode.size() == 0);
    }

    /*
     * Retrieve url from product having no images downloaded
     */
    @Override
    public CompletionStage<String> retrieveAllUrl() {
        return productDao.findAll().thenApply(l -> {
            //throw new CancellationException();
            java.util.List<String> lDescription = l.stream().map(p -> p.description).distinct().collect(java.util.stream.Collectors.toList());
            for (String q : lDescription) {
                if (q != null) {
                    String fullname = getFullname(q, "", "json");
                    if (!fileExist(fullname) || fileIsEmpty(q)) {
                        logger.warn(q + " " + fileExist(fullname) + " " + fileIsEmpty(q));
                        // QPS = 3 request per second max
                        searchImageService(q).thenApply(bingResult -> {
                            if (bingResult.getStatus() != 200)
                                return "Status: " + bingResult.getStatus() + " - " + bingResult.getMessage();
                            String jsonString = "";
                            try {
                                jsonString = (new ObjectMapper()).writeValueAsString(bingResult.extractUrls());
                            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                                return "Json Processing Exception.";
                            }
                            if (!jsonString.equals(""))
                                saveUrl(jsonString, q);
                            try {
                                // Do not spam bing service...
                                Thread.sleep(340);
                            } catch (InterruptedException e) {
                                //throw new CompletionException(e) (just example here to see how to use it);
                                throw new RuntimeException();
                            }
                            return "success";
                        }).exceptionally(throwable -> "Cannot build json object");
                    }
                } // if (q != null)
            } // for
            return "success";
        });
    }

    /*******************************************
     * Save image for each product description *
     * in the repository public/images         *
     *******************************************/

    /*
     * For each keyword, get description and upload images
     */
    @Override
    public CompletionStage<String> saveAllImagesInFiles() {
        return productDao.findAll().thenApply(l -> {
            java.util.List<String> lDescription = l.stream().map(p -> p.description).distinct().collect(java.util.stream.Collectors.toList());
            for (String q : lDescription) {
                if (q != null) {
                    String fullname = getFullname(q, "", "json");
                    //logger.warn("keywords: " + q);
                    // check if json file containing url images exist
                    if (fileExist(fullname)) {
                        // check if image is savecd on local disk
                        if (!fileImageExist(q))
                            loadImagesFromUrls(q);
                    } else
                        return "error description";
                }
                //return ok("done");
            }
            logger.warn("done");
            return "success";
        });
    }

    /*
     * Get image from url
     */
    private CompletionStage<ImageContent> downloadImage(String url) {

        try {
            WSRequest request = ws.url(url);
            request.setHeader("Accept", "application/json");
            // asynchronous
            //CompletionStage<byte[]> b1 = request.get().thenApply(response -> {
            return request.get().thenApply(response -> {
                String contentType = response.getHeader("Content-Type");
                if (contentType.contains("image/")) {
                    contentType = contentType.split("image")[1];
                    // get rid of "image/jpeg;..."
                    if (contentType.contains(";"))
                        contentType = contentType.split(";")[0];
                    contentType = contentType.split("/")[1];
                    byte[] b = response.asByteArray();
                    return new ImageContent(b, contentType);
                }
                return null;
            });
            //responsePromise = request.get().thenApply(WSResponse::asJson);
        } catch (Exception e) {
            logger.error(url, e);
        }
        return null;
    }

    // check if one image exist for a keyword: <prefix>_<number>.ext
    private boolean fileImageExist(String q) {
        String filename = normalize(q);
        String dirname = configuration.getString("file.image.path") + "images/";
        java.io.File fDir = new java.io.File(dirname);
        java.io.File[] files = fDir.listFiles();
        for (java.io.File f : files)
            if (f.getAbsolutePath().contains(filename))
                return true;
        return false;
    }

    /*******************************************
     * Save image for each product description *
     * in the database                         *
     *******************************************/

    @Override
    public CompletionStage<String> saveImagesToDb(String q) {
        java.util.Random rnd = new java.util.Random();
        rnd.setSeed(java.time.Instant.now().toEpochMilli());

        return productDao.findByDescription(q).thenApply(lProduct -> {
            try {
                saveImagesForAllProductsWithDescription(q, lProduct, rnd);
                return "success";
            } catch (IOException e) {
                throw new CancellationException(e.getMessage());
            }
        }).exceptionally(throwable -> {
            return throwable.getMessage();
        });
    }

    private void saveImagesForAllProductsWithDescription(String q, java.util.List<Product> lProduct, java.util.Random rnd) throws java.io.IOException {
        String dirname = configuration.getString("file.image.path") + "images/";
        // get filename containing image for keywords q
        java.util.List<java.nio.file.Path> lFilename =
                java.nio.file.Files.find(java.nio.file.Paths.get(dirname),
                        1,
                        (path, basicFilesAttributes) -> path.toFile().getName().contains(normalize(q)))
                        .collect(java.util.stream.Collectors.toList());
        if (lFilename.size() >= 1) {
            for (Product p : lProduct) {
                int i = rnd.nextInt(lFilename.size());
                String filename = lFilename.get(i).toAbsolutePath().toString();
                p.picture = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filename));
                productDao.save(p);
                logger.warn(filename);
            }
        } else
            logger.warn("no image for keywords " + q);

    }

    @Override
    public CompletionStage<String> saveAllImagesInDb() {
        java.util.Random rnd = new java.util.Random();
        rnd.setSeed(java.time.Instant.now().toEpochMilli());

        return productDao.findAll().thenApply(l -> {
            // Group product by description
            java.util.Map<String, java.util.List<Product>> h = l.stream().collect(
                java.util.stream.Collectors.groupingBy(p -> {
                    if (p.description != null)
                        return p.description.toLowerCase();
                    else return "";
                }));
            try {
                // Iteration over description
                for (java.util.Map.Entry<String, java.util.List<Product>> entry : h.entrySet()) {
                    String q = entry.getKey();
                    java.util.List<Product> lProduct = entry.getValue();
                    saveImagesForAllProductsWithDescription(q, lProduct, rnd);
                }
            } catch (Exception e) {
                logger.error("", e);
                return "error";
            }
            return "success";
        });
    }

    private byte[] readImageFromFile(String fullname) {
        return null;
    }

}
