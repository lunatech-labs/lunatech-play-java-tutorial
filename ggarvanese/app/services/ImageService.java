package services;

import models.ImageSearchDatas;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ImageService {

    CompletionStage<ImageSearchDatas> searchAndStoreURLs(ImageSearchDatas imageSearchDatas);

}
