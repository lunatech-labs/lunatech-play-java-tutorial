//import models.Product;
//import org.junit.Test;
//import services.ProductServiceImpl;
//
//import javax.inject.Inject;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class ProductTest {
//
//    @Inject
//    ProductServiceImpl productService;
//
//    @Test
//    public void checkThatProductFindIsNotEmpty(){
//        assertThat(productService.findAll().isEmpty()).isFalse();
//    }
//
//    @Test
//    public void checkThatTheSizeOfProductsIsGreaterThan3(){
//        assertThat(productService.findAll().size()).isGreaterThan(3);
//    }
//
//    @Test
//    public void checkThereIsOneProductWithEANEqualsTo1000(){
//        assertThat(productService.findByEAN("1000")).isPresent();
//    }
//
//    @Test
//    public void checkThereIsOneProductWithEANEqualsTo999(){
//        assertThat(productService.findByEAN("999")).isNotPresent();
//    }
//
//    @Test
//    public void checkThatFindByNameWorks(){
//    //    assertThat(productService.findByName("MacBook")).isNotNull();
//    }
//
//    @Test
//    public void checkThatSaveWorks(){
//        String ean = "1234567890";
//        String name = "One unit super test";
//        String description = "This is a description";
//
//        Product myProduct = new Product(ean, name, description);
//
//        assertThat(productService.findByEAN(ean)).isNotPresent();
//        productService.save();
//        assertThat(Product.findByEAN(ean)).isPresent();
//    }
//
//    @Test
//    public void checkThatRemoveWorks(){
//        String ean = "1234567890";
//        String name = "One unit super test";
//        String description = "This is a description";
//
//        Product myProduct = new Product(ean, name, description);
//
//        assertThat(Product.findByEAN(ean)).isNotPresent();
//        myProduct.save();
//        assertThat(Product.findByEAN(ean)).isPresent();
//        myProduct.remove();
//        assertThat(Product.findByEAN(ean)).isNotPresent();
//    }
//
//}
