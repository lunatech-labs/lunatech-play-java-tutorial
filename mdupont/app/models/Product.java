package models;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

public class Product {

    private long ean;
    private String name;
    private String description;
    private String url;

    @Inject
    private ProductDao productDao;

    public Product() {
        // Default Ctor for Inject
    }

    public Product(long ean, String name, String description, String url) {
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

    public void save(Product product) {
        this.productDao.save(product);
    }

    public void delete(Product product) {
        this.productDao.delete(product);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return ean == product.ean &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                Objects.equals(url, product.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ean, name, description, url);
    }

    @Override
    public String toString() {
        return "Product{" +
                "ean=" + ean +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", productDao=" + productDao +
                '}';
    }
}
