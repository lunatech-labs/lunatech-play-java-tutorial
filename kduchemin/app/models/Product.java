package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Required;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;

@Entity
public class Product extends Model {

    @Id
    @Constraints.Min(10)
    public Long id;

    @Required

    @Column(unique = true)
    public String ean;

    public String name;
    public String description;

    @Column(columnDefinition = "Image")
    public byte[] picture;

    public String pathLocalPicture;
    private static Finder<Long, Product> find = new Finder<>(Product.class);

    public Product() {
    }

    public Product(String ean, String name, String description, byte[] picture) {
        this.ean = ean;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public String toString() {
        return String.format("%s - %s", ean, name);
    }

    public static List<Product> findAll() {
        return find.all();
    }

    public static List<Product> findAllShuffleVersion() {
        List<Product> list = find.all();
        Collections.shuffle(list);
        return list;
    }

    public static Product findByEan(String ean) {

        return find.where().eq("ean", ean).findUnique();
    }

    public static List<Product> findByName(String term) {

        return find.where().contains("name", term).findList();
    }

    public static void flushAll() {
        for (Product p : findAll()) {
            find.deleteById(p.id);
        }
    }

    public void remove() {
        final Product product = findByEan(this.ean);
        find.byId(product.id).delete();
    }

    public static LinkedList<String> allProductsDescriptionsSortedByOccurenceDesc() {
        // key: occ, value : product descriptions
        TreeMap<Long, HashSet<String>> productsDescriptionsByOcc = new TreeMap<>();
        HashSet<String> allDescriptions = new HashSet<>();

        Product.findAll().stream().map(product -> product.description)
                .filter(description -> description!= null)
                .distinct()
                .forEach(allDescriptions::add);

        for (String description : allDescriptions) {
            Long key = Product.findAll().stream().filter(p -> p.description!= null)
                    .filter(product -> product
                                .description
                                .equals(description))
                    .count();

            HashSet<String> productsDescription = productsDescriptionsByOcc.getOrDefault(key, new HashSet<>());
            productsDescription.add(description);

            productsDescriptionsByOcc.put(key, productsDescription);
        }

        LinkedList<String> allProductsDescriptionsSortedByOccurenceDesc = new LinkedList<>();

        productsDescriptionsByOcc
                .descendingKeySet()
                .forEach(key -> {
                            allProductsDescriptionsSortedByOccurenceDesc
                                    .addAll(productsDescriptionsByOcc.get(key));
                    allProductsDescriptionsSortedByOccurenceDesc.add(key.toString());
                        }
                );

        return allProductsDescriptionsSortedByOccurenceDesc;
    }
}
