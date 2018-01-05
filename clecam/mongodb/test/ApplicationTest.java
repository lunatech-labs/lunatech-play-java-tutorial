
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import models.Product;
import org.junit.*;
import static org.junit.Assert.*;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import play.Configuration;
import play.inject.guice.GuiceApplicationBuilder;
import static play.inject.Bindings.bind;
import play.libs.concurrent.HttpExecutionContext;
import repository.DatabaseExecutionContext;
import repository.ProductDAO;
import services.TaskActor;
//import repository.EbeanProductRepository;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.util.concurrent.ForkJoinPool;

import javax.inject.Inject;
import javax.inject.Named;

public class ApplicationTest {

    private ProductDAO productDao;
    private HttpExecutionContext ec;
    private Datastore datastore;
    //final ActorRef taskActor;
    private DatabaseExecutionContext databaseEc;
    private Configuration configuration;
    private TaskActor taskActor;
    private static final play.Logger.ALogger logger = play.Logger.of(ApplicationTest.class);

    @Inject
    public ApplicationTest() {
        //@Named("actor-bing") ActorRef taskActor);
        logger.warn("#####");
        this.ec = new HttpExecutionContext(ForkJoinPool.commonPool());

        GuiceApplicationBuilder application = new GuiceApplicationBuilder();
        this.configuration = application.build().configuration();
        productDao = application.injector().instanceOf(ProductDAO.class);
        //datastore = application.injector().instanceOf(Datastore.class);
        //taskActor = application.injector().instanceOf(TaskActor.class);
    }

    @BeforeClass
    public static void startApp() {
        logger.warn("**** starting tests ****");
    }

    @AfterClass
    public static void stopApp() {
        logger.warn("**** ending tests ****");
    }

    @Test
    public void checkProductDaoNotNull() {
        assertNotNull(productDao);
    }

    @Test
    public void checkThatProductFindAllIsNotEmpty() {
        try {
            assertFalse(productDao.findAll().toCompletableFuture().get().isEmpty());
        } catch (InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatTheSizeOfProductsIsGreaterThan3() {
        try {
            assertTrue(productDao.findAll().toCompletableFuture().get().size() >= 3);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatThereIsOneProductWithEANEqualsToABSORB() {
        try {
            assertNotNull(productDao.findByEan("ABSORB").toCompletableFuture().get());
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatThereIsNoProductWithEANEqualsTo999() {
        try {
            assertNull(productDao.findByEan("999").toCompletableFuture().get());
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatFindByNameWorks() {
        try {
            assertNotNull(productDao.findByName("AGEN").toCompletableFuture().get());
            assertTrue(productDao.findByName("AGEN").toCompletableFuture().get().size() >= 2);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatSaveDeleteWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        Product myProduct = new Product(ean,name,description, null);

        try {
            assertNull(productDao.findByEan(ean).toCompletableFuture().get());
            productDao.save(myProduct);
            assertNotNull(productDao.findByEan(ean).toCompletableFuture().get());
            productDao.delete(myProduct);
            assertNull(productDao.findByEan(ean).toCompletableFuture().get());
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert(false);
        }
    }
}
