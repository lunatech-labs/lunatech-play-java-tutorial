package services.clients;

import com.fasterxml.jackson.databind.JsonNode;
import models.SearchImageData;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Singleton
public class BingImageClient implements ImageServiceClient {
    private final WSClient ws;

    static String subscriptionKey = "754aece3d89f42638276ce34ed4073eb";
    static String host = "https://api.cognitive.microsoft.com";
    static String path = "/bing/v7.0/images/search";

    @Inject
    public BingImageClient(WSClient ws) {
        this.ws = ws;
    }

    /**
     * @param searchTerm {String} The search term we are looking for
     * @return A List of images corresponding to the search term.
     */
    @Override
    public CompletionStage<List<SearchImageData>> getImagesFromDescription(String searchTerm) {
        // construct the search request URL (in the form of endpoint + query string)
        URL url = null;
        try {
            url = new URL(host + path + "?q=" + URLEncoder.encode(searchTerm + " furniture", "UTF-8"));
        } catch (MalformedURLException e) {
//            Logger.error("URL is malformed", e);
        } catch (UnsupportedEncodingException e) {
//            Logger.error("Unsupported encoding", e);
        } finally {
            return ws.url(url.toString())
                    .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .get()
                    .thenApply((response) -> this.extractURLs(response.asJson()));
        }
    }

    /**
     * Extracts a list of URLS based on a JSON response.
     *
     * @param json The JSON Node entry where we want to look for urls.
     * @return A List of URLs.
     */
    public List<SearchImageData> extractURLs(JsonNode json) {
        List<SearchImageData> retVal = new ArrayList<>();

        JsonNode hits = json.get("value");

        Iterator<JsonNode> itr = hits.iterator();
        while (itr.hasNext()) {
            JsonNode temp = itr.next();
            retVal.add(new SearchImageData(temp.get("contentUrl").asText()));
        }
        return retVal;
    }
}
