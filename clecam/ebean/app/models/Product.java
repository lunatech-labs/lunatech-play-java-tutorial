package models;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.*;

import javax.persistence.*;

@Entity
public class Product extends Model {

    @Id
    public Long id;
    public String ean;
    public String name;
    public String description;
    @Lob
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

    public void saveProduct() {
        // if we modify ean, a new product is created (like a copy)

        // check if product already exists
        if (id == null)
            this.save();
        else
            this.update();
    }

    @Override
    public String toString() {
        return "id:"+id + "\tean:"+ean + "\t\tname:"+name + "\t\tdescription:"+description + "\t\tpicture:"+picture;
    }
}

