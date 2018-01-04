package models;

import models.Product;
import play.data.validation.Constraints;

import javax.validation.Constraint;

public class ProductData {

 @Constraints.Required
 private String name;
 @Constraints.Required
 private String ean;
 private String description;
 private byte[] picture;

 public ProductData() {}

 public ProductData(Product product) {
  setName(product.name);
  setEan(product.ean);
  setDescription(product.description);
  setPicture(product.picture);
 }

 public String getName() { return name; }
 public String getEan() { return ean; }
 public String getDescription() { return description; }
 public byte[] getPicture() { return picture; }

 public void setName(String name) { this.name = name; }
 public void setEan(String ean) { this.ean = ean; }
 public void setDescription(String description) { this.description = description; }
 public void setPicture(byte[] picture) { this.picture = picture; }

}
