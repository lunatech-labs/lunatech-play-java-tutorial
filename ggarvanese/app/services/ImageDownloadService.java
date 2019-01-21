package services;

import models.ImageSearchDatas;

import java.util.concurrent.CompletionStage;

public interface ImageDownloadService {

    CompletionStage<ImageSearchDatas> downloadAndStoreImages(ImageSearchDatas imageSearchDatas);
}
