package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.SearchImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.clients.BingImageClient;
import services.clients.PixaBayImageClient;
import views.html.images;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * This class is used to search from the Web for images based on a search terms and stores the results into a file on the
 * filesystem.
 */
@Singleton
public class SearchService {

    @Inject
    PixaBayImageClient pixaBayImageClient;

    @Inject
    BingImageClient bingImageClient;

    public SearchService() {
        // Usually build by Injection
    }

    /**
     * This method download files and write them to disk. It takes a list with some path URL in it and
     * download this file and returns it to the place we want.
     *
     * @param searchTerm Search term
     * @return The Filepath of where the data has been saved
     */
    public CompletionStage<File> search(String searchTerm) {
        return this.bingImageClient.getImagesFromDescription(searchTerm)
                .thenApply((urls) -> this.saveFile(searchTerm, urls));
    }

    /**
     * This method is used to save the search results on disk
     *
     * @param searchTerm String Search Term
     * @param urls       List<SearchService> List of urls to store on disk
     * @return File The File path of the file saved
     */
    protected File saveFile(String searchTerm, List<SearchImageData> urls) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + searchTerm + ".json");
        play.Logger.info("Writing URLs for" + searchTerm + " to " + file.getAbsolutePath());

        try {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, urls);
        } catch (IOException e) {
            play.Logger.error("Something wrong happened when writing search results to JSON file", e);
        } finally {
            return file;
        }
    }
}
