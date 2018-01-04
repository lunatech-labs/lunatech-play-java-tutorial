package models.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import models.Product;
import org.mongodb.morphia.Datastore;

import javax.inject.Inject;
import play.inject.guice.GuiceApplicationBuilder;
import play.Configuration;
import repository.ProductDAO;

@ChangeLog
public class TestChangelog {

    final play.Logger.ALogger logger = play.Logger.of(getClass());
    final ProductDAO productDao;
    //final Datastore datastore;

    public TestChangelog() {
        GuiceApplicationBuilder application = new GuiceApplicationBuilder();
        Configuration configuration = application.build().configuration();
        //this.datastore = application.injector().instanceOf(Datastore.class);
        this.productDao = application.injector().instanceOf(ProductDAO.class);
    }

    @ChangeSet(order = "001", id = "insertProducts", author = "INIT")
    public void insertProducts(DB db){

        DBCollection products = db.getCollection("products");
        Product product = new Product("AGEN", "AGEN", "a", null);
        Product product2 = new Product("AGEN", "AGEN", "b", null);
        Product product3 = new Product("ABSORB", "ABSORB", "c", null);
        productDao.save(product);
        productDao.save(product2);
        productDao.save(product3);
/*
        BasicDBObject product1 = new BasicDBObject("ean", "AGEN").append("name", "AGEN").append("description", "a");
        BasicDBObject product2 = new BasicDBObject("ean", "AGEN").append("name", "AGEN").append("description", "b");
        BasicDBObject product3 = new BasicDBObject("ean", "ABSORB").append("name", "ABSORB").append("description", "b");
        products.insert(product1, product2, product3);
 */
    }
}
