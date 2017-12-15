import models.Product;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class ApplicationTest {

    @Test
    public void checkThatProductFindAllIsNotEmpty() {
        assertTrue(!Product.findAll().isEmpty());
    }

    @Test
    public void checkThatTheSizeOfProductsIsGreaterThan3() {
        assertTrue(Product.findAll().size() > 3);
    }

    @Test
    public void checkThatThereIsOneProductWithEANEqualsTo10000() {
        assertTrue(Product.findByEan("10000") != null);
    }

    @Test
    public void checkThatThereIsNoProductWithEANEqualsTo999() {
        assertTrue(Product.findByEan("999") == null);
    }

    @Test
    public void checkThatFindByNameWorks() {
        assertTrue(Product.findByName("Macbook") != null);
        assertTrue(Product.findByName("Macbook").size() == 2);
    }

    @Test
    public void checkThatSaveWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        Product myProduct = new Product(ean,name,description,null,null);

        assertTrue(Product.findByEan(ean) == null);
        myProduct.add();
        assertTrue(Product.findByEan(ean) != null);
    }

    @Test
    public void checkThatRemoveWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        Product myProduct = new Product(ean,name,description,null,null);

        assertTrue(Product.findByEan(ean) == null);
        myProduct.add();
        assertTrue(Product.findByEan(ean) != null);
        myProduct.remove();
        assertTrue(Product.findByEan(ean) == null);
    }
}
