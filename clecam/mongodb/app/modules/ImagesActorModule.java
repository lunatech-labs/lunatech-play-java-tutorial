package modules;

import com.google.inject.AbstractModule;
import play.libs.akka.AkkaGuiceSupport;
import services.TaskActor;
import play.Configuration;
import play.Environment;

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

public class ImagesActorModule extends AbstractModule implements AkkaGuiceSupport {

    public ImagesActorModule() {
    }

    @Override
    public void configure() {
        bindActor(TaskActor.class, "actor-bing");
    }

}
