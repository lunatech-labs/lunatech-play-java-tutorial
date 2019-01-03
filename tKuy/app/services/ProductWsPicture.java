package services;

import hexagon.IProductWsPicture;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.libs.ws.ahc.AhcWSClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
@Singleton
public class ProductWsPicture implements IProductWsPicture {
    private WSClient ws;

    @Inject
    public ProductWsPicture(WSClient ws) {
        this.ws = ws;
    }

    public Optional<String> randomPicture(long ean) {
        System.out.println("WS "+ws);
        WSRequest request = ws.url("https://picsum.photos/300/200/?image="+ean);
        CompletionStage<WSResponse> response = request.get();
        String pictureName = "image"+ean+".jpg";
        Path path = Paths.get("public/images/store/"+pictureName);
        if(path.toFile().exists()) {
            return Optional.ofNullable(pictureName);
        }
        CompletionStage<String> img = response.thenApply(x -> x.asByteArray()).thenApply(bytes -> {
            try {
                if (!path.toFile().exists()) {
                    Files.createFile(path);
                    Files.write(path, bytes);
                }
                return pictureName;
            } catch (IOException e) {
                Logger.error("The image creation has failed");
                return null;
            }
        });

        try {
            return Optional.ofNullable(img.toCompletableFuture().get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }
}
