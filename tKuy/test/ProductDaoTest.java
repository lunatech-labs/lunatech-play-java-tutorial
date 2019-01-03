
import dao.ProductDao;
import hexagon.Product;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static play.inject.Bindings.bind;


public class ProductDaoTest {

    private final ProductDaoMock productDaoMock = new ProductDaoMock();

    @Test
    public void getProductById() {
        System.out.println(productDaoMock.create(new Product(0L,"q", "a")));
        assertTrue(productDaoMock.findProductById(0L).isPresent());
    }
    @Test
    public void getProductByIdShouldFail() {
        assertTrue(!productDaoMock.findProductById(123457).isPresent());
    }

    @Test
    public void modifyDescription() {
        String description = "Table en papier recyclé";
        Optional<Product> opt = productDaoMock.findProductById(12345L);
        opt.ifPresent(x-> {
            x.description = description;
            productDaoMock.create(x);
        });
        assertTrue(productDaoMock.findProductById(12345L).get().description.equals(description));

    }
    @Test
    public void delete() {
        Product product = new Product(111234L, "a", "a");
        productDaoMock.create(product);
        productDaoMock.delete(111234L);
        assertFalse(productDaoMock.findProductById(111234L).isPresent());
    }
    @Test
    public void shouldNotAddExistingProduct() {
        Product product = new Product(000, "Coussin", "En plume de paon");
        productDaoMock.create(product);
        assertFalse(productDaoMock.create(product));
    }
    @Test
    public void findAllShouldNotBeEmpty() {
        assertFalse(productDaoMock.findAll().isEmpty());
    }
    @Test
    public void findByName() {
        Product product = new Product(000, "Coussin", "En plume de paon");
        productDaoMock.create(product);
        productDaoMock.findByName("coussin").contains(product);
    }

}
