package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class SearchImageService {

    @Inject
    WSClient ws;

    private final static String bingSearchImageUrl = "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q=%s&mkt=fr-fr&cc=10&responseFilter=images";
    private final static String userBingId = "30e00c359c224fe1b5d7dbf3ea41a1a0";
    private final static String searchDirectoryBase = "./data/";
    private final static int maxImageToDownload = 30;

    public CompletionStage<String> search(final String query) {

        final String queryEncoded;
        final String searchFileName;
        final WSRequest request;

        try {
            if(query == null || query.isEmpty())
                throw new IllegalArgumentException("Argument query is required");

            Logger.info("SearchImageService with query={} - thread={}", query, Thread.currentThread().getName());

            searchFileName = String.format("%s%s.json",
                    searchDirectoryBase, StringUtils.stripAccents(query).replaceAll("[^\\w.-]", "_").toLowerCase());
            queryEncoded = getQueryEncoded(query);
            request = ws.url(String.format(bingSearchImageUrl, queryEncoded))
                    .setHeader("Ocp-Apim-Subscription-Key", userBingId)
                    .setHeader("X-MSEdge-ClientID", "123456789");

        } catch (Exception e) {
            return CompletableFuture.supplyAsync(() -> { throw e; });
        }

        return CompletableFuture.supplyAsync(() -> findNonExpiredSearch(searchFileName))
                .thenCompose(searchFound -> {
                    if(searchFound)
                        return CompletableFuture.completedFuture(searchFileName);
                    else
                        return request.get()
                                .thenApply(WSResponse::asJson)
                                .thenApply(json -> extractThumbnailsJson(query, json))
                                .thenApply(json -> writeSearchFile(searchFileName, json));
                });
    }

    // Si un fichier existe deja avec moins d'un jour alors on le garde et on n'appelle pas l'API
    private Boolean findNonExpiredSearch(final String searchFileName) {

        Logger.info("-> findNonExpiredSearch with searchFileName={} - thread={}", searchFileName, Thread.currentThread().getName());

        final File file = new File(searchFileName);
        return (file.exists() &&  (new Date().getTime() - file.lastModified() < (24 * 60 * 60 * 1000)));
    }

    private String getQueryEncoded(final String query) {

        //Logger.info("getQueryEncoded with query={} - thread={}", query, Thread.currentThread().getName());

        try {
            return URLEncoder.encode(StringUtils
                            .stripAccents(query)
                            .replaceAll("[^\\w.-]", " ")
                            .toLowerCase(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.error("Encoding not supported", e);
            throw new RuntimeException("An unexpected error occurred: ERR-S-SIS-001");
        }
    }

    private JsonNode extractThumbnailsJson(final String query, final JsonNode json) {

        Logger.info("-> extractThumbnailsJson with query={} - thread={}", query, Thread.currentThread().getName());

        List<String> thumbnailUrls = json.findValuesAsText("thumbnailUrl");

        if(thumbnailUrls != null && thumbnailUrls.size() >= maxImageToDownload) {
            thumbnailUrls = thumbnailUrls.subList(0, maxImageToDownload);
        }

        return Json.newObject()
                .put("query", query)
                .putPOJO("thumbnailUrls", thumbnailUrls);
    }

    private String writeSearchFile(final String fileName, final JsonNode json) {

        Logger.info("-> writeSearchFile with fileName={} - thread={}", fileName, Thread.currentThread().getName());

        final ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(fileName), json);
        } catch (IOException e) {
            Logger.error("The file " + fileName + " can\'t be written", e);
            throw new RuntimeException("An unexpected error occurred: ERR-S-SIS-002");
        }

        return fileName;
    }
}
