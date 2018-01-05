package models;


public class ImageContent {

 byte[] content = null;
 String contentType = null;

 public ImageContent(byte[] content, String contentType) {
  this.content = content;
  this.contentType = contentType;
 }

 public String getContentType() {
  return contentType;
 }

 public byte[] getContent() {
  return content;
 }
}
