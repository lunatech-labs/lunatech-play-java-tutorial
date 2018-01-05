package repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.*;

import javax.inject.Inject;
import javax.persistence.*;
import com.univocity.parsers.csv.*;
//import play.db.ebean.EbeanConfig;
import play.libs.concurrent.HttpExecutionContext;
import models.Product;

@CacheStrategy(readOnly=true)
@Entity
public class EbeanProductRepository implements ProductRepository {

    private play.Logger.ALogger logger = play.Logger.of(getClass());
    private final Finder<Long, Product> find =new Finder<Long, Product>(Product.class);
    //private final EbeanServer ebeanServer;


    private final HttpExecutionContext ec;

    /*
    @Inject
    public EbeanProductRepository(HttpExecutionContext ec) {
        this.ec = ec;
        logger.warn("injected ec="+getEc().toString());
    }
    */

    //private final DatabaseExecutionContext ec;

    @Inject
    public EbeanProductRepository(DatabaseExecutionContext ec) {
        this.ec = new HttpExecutionContext(ec);
        logger.warn("injected ec="+ec.toString());
    }


    //public EbeanProductRepository(DatabaseExecutionContext ec, EbeanConfig ebeanConfig) {
    //    this.ec = ec;
    //    this.ebeanServer = Ebean.getServer(ebeanConfig.defaultServer());
    //    System.out.println(ebeanServer);
    //}

    @Override
    public CompletionStage<String> eraseAll() {
        return CompletableFuture.supplyAsync(() -> {
            //Transaction tx = Ebean.beginTransaction();
            try {
                Ebean.beginTransaction();
                findAll().thenAccept(lProducts -> {
                    Ebean.deleteAll(lProducts);

                });
                Ebean.commitTransaction();
               //find.query().delete();
                //tx.commit();
                //Ebean.find(Product.class).delete();
                //ebeanServer.find(Product.class).delete();
                return "success";
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return "error";
            }
            finally {
                Ebean.endTransaction();
                //tx.end();
            }
        }, ec.current());
    }

    @Override
    public  CompletionStage<String> loadCsv(String fileRelativePath) {
        return CompletableFuture.supplyAsync(() -> {
            CsvParserSettings settings = new CsvParserSettings();
            settings.setHeaderExtractionEnabled(true);
            CsvParser parser = new CsvParser(settings);
            Transaction tx = Ebean.beginTransaction();
            try {
                tx.setBatchMode(true);
                tx.setBatchSize(100);
                boolean inserts = true;
                boolean updates = false;
                boolean deletes = false;
                tx.addModification("product", inserts, updates, deletes);
                parser.beginParsing(new java.io.FileReader(fileRelativePath));
                String[] l = parser.parseNext();
                while (l != null) {
                    // ean, name, description, picture
                    Product p = new Product(l[0], l[0], l[1], null);
                    p.save();
                    l = parser.parseNext();
                }
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
            finally {
                tx.end();
                tx.setBatchMode(false);
            }
            return "success";
        }, ec.current());
    }

    @Override
    public CompletionStage<List<Product>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            return find.query().orderBy("id asc").findList();
            //return ebeanServer.find(Product.class).orderBy("id asc").findList();
            //return Ebean.find(Product.class).orderBy("id asc").findList();
        }, ec.current());
    }

    @Override
    public CompletionStage<Product> findById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> l = find.query().where().eq("id", id).findList();
            //List<Product> l = ebeanServer.find(Product.class).where().eq("id", id).findList();
            //List<Product> l = Ebean.find(Product.class).where().eq("id", id).findList();
            if (l.isEmpty()) return null;
            return l.get(0);
        }, ec.current());
    }

    @Override
    public CompletionStage<Product> findByEan(String ean) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> l = find.query().where().eq("ean", ean).findList();
            //List<Product> l = ebeanServer.find(Product.class).where().eq("ean", ean).findList();
            //List<Product> l = Ebean.find(Product.class).where().eq("ean", ean).findList();
            //List<Product> l = find.setUseCache(true).where().eq("ean", ean).findList();
            if (l.isEmpty()) return null;
            return l.get(0);
        }, ec.current());
    }

    @Override
    public CompletionStage<List<Product>> findByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> l = find.query().where().eq("name", name).findList();
            //List<Product> l = ebeanServer.find(Product.class).where().eq("name", name).findList();
            //List<Product> l = Ebean.find(Product.class).where().eq("name", name).findList();
            //List<Product> l = findAll().stream().filter(p -> p.name.compareTo(name) == 0).collect(Collectors.toList());
            if (l.isEmpty()) return null;
            return l;
        }, ec.current());
    }

    public CompletionStage<List<Product>> findByDescription(String description) {
        return CompletableFuture.supplyAsync(() -> {
            List<Product> l = find.query().where().eq("description", description).findList();
            //List<Product> l = Ebean.find(Product.class).where().eq("description", description).findList();
            return l;
        }, ec.current());

    }
}

