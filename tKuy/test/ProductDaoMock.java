import dao.ProductDao;
import hexagon.IProductDao;
import hexagon.Product;
import play.data.validation.Constraints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Cette classe a été créée au début lorsque l'on n'utilisait pas encore la base de données
 */
public class ProductDaoMock implements IProductDao {
    @Constraints.Required
    public long ean;
    @Constraints.Required
    public String name;
    public String description;
    public String picture;
    private static HashMap<Long, Product> products;
    static {
        products = new HashMap<>();
        products.put(12345L, new Product(12345, "Table en marbre", "Table rose en bois naturel"));
        products.put(12347L, new Product(12347, "Chaise en marbre", "Chaise en plastique naturel"));
        products.put(11111L, new Product(11111, "Chaise en plastique", "Chaise en plastique renforcé"));
    }
    public ProductDaoMock(long ean, String name, String description, String picture) {
        this.ean = ean;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }
    public ProductDaoMock(long ean, String name, String description) {
        this.ean = ean;
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "ean=" + ean +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }

    public ProductDaoMock() {}

    public ProductDaoMock(int ean, String name, String description) {
        this.ean = ean;
        this.name = name;
        this.description = description;
    }

    public String namePicture() {
        return picture==null ? "default-picture.png" : picture;
    }


    @Override
    public void delete(long id) {
        products.remove(id);
    }

    @Override
    public boolean create(Product product) {
        if(products.get(product.ean)!=null) {
            return false;
        }
        products.put(product.ean, product);
        return true;
    }
    @Override
    public List<Product> findAll() {
        ArrayList<Product> list = new ArrayList<>();
        list.addAll(products.values());
        return list;
    }
    @Override
    public List<Product> findByName(String name) {
        return products.values().stream().filter(p -> p.name.contains(name)).collect(Collectors.toList());
    }

    @Override
    public Optional<Product> findProductById(long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public void flushAll() {
        products.clear();
    }

    @Override
    public void update(Product product) {
        products.put(product.ean, product);
    }

    @Override
    public List<Product> paginated(int page, int pageSize, String sortBy, String filter) {
        return null;
    }

    @Override
    public List<Product> paginated(int page, int pageSize) {
        return null;
    }

    @Override
    public int numberOfProducts() {
        return 0;
    }
}
