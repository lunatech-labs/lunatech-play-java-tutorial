package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.ImplementedBy;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import models.BingResult;

import javax.annotation.processing.Completion;

@ImplementedBy(ImagesManager.class)
public interface ImagesServices {

    /*
     * Retrieve urls associated with keywords q from a Bing servicde
     */
    public CompletionStage<BingResult> searchImageService(String q);

    /*
     * Upload an image and save it to a local file
     */
    public CompletionStage<String> loadImagesFromUrls(String q);

    /*
     * Save images associated with one keyword to the database
     */
    public CompletionStage<String> saveImagesToDb(String q);

    /********************************************************/
    /********************************************************/

    /*
     * Save urls associated with keywords q to a local file
     * The name of the local file is deduced from q
     */
    public String saveUrl(String json, String q);

    /*
     * Get all urls inside a file as a json string
     */
    public String loadUrlsFromJsonFile(String q);

    /*
     * Retrieve urls for each keyword having no images downloaded
     */
    public CompletionStage<String> retrieveAllUrl();

    /*
     *
     */
    public CompletionStage<String> saveAllImagesInFiles();

    public CompletionStage<String> saveAllImagesInDb();

    /*
     * Normalize a keyword
     */
    public String normalize(String q);
 }

