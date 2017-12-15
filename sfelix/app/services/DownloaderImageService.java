package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.ProcessData;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class DownloaderImageService {

    @Inject
    WSClient ws;

    private final static String imageDirectoryBase = "./public/images/search/";

    public CompletionStage<ProcessData> download(final String query, final String searchFileName) {

        final String imgDirectory;

        try {
            if(query == null || query.isEmpty())
                throw new IllegalArgumentException("Argument query is required");

            if(searchFileName == null || searchFileName.isEmpty())
                throw new IllegalArgumentException("Argument searchFileName is required");

            Logger.info("DownloaderImageService with searchFileName={} - thread={}", searchFileName, Thread.currentThread().getName());

            imgDirectory = String.format("%s%s",
                    imageDirectoryBase, StringUtils.stripAccents(query).replaceAll("[^\\w.-]", "_").toLowerCase());

        } catch (Exception e) {
            return CompletableFuture.supplyAsync(() -> { throw e; });
        }

        return CompletableFuture.supplyAsync(() -> createImageDirectory(imgDirectory))
                .thenCombineAsync(CompletableFuture.supplyAsync(() -> extractThumbnailUrls(searchFileName)), this::downloadImages)
                .thenApply(imgDownloaded -> new ProcessData(searchFileName, imgDirectory, imgDownloaded, 0));
    }

    private String createImageDirectory(final String directory) {

        Logger.info("-> createImageDirectory with directory={} - thread={}", directory, Thread.currentThread().getName());

        final Path path = Paths.get(directory);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                Logger.error("The directory " + directory + " can\'t be created", e);
                throw new RuntimeException("An unexpected error occurred: ERR-S-DIS-001");
            }
        } else {
            //Logger.info("The directory " + directory + " already exists");
        }

        return directory;
    }

    private List<String> extractThumbnailUrls(final String searchFileName) {

        Logger.info("-> extractThumbnailUrls with searchFileName={} - thread={}", searchFileName, Thread.currentThread().getName());

        final ObjectMapper mapper = new ObjectMapper();
        final List<String> urls = new ArrayList<>();

        try {
            final JsonNode jsonSearch = mapper.readTree(new File(searchFileName));
            final Iterator<JsonNode> thumbnailUrls = jsonSearch.findPath("thumbnailUrls").elements();

            while(thumbnailUrls.hasNext()){
                urls.add(thumbnailUrls.next().textValue());
            }
        } catch (IOException e) {
            Logger.error("The search file " + searchFileName + " can\'t be read", e);
            throw new RuntimeException("An unexpected error occurred: ERR-S-DIS-002");
        }

        return urls;
    }

    private int downloadImages(final String directory, final List<String> urls) {

        Logger.info("-> downloadImages with directory={} and {} urls - thread={}", directory, urls==null ? 0 : urls.size(), Thread.currentThread().getName());

        if (urls == null || urls.isEmpty()) {
            return 0;
        } else {
            return urls.parallelStream()
                    .map(url -> ws.url(String.format("%s&w=300&h=300&c=7", url)).get()
                            .thenApply(response -> writeImageFile(directory, response)))
                    .collect(Collectors.toList())
                    .stream()
                    .map(stage -> stage.toCompletableFuture().join())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
                    .size();
        }
    }

    private String writeImageFile(final String imgDirectory, final WSResponse response) {

        StringBuilder imgFileName = new StringBuilder(imgDirectory).append("/");

        final String name = String.format("%d",response.getUri().hashCode()).replaceAll("-", "a");
        imgFileName.append(name);

        final String contentType = response.getHeader("content-type");
        String[] contentTypeSplitted = contentType.split("/");

        if(contentTypeSplitted.length > 1) {
            imgFileName.append(".").append(contentTypeSplitted[1]);
        } else {
            imgFileName.append(".jpeg");
        }

        //Logger.info("writeImageFile with directory={} and imgFileName={} - thread={}", imgDirectory, imgFileName, Thread.currentThread().getName());

        if(Files.exists(Paths.get(imgFileName.toString()), LinkOption.NOFOLLOW_LINKS)) {
            //Logger.info("The image {} already exists", imgFileName);
            return null;
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(imgFileName.toString());
            fos.write(response.asByteArray());
        } catch (IOException e) {
            Logger.error("The image " + imgFileName + " can\'t be create", e);
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Logger.error("The image " + imgFileName + " can\'t be close", e);
                }
            }
        }

        return imgFileName.toString();
    }
}
