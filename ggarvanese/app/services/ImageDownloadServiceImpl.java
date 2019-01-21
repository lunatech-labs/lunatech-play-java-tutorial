package services;


import com.google.common.io.Files;
import models.ImageSearchDatas;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ImageDownloadServiceImpl implements ImageDownloadService {

    private final HttpExecutionContext ec;

    @Inject
    public ImageDownloadServiceImpl(HttpExecutionContext ec) {
         this.ec = ec;
    }

    @Override
    public CompletionStage<ImageSearchDatas> downloadAndStoreImages(ImageSearchDatas imageSearchDatas) {
        List<String> urls = parseFileToList(imageSearchDatas.getUrlsFilePath().get());
        String searchTerm = imageSearchDatas.getSearchTerm();
        List<String> imagesPath = executeDownloadAndStore(urls, searchTerm).join();
        imageSearchDatas.setImagesPaths(imagesPath);
        return CompletableFuture.completedFuture(imageSearchDatas);
    }

    protected List<String> parseFileToList(String filePath) {
        try {
            return Files.readLines(new File(filePath), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    protected CompletableFuture<List<String>> executeDownloadAndStore(List<String> urls, String term) {
        List<String> imagesPaths = new ArrayList<>();
        Path path = Paths.get("public/images/" + term);
        if (!java.nio.file.Files.exists(path)) {
            new File(path.toString()).mkdir();
        }

        return CompletableFuture.supplyAsync(() -> {
            for (String link : urls) {
                String fileName = UUID.randomUUID().toString() + ".jpg";
                try {
                    FileUtils.copyURLToFile(new URL(link), new File(path.toString() + "/" + fileName), 10000, 10000);
                } catch (IOException e) {
                    Logger.info(e.getMessage());
                }
                imagesPaths.add(term + "/" + fileName);
            }
            return imagesPaths;
        }, ec.current());
    }

}
