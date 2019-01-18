package services.clients;

import com.fasterxml.jackson.databind.JsonNode;
import models.SearchImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.ws.WSClient;
import services.DownloadService;

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
public class PixaBayImageClient implements ImageServiceClient {
    private Logger logger = LoggerFactory.getLogger(PixaBayImageClient.class);

    private final WSClient ws;

    static String key = "11300460-dfb09c721a84595d025ead502";
    static String host = "https://pixabay.com/api/";

    @Inject
    public PixaBayImageClient(WSClient ws) {
        this.ws = ws;
    }

    /**
     * Extracts a list of URLS based on a JSON response.
     *
     * @param json The JSON Node entry where we want to look for urls.
     * @return A List of URLs.
     */
    public List<SearchImageData> extractURLs(JsonNode json) {
        List<SearchImageData> retVal = new ArrayList<>();

        JsonNode hits = json.get("hits");

        Iterator<JsonNode> itr = hits.iterator();
        while (itr.hasNext()) {
            JsonNode temp = itr.next();
            retVal.add(new SearchImageData(temp.get("largeImageURL").asText()));
        }
        return retVal;
    }

    @Override
    public CompletionStage<List<SearchImageData>> getImagesFromDescription(String searchTerm) {
        // construct the search request URL (in the form of endpoint + query string)
        URL url = null;
        try {
            url = new URL(host + "?key="+ key + "&q=" + URLEncoder.encode(searchTerm, "UTF-8") + "&image_type=photo");
        } catch (MalformedURLException e) {
            play.Logger.error("URL is malformed", e);
        } catch (UnsupportedEncodingException e) {
            play.Logger.error("Unsupported encoding", e);
        } finally {
            return ws.url(url.toString())
                    .get()
                    .thenApply((response) -> this.extractURLs(response.asJson()));
        }
    }
}
