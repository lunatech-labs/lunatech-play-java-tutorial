package hexagon;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Product extends Model {
    @Id
    @Constraints.Required
    public long ean;
    @Constraints.Required
    public String name;
    public String description;
    public String picture;

    public String namePicture() {
        return (picture==null ? "default-picture.png" : picture);
    }
    public Product() {
    }
    public Product(long ean, String name, String description, String picture) {
        this.ean = ean;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }
    public Product(long ean, String name, String description) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return ean == product.ean &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                Objects.equals(picture, product.picture);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ean);
    }
}
