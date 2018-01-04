package models;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import play.data.validation.Constraints.Required;
import org.mongodb.morphia.utils.IndexType;

@Entity(value="products", noClassnameStored = true)
public class Product {

    @Id
    public ObjectId id;
    @Required
    public String ean;
    public String name;
    public String description;
    //@Lob
    public byte[] picture;

    public Product() {
        ean = null;
        name = null;
        description = null;
        picture = null;
    }


    public Product(String ean, String name, String description, byte[] picture) {
        this.ean = ean;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public String toString() {
        return "id:"+id + "\tean:"+ean + "\t\tname:"+name + "\t\tdescription:"+description + "\t\tpicture:"+picture;
    }
}

