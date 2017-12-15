package models;

import com.avaje.ebean.Ebean;
import play.data.validation.Constraints;
import java.util.*;
import java.util.stream.Collectors;

import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity
//@Table(name = "PRODUCTS")
public class Product extends Model {

    @Id
    @Constraints.Required
    public String ean;

    @Constraints.Required
    public String name;

    public String description;

    @Column(columnDefinition = "image")
    public byte[] picture;

    public String picturePath;

    //private static ArrayList<Product> products = init();

    public Product() {
    }

    public Product(String ean, String name, String description, byte[] picture, String picturePath) {
        this();

        this.ean = ean;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.picturePath = picturePath;
    }

    private static Finder<String, Product> find = new Finder<>(Product.class);

    public static List<Product> findAll() {
        List<Product> products = find.all();
        products.sort(Comparator.comparing(p -> p.name));
        return products;
    }

    public static Product findByEan(String ean) {
        return find.byId(ean);
    }

    public static List<Product> findByName(String name) {
        return find.where().eq("name", name).findList();
    }

    public static List<Product> findByQuery(String query) {
        HashMap<Product, Integer> map = new HashMap<>();

        String[] keywords = query.split(" ");

        for(String keyword : keywords) {
            find.where()
                    .disjunction()
                        .icontains("name", keyword)
                        .icontains("description", keyword)
                    .endJunction()
                    .findEach((Product product) -> {
                        Integer oldValue = map.putIfAbsent(product, 1);

                        if(oldValue != null)
                            map.replace(product, oldValue+1);
                    });
        }

        // On cherche à trier la map en fonction de la value pour avoir les meilleurs resultats en premier
        List<Map.Entry<Product, Integer>> list = new LinkedList<>(map.entrySet());
        return list.stream()
                .filter(o -> o.getValue()==keywords.length)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static void flushAll() {
        Ebean.deleteAll(findAll());
    }

    public void add() {
        this.insert();
    }

    public void modify() { this.save(); }

    public void remove() {
        this.delete();
    }
}
