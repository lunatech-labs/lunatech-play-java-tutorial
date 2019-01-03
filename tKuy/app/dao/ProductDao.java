package dao;

import hexagon.IProductDao;
import io.ebean.Ebean;
import io.ebean.Finder;
import hexagon.Product;
import io.ebean.Model;

import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
@Singleton
public class ProductDao extends Model implements IProductDao {

    private static final Finder<Long, Product> find = new Finder<>(Product.class);

    @Override
    public Optional<Product> findProductById(long id) {
        Product product = find.byId(id);
        return Optional.ofNullable(product);
    }
    @Override
    public void flushAll() {
//        find.nativeSql("TRUNCATE TABLE Product;");
        findAll().forEach(Model::delete);
    }

    @Override
    public void update(Product product) {
        product.update();
    }

    @Override
    public List<Product> paginated(int page, int pageSize, String orderBy, String filter) {
        return find.query().where().ilike("name", "%"+filter+"%")
                .orderBy(orderBy)
                .setFirstRow((page-1)*pageSize)
                .setMaxRows(page*pageSize)
                .findPagedList()
                .getList();
    }

    @Override
    public List<Product> paginated(int page, int pageSize) {
        return find.query().where()
                .setFirstRow(page*(pageSize)-pageSize)
                .setMaxRows(pageSize)
                .findPagedList()
                .getList();
    }

    @Override
    public int numberOfProducts() {
        return find.query().findCount();
    }

    @Override
    public void delete(long id) {
        find.deleteById(id);
    }

    @Override
    public boolean create(Product product) {
        if(findProductById(product.ean).isPresent()) {
            return false;
        }
        product.save();
        return true;
    }
    @Override
    public List<Product> findAll() {
        return find.all();
    }
    @Override
    public List<Product> findByName(String name) {
        return find.query().where().ilike("name", "%"+name+"%").setFirstRow(0).setMaxRows(15)
                .findPagedList().getList();
    }

}
