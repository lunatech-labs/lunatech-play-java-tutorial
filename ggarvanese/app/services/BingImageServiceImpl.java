package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.ImageSearchDatas;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BingImageServiceImpl implements ImageService {

    private WSClient ws;

    @Inject
    public BingImageServiceImpl(WSClient ws) {
        this.ws = ws;
    }

    @Override
    public CompletionStage<ImageSearchDatas> searchAndStoreURLs(ImageSearchDatas imageSearchDatas) {
        String searchTerm = imageSearchDatas.getSearchTerm();
        if (StringUtils.isEmpty(searchTerm)){
            return CompletableFuture.completedFuture(imageSearchDatas);
        }
        return lookupAndExtractJSONUrls(searchTerm)
                .thenApply(this::extractURLs)
                .thenApply(urls -> storeURLsAndKeyword(searchTerm, urls))
                .thenApply(maybeFileName -> {
                    imageSearchDatas.setUrlsFilePath(maybeFileName);
                    return imageSearchDatas;
                });
    }

    protected CompletionStage<JsonNode> lookupAndExtractJSONUrls(String imageKeyword) {
        WSRequest request = getRequest(imageKeyword);
        return request.get().thenApply(WSResponse::asJson);
    }

    protected List<String> extractURLs(JsonNode jsonNode) {
        List<String> urlsList = new ArrayList<>();
        JsonNode parentNode = jsonNode.findPath("value");
        parentNode.forEach(node -> urlsList.add(node.findPath("contentUrl").asText()));
        return urlsList;
    }

    protected Optional<String> storeURLsAndKeyword(String imageKeyword, List<String> urls) {
        if (StringUtils.trimToNull(imageKeyword) == null) {
            return Optional.empty();
        }
        if (urls.isEmpty()) {
            return Optional.empty();
        }
        imageKeyword = normalizedFolderName(imageKeyword);
        Path urlsPath = Paths.get("public/images/" + imageKeyword + ".txt");
        try {
            Files.deleteIfExists(urlsPath);
            if (!Files.exists(urlsPath)) {
                Files.createFile(urlsPath);
            }
            Files.write(urlsPath, urls);
        } catch (IOException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(urlsPath.toString());
    }

    private static String normalizedFolderName(String imageKeyword) {
        return imageKeyword.toLowerCase().replaceAll("\\W+", "");
    }


    private WSRequest getRequest(String searchTerm) {
        return ws.url("https://api.cognitive.microsoft.com/bing/v7.0/images/search")
                .addQueryParameter("q", searchTerm)
                .addHeader("Ocp-Apim-Subscription-Key", "2f1e9ba3d00d47a095c71b87f092dbde");
    }

}
