package models;

import java.util.List;

public interface ProductDao {
    List<Product> findAll();
    Product findByEan(long ean);
    List<Product> findByName(String name);
    void save(Product product);
    void delete(Product product);

    List<Product> findPage(int page, int length);

    int count();

    List<Product> findByDescription(String description);
}
