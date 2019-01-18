package services.clients;

import models.SearchImageData;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ImageServiceClient {
    CompletionStage<List<SearchImageData>> getImagesFromDescription(String description);
}
