import actors.ImageSearchActor;
import actors.ImagesDownloaderActor;
import actors.ImagesProductsUpdateActor;
import actors.JokeActor;
import com.google.inject.AbstractModule;
import models.ProductDAO;
import persistence.ProductH2EbeanDAO;
import play.libs.akka.AkkaGuiceSupport;
import services.*;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {

    @Override
    public void configure() {
        // Use the system clock as the default implementation of Clock
        bind(ProductService.class).to(ProductServiceImpl.class);
        bind(ProductDAO.class).to(ProductH2EbeanDAO.class);
        bind(ImageService.class).to(BingImageServiceImpl.class);
        bind(ImageDownloadService.class).to(ImageDownloadServiceImpl.class);
        bindActor(JokeActor.class, "theActor");
        bindActor(ImageSearchActor.class, "imagesSearchActor");
        bindActor(ImagesDownloaderActor.class, "imagesDownloaderActor");
        bindActor(ImagesProductsUpdateActor.class, "imagesProductsUpdateActor");
    }

}
