package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.URLEntity;
import models.WebSearch;
import play.Logger;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class WebSearchService {

    @Inject
    public WebSearchService(WSClient ws) {

        this.ws = ws;
    }

    private WSClient ws = WS.newClient(9000);

    public static HashSet<String> visitedTarget = new HashSet<>();

    public WebSearchService() {

    }

    public void searchTerm(String target) {
        if (visitedTarget.contains(target))
            return;

        Optional<WebSearch> optWebSearch = WebSearch.findByName(target);

        if (optWebSearch.isPresent()
                && optWebSearch.get().urlList != null
                && optWebSearch.get().urlList.size() > 10)
            return;

        WebSearch searched;

        if (optWebSearch.isPresent())
            searched = optWebSearch.get();
        else {
            searched = new WebSearch(target);
            searched.save();
        }

        try {

            String targetSearchName = Objects.requireNonNull(URLEncoder.encode(searched.name, "UTF-8"));

            String url = "https://api.cognitive.microsoft.com/bing/v7.0/search?q="
                    + targetSearchName
                    + "&responseFilter=Images"
                    + "&count=10";

            CompletionStage<WSResponse> futureResponse = ws
                    .url(url)
                    .setMethod("GET")
                    .setHeader("Ocp-Apim-Subscription-Key", "f49b1b0c9d234ebfbaf884b2db2090d2")
                    .setHeader("X-MSEdge-ClientIP", "90.88.75.134")
                    .setHeader("Host", "api.cognitive.microsoft.com")
                    .get();

            CompletionStage<JsonNode> jsonNode = futureResponse
                    .thenApply(WSResponse::asJson)
                    .thenApply(response -> response.findValue("images")
                            .withArray("value"));

            Logger.debug(searched.name + " - attempted for new url");

            jsonNode.thenAccept(res -> res.findValues("contentUrl")
                    .forEach(contentUrl -> {
                                Logger.debug(searched.name + " - add new url");
                                searched.urlList.add(new URLEntity(contentUrl.asText()));
                                searched.update();
                                visitedTarget.add(searched.name);
                            }
                    ));
        } catch (UnsupportedEncodingException uee) {
            Logger.error("WebSearchService.searchTerm(String term)", uee);
        }
    }
}