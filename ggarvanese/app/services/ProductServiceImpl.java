package services;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Streams;
import models.ImageSearchDatas;
import models.Product;
import models.ProductDAO;

import javax.inject.Inject;
import java.util.*;

public class ProductServiceImpl implements ProductService {

    @Inject
    private ProductDAO productDAO;

    @Inject
    public ProductServiceImpl(ProductDAO product) {
        this.productDAO = product;
    }

    @Override
    public List<Product> findAll() {
        return productDAO.findAll();
    }

    @Override
    public Optional<Product> findByEAN(String ean) {
        return productDAO.findByEAN(ean);
    }

    @Override
    public List<Product> findByCriteria(String type, String criteria) {
        if (type.equals("name")) {
            return productDAO.findByName(criteria);
        }
        if (type.equals("description")) {
            return productDAO.findByDescription(criteria);
        }
        return new ArrayList<>();
    }

    @Override
    public Product create(Product product) {
        product.setEan(UUID.randomUUID().toString());
        return productDAO.create(product);
    }

    @Override
    public Product update(Product product) {
        return productDAO.update(product);
    }

    @Override
    public void delete(Product product) {
        productDAO.delete(product);
    }

    @Override
    public List<Product> updateProducts(ImageSearchDatas imageSearchDatas) {
        List<String> filesPath = imageSearchDatas.getImagesPaths();
        List<Product> products = imageSearchDatas.getProductsToUpdate();
        ImmutableListMultimap<String, Product> results = Streams.zip(filesPath.stream(), products.stream(), AbstractMap.SimpleImmutableEntry::new)
                .collect(ImmutableListMultimap.toImmutableListMultimap(Map.Entry::getKey, Map.Entry::getValue));
        results.forEach((path, product) -> {
            product.setImagePath(path);
            productDAO.update(product);
        });
        return new ArrayList<>(results.values());
    }
}
