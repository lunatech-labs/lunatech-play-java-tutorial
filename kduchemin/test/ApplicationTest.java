import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import models.Product;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


public class ApplicationTest {

    @Test
    public void checkThatProductFindAllIsNotEmpty() {
        assertTrue(Product.findAll().isEmpty() == false);
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

        Product myProduct = new Product(ean,name,description);

        assertTrue(Product.findByEan(ean) == null);
        myProduct.save();
        assertTrue(Product.findByEan(ean) != null);
    }

    @Test
    public void checkThatRemoveWorks() {
        String ean = "12333456623456";
        String name= "One unit super test";
        String description = "bla bla bla";

        Product myProduct = new Product(ean,name,description);

        assertTrue(Product.findByEan(ean) == null);
        myProduct.save();
        assertTrue(Product.findByEan(ean) != null);
        myProduct.remove();
        assertTrue(Product.findByEan(ean) == null);
    }
}