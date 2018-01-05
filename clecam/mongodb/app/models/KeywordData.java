package models;

import models.Product;
import play.data.validation.Constraints;

import javax.validation.Constraint;

public class KeywordData {

 @Constraints.Required
 private String keyword;

 public KeywordData() {
  keyword = "";
 }

 public KeywordData(String keyword) {
  this.keyword = keyword;
 }

 public String getKeyword() { return keyword; }

 public void setKeyword(String keyword) { this.keyword = keyword; }

}