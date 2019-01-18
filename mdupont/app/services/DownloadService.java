package services;

import akka.actor.ActorSystem;
import akka.dispatch.ExecutorServiceFactory;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.ConfigException;
import models.SearchImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.api.Play;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is intended to be used as a File Service. It can contains anything related to storing, downloading, writing files.
 */
@Singleton
public class DownloadService {

    private final WSClient ws;
    private final Materializer materializer;

    private Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Inject
    public DownloadService(final WSClient ws, final ActorSystem system) {
        this.ws = ws;
        this.materializer = ActorMaterializer.create(system);
    }


    // Play 2.5 Akka Stream -> Download an image
    public CompletionStage<Optional<File>> downloadImageFile(String imageURL, String filePath) {

        play.Logger.debug("Downloading " + imageURL);

        CompletionStage<WSResponse> futureResponse = ws.url(imageURL).setFollowRedirects(true).stream();

        // Version 2
        return futureResponse.thenApply(response -> downloadFile(response, imageURL, filePath));
    }

    protected Optional<File> downloadFile(WSResponse streamedResponse, final String url, final String imageDirPath) {
        if (streamedResponse.getStatus() != 200) {
            play.Logger.warn("Tried to download an image but got a HTTP Status error code != 200", url);
            return Optional.empty();
        }
        List<String> allContentType = streamedResponse.getHeaders().get("Content-Type");
        String contentType;
        try {
            if (allContentType.isEmpty()) {
                contentType = "application/octet-stream";
            } else {
                contentType = allContentType.get(0);
            }
        } catch (NullPointerException e) {
            return Optional.empty();
        }

        if (contentType.equalsIgnoreCase("text/html")) {
            play.Logger.warn("Received text/html instead of image. Could not download " + url);
            return Optional.empty();
        }

        File imagesFolder = Play.current().getFile("public/images/" + imageDirPath);
        if (imagesFolder.exists()) {
            if (!imagesFolder.canRead() || !imagesFolder.canWrite()) {
                play.Logger.error("The folder public/images does not exist or cannot be read");
                return Optional.empty();
            }
        } else {
            imagesFolder.mkdirs();
        }

        String extension = extractExtension(url);
        if (extension == null) {
            play.Logger.error("Could not find image type from url " + url);
            play.Logger.error("However we had content-type => " + contentType);
        } else {

            File newImage = new File(imagesFolder, url.hashCode() + extension);
            streamedResponse.getBodyAsSource().runWith(FileIO.toPath(newImage.toPath()), materializer);
            play.Logger.debug("Saved new image " + newImage.getAbsoluteFile());
            return Optional.of(newImage);
        }

        return Optional.empty();
    }

    protected String extractExtension(String url) {
        if (url == null) return null;
        if (url.toLowerCase().endsWith(".jpg")) return ".jpg";
        if (url.toLowerCase().endsWith(".jpeg")) return ".jpeg";
        if (url.toLowerCase().endsWith(".png")) return ".png";
        if (url.toLowerCase().contains(".jpg")) return ".jpg";
        if (url.toLowerCase().contains(".png")) return ".png";
        return null;
    }
}
