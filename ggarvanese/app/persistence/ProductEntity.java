package persistence;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProductEntity extends Model {

    @Id
    private Long id;

    private String ean;

    private String name;

    private String description;

    private String imagePath;

    public ProductEntity(String ean, String name, String description, String imagePath) {
        this.ean = ean;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    public ProductEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + id +
                ", ean='" + ean + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
