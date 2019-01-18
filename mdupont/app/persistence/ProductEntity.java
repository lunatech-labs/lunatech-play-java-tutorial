package persistence;

import io.ebean.Model;
import models.Product;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ProductEntity extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long ean;

    @Constraints.Required
    private String name;

    @Constraints.Required
    private String description;

    private String url;

    public ProductEntity() {
        // Default ctor.
    }

    public ProductEntity(long ean, String name, String description, String url) {
        this.ean = ean;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public long getEan() {
        return ean;
    }

    public void setEan(long ean) {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Product toProduct() {
        return new Product(this.getEan(), this.getName(), this.getDescription(), this.getUrl());
    }
}
