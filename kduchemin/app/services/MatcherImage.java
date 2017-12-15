package services;

import models.Product;
import play.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MatcherImage {


    private static final String DEFAULT_IMAGE_PATH = "./public/images/";
    private static Set<Path> setFilesPath = new HashSet<>();
    private static Random random = new Random(System.currentTimeMillis());

    public static void matching() {

        try {
            filesFromDirectory(Paths.get(DEFAULT_IMAGE_PATH));
            setFilesPath.forEach(value -> Logger.debug(value.toString()));
            Product.findAll().forEach(product -> {

                List<Path> paths = setFilesPath
                        .stream()
                        .filter(path -> path.getNameCount() == 5 && path.getName(3).toString().equals(product.description))
                        .collect(Collectors.toList());
                if (paths.size() <= 0)
                    return;
                int tour = 3;
                while (tour-- > 0
                        && (product.picture == null || product.picture.length == 0)
                        && product.pathLocalPicture == null) {

                    int index = random.nextInt(paths.size());
                    product.pathLocalPicture = paths.get(index).toString();
                    product.update();
                }
            });
        } catch (IOException io) {
            Logger.error("MatcherImage.matching() :", io);
        }
    }

    private static void filesFromDirectory(Path path) throws IOException {
        try (Stream<Path> paths = Files.walk(path)) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    setFilesPath.add(filePath);
                }
            });
        }
    }
}