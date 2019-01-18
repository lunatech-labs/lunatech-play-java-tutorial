import actors.ChuckNorrisActor;
import actors.DbUpdateImageActor;
import actors.DownloadActor;
import actors.messages.DBUpdateImageActorMsg;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import models.ProductDao;
import persistence.ProductDaoImpl;
import play.libs.akka.AkkaGuiceSupport;

import java.time.Clock;


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
        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.

        bind(ProductDao.class).to(ProductDaoImpl.class);
        bindActor(ChuckNorrisActor.class, "theActor");
        bindActor(DownloadActor.class, "downloadActor");
        bindActor(DbUpdateImageActor.class, "dbUpdateImageActor");
    }
}
