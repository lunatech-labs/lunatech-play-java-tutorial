package persistence;

import com.google.inject.Singleton;
import io.ebean.Finder;
import models.Product;
import models.ProductDao;
import play.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class ProductDaoImpl implements ProductDao {

    public static final Finder<Long, ProductEntity> find = new Finder<>(ProductEntity.class);

    public ProductDaoImpl() {
        // Default Ctor;
    }

    @Override
    public List<Product> findAll() {
        return find.all().stream().map(productEntity -> productEntity.toProduct()).collect(Collectors.toList());
    }

    @Override
    public Product findByEan(long ean) {

        return find.query().where()
                .eq("ean", ean)
                .orderBy("ean")
                .findOne().toProduct();
    }

    @Override
    public List<Product> findByName(String name) {
        return find.query().where()
                .eq("name", name)
                .orderBy("name")
                .findList().stream().map(productEntity -> productEntity.toProduct()).collect(Collectors.toList());
    }

    @Override
    public void save(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setUrl(product.getUrl());

        Logger.debug("Proudct saved " + product.toString());
        entity.save();
    }

    @Override
    public void delete(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.setEan(product.getEan());

        entity.delete();
    }

    @Override
    public List<Product> findPage(int page, int length) {
        return find.query()
                .where()
                .setFirstRow(page * length)
                .setMaxRows(length)
                .findList()
                .stream()
                .map(productEntity -> productEntity.toProduct()).collect(Collectors.toList());

    }

    @Override
    public int count() {
        return find.query().where().findCount();
    }

    @Override
    public List<Product> findByDescription(String description) {
        return find
                .query()
                .where()
                .like("description", description)
                .findList()
                .stream().map(productEntity -> productEntity.toProduct()).collect(Collectors.toList());
    }
}
