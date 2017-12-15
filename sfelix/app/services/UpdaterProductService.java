package services;

import models.ProcessData;
import models.Product;
import play.Logger;
import play.libs.F;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class UpdaterProductService {

    public CompletionStage<ProcessData> update(final String query, final ProcessData data) {

        try {
            if(query == null || query.isEmpty())
                throw new IllegalArgumentException("Argument query is required");

            if(data == null)
                throw new IllegalArgumentException("Argument data is required");

            if(data.imgDirectory == null || data.imgDirectory.isEmpty())
                throw new IllegalArgumentException("Argument data.imgDirectory is required");

            Logger.info("UpdaterProductService with imgDirectory={} - thread={}", data.imgDirectory, Thread.currentThread().getName());

        } catch (Exception e) {
            return CompletableFuture.supplyAsync(() -> { throw e; });
        }

        return CompletableFuture.supplyAsync(() -> Product.findByQuery(query))
                .thenCombineAsync(CompletableFuture.supplyAsync(() -> getImagesPath(data.imgDirectory)), this::updateProducts)
                .thenApply(productUpdated -> { data.productUpdated = productUpdated; return data; } );
    }

    private List<String> getImagesPath(final String directoryPath) {

        Logger.info("-> getImagesPath with directory={} - thread={}", directoryPath, Thread.currentThread().getName());

        List<F.Tuple<String,Long>> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
            for (Path path : directoryStream) {
                fileNames.add(new F.Tuple<>(path.toString().replaceFirst("[.]/public/", ""), path.toFile().lastModified()));
            }
        } catch (IOException e) {
            Logger.error("The directory " + directoryPath + " can\'t be read", e);
            throw new RuntimeException("An unexpected error occurred");
        }

        fileNames.sort(Comparator.comparing(o -> -o._2));
        return fileNames.stream().map(o -> o._1).collect(Collectors.toList());
    }

    private int updateProducts(final List<Product> products, final List<String> filenames) {

        Logger.info("-> updateProducts with {} products and {} filenames - thread={}",
                products==null?0:products.size(), filenames==null?0:filenames.size(), Thread.currentThread().getName());

        int current = 0;
        int count = 0;

        if (products == null || products.isEmpty() || filenames == null || filenames.isEmpty()) {
            return 0;
        } else {
            for (Product product : products) {
                if (current >= filenames.size())
                    current = 0;

                if (product.picturePath == null || product.picturePath.isEmpty()) {
                    product.picturePath = filenames.get(current++);
                    product.modify();
                    count++;
                }
            }
            return count;
        }
    }
}
