package services;

import models.ImageSearchDatas;
import models.Product;
import models.ProductDAO;
import play.Logger;

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
        String term = imageSearchDatas.getSearchTerm();
        List<String> filesPath = imageSearchDatas.getImagesPaths();
        List<Product> results = this.findByCriteria("description", term);
        Iterator<Product> itProducts = results.listIterator();
        Iterator<String> itPaths = filesPath.listIterator();
        while (itProducts.hasNext() && itPaths.hasNext()) {
            Product p = itProducts.next();
            String imagePath = itPaths.next();
            Logger.info("---------------");
            Logger.info("imagePath = " + imagePath);
            p.setImagePath(imagePath);
            Logger.info("updateted : " + p.getName() + " -> " + p.getImagePath());
            productDAO.update(p);
            Logger.info("My updated Product : " + productDAO.findByEAN(p.getEan()));
            Logger.info("---------------");

            //TODO Look at zip function to iterate over the two lists
        }
        return results;
    }
}
