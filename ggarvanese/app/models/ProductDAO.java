package models;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    List<Product> findAll();

    Optional<Product> findByEAN(String ean);

    List<Product> findByName(String name);

    Product create(Product product);

    Product update(Product product);

    void delete(Product product);

    List<Product> findByDescription(String description);
}
