package models;

import play.data.validation.Constraints;

public class Product {

    @Constraints.Required
    public String ean;

    @Constraints.Required
    public String name;

    public String description;

    public String imagePath;

    public Product() {
    }

    /* public List<Product> findAll(){
         return productDAO.findAll();
     }

     public Optional<Product> findByEAN(String ean){
         return productDAO.findByEAN(ean);
     }

     public List<Product> findByName(String name){
        return productDAO.findByName(name);
     }

     public Product create(){
         return productDAO.create(this);
     }

     public Product update(){
         return productDAO.update(this);
     }

     public void delete(){
         productDAO.delete(this);
     }
 */
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
        return "Product{" +
                "ean='" + ean + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
