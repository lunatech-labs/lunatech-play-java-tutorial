package models;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTest {

//    @Test
//    public void checkThisProductFindAllIsNotEmpty() {
//        assertThat(Product.findAll().isEmpty()).isFalse();
//    }
//
//    @Test
//    public void checkThatTheSizeOfProductsIsGreaterThan3() {
//        assertThat(Product.findAll().size()).isGreaterThan(3);
//    }
//
//    @Test
//    public void checkThatTHereIsOneProductWithEANEqualsTo10000() {
//        assertThat(Product.findByEan("10000")).isPresent();
//    }
//
//    @Test
//    public void checkThatTHereIsOneProductWithEANEqualsNotTo999() {
//        assertThat(Product.findByEan("999")).isNotPresent();
//    }
//
//    @Test
//    public void checkThatFindByNameWorks() {
//        assertThat(Product.findByName("Macbook")).isNotNull();
//        assertThat(Product.findByName("Macbook")).hasSize(2);
//    }
//
//    @Test
//    public void checkThatSaveWorks() {
//        String ean = "123456789";
//        String name = "Super product ";
//        String description = "one description";
//
//        Product myProduct = new Product(ean, name, description);
//
//        assertThat(Product.findByEan(ean)).isNotPresent();
//        myProduct.save(new Product(0, oneRow[0], oneRow[1]));
//        assertThat(Product.findByEan(ean)).isPresent();
//    }
//
//    @Test
//    public void checkThatRemoveWorks() {
//        String ean = "123456789";
//        String name = "Super product ";
//        String description = "one description";
//
//        Product myProduct = new Product(ean, name, description);
//
//        assertThat(Product.findByEan(ean)).isNotPresent();
//        myProduct.save(new Product(0, oneRow[0], oneRow[1]));
//        assertThat(Product.findByEan(ean)).isPresent();
//        myProduct.remove();
//        assertThat(Product.findByEan(ean)).isNotPresent();
//    }
}
