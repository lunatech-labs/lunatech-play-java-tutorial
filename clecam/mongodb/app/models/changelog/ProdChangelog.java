package models.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.DB;

@ChangeLog
public class ProdChangelog {

    @ChangeSet(order="001",id="dummyChangeSet",author = "INIT")
    public void dummyChangeSet(DB db){
        // example
    }
}
