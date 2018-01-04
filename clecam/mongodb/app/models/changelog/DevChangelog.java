package models.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@ChangeLog
public class DevChangelog {

    final play.Logger.ALogger logger = play.Logger.of(getClass());

    @ChangeSet(order = "001", id = "insertProducts", author = "INIT")
    public void insertProducts(DB db){
        logger.warn(db.toString());
        logger.warn(db.getCollectionNames().size()+"");
        DBCollection products = db.getCollection("products");
        BasicDBObject product1 = new BasicDBObject("ean", "a").append("name", "a").append("description", "a");
        BasicDBObject product2 = new BasicDBObject("ean", "b").append("name", "b").append("description", "b");
        products.insert(product1, product2);
    }
}
