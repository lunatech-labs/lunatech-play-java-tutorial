package repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.mongodb.morphia.Key;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexType;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import javax.inject.Singleton;
import javax.inject.Inject;
import com.univocity.parsers.csv.*;
import play.libs.concurrent.HttpExecutionContext;
import models.Product;

@Singleton
public class ProductDAO extends BasicDAO<Product,ObjectId>  {

    private play.Logger.ALogger logger = play.Logger.of(getClass());
    private final HttpExecutionContext ec;

    @Inject
    public ProductDAO(Datastore ds, DatabaseExecutionContext ec) {

	    super(Product.class, ds);
        this.ec = new HttpExecutionContext(ec);
        logger.warn("injected ec="+ec.toString());
    }

    public CompletionStage<String> eraseAll() {

        return CompletableFuture.supplyAsync(() -> {
            findAll().thenAccept(lProducts -> {
                deleteByQuery(getDatastore().createQuery(Product.class));
                });
                return "success";
        }, ec.current());
    }

    public  CompletionStage<String> loadCsv(String fileRelativePath) {

        return CompletableFuture.supplyAsync(() -> {
                    CsvParserSettings settings = new CsvParserSettings();
                    settings.setHeaderExtractionEnabled(true);
                    CsvParser parser = new CsvParser(settings);

                    try {
                        parser.beginParsing(new java.io.FileReader(fileRelativePath));
                        String[] l = parser.parseNext();
                        while (l != null) {
                            // ean, name, description, picture
                            Product p = new Product(l[0], l[0], l[1], null);
                            save(p);
                            l = parser.parseNext();
                        }
                    } catch (Exception e) {
                        logger.error("mongdb insert error", e);
                        return "error";
                    }
                    return "success";
        }, ec.current());
    }

    public CompletionStage<List<Product>> findAll() {

        return CompletableFuture.supplyAsync(() -> {
            return getDatastore().createQuery(Product.class).order("id").asList();
            //return find.query().orderBy("id asc").findList();
        }, ec.current());
    }

    public CompletionStage<Product> findById(String hexId) {

        return CompletableFuture.supplyAsync(() -> {
            // FIXME: use findOneId() instead
            List<Product> l = getDatastore().createQuery(Product.class).filter("id =", new ObjectId(hexId)).asList();
            //Key key = super.findOneId("id", id);
            //return key.getId();
            if (l.isEmpty()) return null;
            return l.get(0);
        }, ec.current());
    }

    public CompletionStage<Product> findByEan(String ean) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> l = getDatastore().createQuery(Product.class).filter("ean =", ean).order("ean").asList();
            if (l.isEmpty()) return null;
            return l.get(0);
        }, ec.current());
    }

    public CompletionStage<List<Product>> findByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> l = getDatastore().createQuery(Product.class).filter("name =", name).order("ean").asList();
            if (l.isEmpty()) return null;
            return l;
        }, ec.current());
    }

    public CompletionStage<List<Product>> findByDescription(String description) {
        logger.warn(description);
        return CompletableFuture.supplyAsync(() -> {
            List<Product> l = getDatastore().createQuery(Product.class).filter("description =", description).order("ean").asList();
            logger.warn("size="+l.size());
            return l;
        }, ec.current());
    }
}

