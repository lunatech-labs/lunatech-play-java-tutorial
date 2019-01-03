package hexagon;

import java.util.List;
import java.util.Optional;

public interface IProductDao {
    void delete(long id);
    boolean create(Product product);
    List<Product> findAll();
    List<Product> findByName(String name);
    Optional<Product> findProductById(long id);
    void flushAll();
    void update(Product product);
    List<Product> paginated(int page, int pageSize, String sortBy, String filter);
    List<Product> paginated(int page, int pageSize);
    int numberOfProducts();
}
