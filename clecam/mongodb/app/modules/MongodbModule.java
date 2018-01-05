package modules;

import com.google.inject.AbstractModule;
import com.github.mongobee.Mongobee;
import com.github.mongobee.exception.MongobeeException;
import models.Product;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.logging.jdk.JDKLoggerFactory;
import play.libs.akka.AkkaGuiceSupport;
import play.Configuration;
import play.Environment;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;

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

public class MongodbModule extends AbstractModule implements AkkaGuiceSupport {

    private final Environment environment;
    private final Configuration configuration;

    public MongodbModule(Environment environment, Configuration configuration) {
        this.environment = environment;
        this.configuration = configuration;
    }

    @Override
    public void configure() {
        // MongoClient
        final String uri = configuration.underlying().getString("mongo.hiqea.uri");
        final String dbName = configuration.underlying().getString("mongo.hiqea.database");
        MongoClientURI m = new MongoClientURI(uri);
        //final MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));
        final MongoClient mongoClient = new MongoClient(m);
        bind(MongoClient.class).toInstance(mongoClient);
        // pool size ~ getConnectionsPerHost
        //System.out.println("mongo=" + mongoClient.getMongoClientOptions().getConnectionsPerHost());

        // Morphia Datastore
        final Morphia morphia = new Morphia();
        Datastore datastore = morphia.createDatastore(mongoClient, dbName);
        datastore.ensureIndexes(true);
        bind(Datastore.class).toInstance(datastore);
        morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return Product.class.getClassLoader();
            }
        });
        MorphiaLoggerFactory.reset();
        MorphiaLoggerFactory.registerLogger(JDKLoggerFactory.class);

        // Mongobee
        Mongobee mongobee = new Mongobee(mongoClient);
        mongobee.setDbName(dbName);
        if (environment.isProd()) {
            System.out.println("*** prod ***");
            mongobee.setChangeLogsScanPackage("models.changelog.ProdChangelog");
        }
        if (environment.isTest()) {
            System.out.println("*** test ***");
            mongobee.setChangeLogsScanPackage("models.changelog.TestChangelog");
        }
        if (environment.isDev()) {
            System.out.println("*** dev ***");
            mongobee.setChangeLogsScanPackage("models.changelog.DevChangelog");
        }
        try {
            System.out.println("*** mongobee starting ***");
            mongobee.execute();
        } catch (MongobeeException e) {
            throw new RuntimeException("Une erreur s'est produite lors de l'execution de changelog Mongodb", e);
        }

    }

}
