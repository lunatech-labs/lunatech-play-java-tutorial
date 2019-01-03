import com.google.inject.AbstractModule;
import dao.ProductDao;
import hexagon.IProductDao;
import hexagon.IProductService;
import hexagon.IProductWsPicture;
import hexagon.ProductService;
import play.libs.akka.AkkaGuiceSupport;
import actors.PictureActorGenerator;
import services.ProductWsPicture;

import java.time.Clock;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 * <p>
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule implements AkkaGuiceSupport {

    @Override
    public void configure() {
        // Use the system clock as the default implementation of Clock

        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
        bind(IProductDao.class).to(ProductDao.class);
        bind(IProductWsPicture.class).to(ProductWsPicture.class);
        bind(IProductService.class).to(ProductService.class);
        bindActor(PictureActorGenerator.class, "Picture");

    }
}
