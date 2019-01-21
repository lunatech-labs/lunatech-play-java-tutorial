package services;

import models.ImageSearchDatas;
import models.Product;
import play.mvc.Result;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> findAll();

    Optional<Product> findByEAN(String ean);

    List<Product> findByCriteria(String type, String criteria);

    Product create(Product product);

    Product update(Product product);

    void delete(Product product);

    List<Product> updateProducts(ImageSearchDatas imageSearchDatas);
}
