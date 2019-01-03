import hexagon.IProductWsPicture;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class ProductWsPictureMock implements IProductWsPicture {

    public Optional<String> randomPicture(long ean) {
        return Optional.ofNullable("images/store/default-picture.png");
    }
}
