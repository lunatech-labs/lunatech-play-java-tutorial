
import com.fasterxml.jackson.databind.JsonNode;
import models.Product;
import org.junit.*;
import static org.junit.Assert.*;

import play.libs.concurrent.HttpExecutionContext;
import repository.EbeanProductRepository;
import repository.ProductRepository;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.util.concurrent.ForkJoinPool;

import javax.inject.Inject;

public class ApplicationTest {

    private ProductRepository productRepository;
    private HttpExecutionContext ec;

    @Inject
    public ApplicationTest() {
        this.ec = new HttpExecutionContext(ForkJoinPool.commonPool());
        this.productRepository = new EbeanProductRepository(ec);;
    }

    @Test
    public void checkThatProductFindAllIsNotEmpty() {
        try {
            assertTrue(!productRepository.findAll().toCompletableFuture().get().isEmpty());
        } catch (InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatTheSizeOfProductsIsGreaterThan3() {
        try {
            assertTrue(productRepository.findAll().toCompletableFuture().get().size() > 3);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatThereIsOneProductWithEANEqualsTo10000() {
        try {
            assertTrue(productRepository.findByEan("10000").toCompletableFuture().get() != null);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatThereIsNoProductWithEANEqualsTo999() {
        try {
            assertTrue(productRepository.findByEan("999").toCompletableFuture().get() == null);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatFindByNameWorks() {
        try {
            assertTrue(productRepository.findByName("Macbook").toCompletableFuture().get() != null);
            assertTrue(productRepository.findByName("Macbook").toCompletableFuture().get().size() == 2);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatSaveWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        Product myProduct = new Product(ean,name,description, null);

        try {
            assertTrue(productRepository.findByEan(ean).toCompletableFuture().get() == null);
            myProduct.save();
            assertTrue(productRepository.findByEan(ean).toCompletableFuture().get() != null);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert (false);
        }
    }

    @Test
    public void checkThatRemoveWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        Product myProduct = new Product(ean,name,description, null);

        try {
            assertTrue(productRepository.findByEan(ean).toCompletableFuture().get() == null);
            myProduct.save();
            assertTrue(productRepository.findByEan(ean).toCompletableFuture().get() != null);
            myProduct.delete();
            assertTrue(productRepository.findByEan(ean).toCompletableFuture().get() == null);
        } catch (java.lang.InterruptedException | ExecutionException e) {
            assert(false);
        }
    }
}
