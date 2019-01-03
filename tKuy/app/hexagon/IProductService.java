package hexagon;

import play.data.Form;
import play.mvc.Http;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IProductService {
    boolean createProduct(Form<Product> form, Http.Request request);
    void loadSample();
    void generateMissingPictures();
    List<Product> allProducts();
    Optional<Product> productById(long ean);
    void deleteProduct(long ean);
    List<Product> productListByPage(int page, int pageSize);
    int numberOfPages(int pageSize);
    Product generateMissingPicture(Product product);
    void updateProduct(Product product);
}
