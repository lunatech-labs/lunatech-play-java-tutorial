package persistence;

import io.ebean.Finder;
import models.Product;
import models.ProductDAO;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class ProductH2EbeanDAO implements ProductDAO {

    private static final Finder<Long, ProductEntity> find = new Finder<>(ProductEntity.class);

    @Override
    public List<Product> findAll() {
        List<ProductEntity> entities = find.all();
        return mapToProductList(entities);
    }

    @Override
    public Optional<Product> findByEAN(String ean) {
        Optional<ProductEntity> entity = find.query().where().eq("ean", ean).findOneOrEmpty();
        return entity.map(this::mapToProduct);
    }

    @Override
    public List<Product> findByName(String name) {
        List<ProductEntity> entities = find.query().where().startsWith("name", name).findList();
        return mapToProductList(entities);
    }

    @Override
    public Product create(Product product) {
        ProductEntity entity = maptoEntity(product);
        entity.save();
        return mapToProduct(entity);
    }

    @Override
    public Product update(Product product) {
        Optional<ProductEntity> found = find.query().where().eq("ean", product.getEan()).findOneOrEmpty();
        if (found.isPresent()) {
            found.get().setName(product.getName());
            found.get().setDescription(product.getDescription());
            found.get().setImagePath(product.getImagePath());
            found.get().update();
        }
        return null;
    }

    @Override
    public void delete(Product product) {
        Optional<ProductEntity> found = find.query().where().eq("ean", product.getEan()).findOneOrEmpty();
        if (found.isPresent()) {
            found.get().delete();
        }
    }

    @Override
    public List<Product> findByDescription(String description) {
        List<ProductEntity> entities = find.query().where().contains("description", description).findList();
        return mapToProductList(entities);
    }

    private Product mapToProduct(ProductEntity entity) {
        Product product = new Product();
        product.setEan(entity.getEan());
        product.setName(entity.getName());
        product.setDescription(entity.getDescription());
        product.setImagePath(entity.getImagePath());
        return product;
    }

    private List<Product> mapToProductList(List<ProductEntity> entities) {
        List<Product> products = new ArrayList<>();
        for (ProductEntity entity : entities) {
            Product product = new Product();
            product.setEan(entity.getEan());
            product.setName(entity.getName());
            product.setDescription(entity.getDescription());
            product.setImagePath(entity.getImagePath());
            products.add(product);
        }
        return products;
    }

    private ProductEntity maptoEntity(Product product) {
        return new ProductEntity(product.getEan(), product.getName(), product.getDescription(), product.getImagePath());
    }
}
