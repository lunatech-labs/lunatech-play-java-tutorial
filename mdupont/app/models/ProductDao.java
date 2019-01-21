package models;

import java.util.Collection;
import java.util.List;

public interface ProductDao {
    List<Product> findAll();
    Product findByEan(long ean);
    List<Product> findByName(String name);
    void save(Product product);

    void update(Product product);

    void delete(Product product);

    List<Product> findPage(int page, int length, String description);

    int count(String description);

    List<Product> findByDescription(String description);

    List<Product> findByDescriptionAndUrlIsEmpty(String searchTerm);
}
