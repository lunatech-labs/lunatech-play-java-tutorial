package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BingResult {
    private int status;
    private String message;
    private java.util.List<JsonNode> urls;


    public BingResult() {
        urls = new java.util.LinkedList<JsonNode>();
    }

    public BingResult(int status, String message, java.util.List<JsonNode> urls) {
        this.status = status;
        this.message = message;
        this.urls = urls;
    }

    /*
     * Request status
     */
    public int getStatus() { return status; }

    /*
     * Message if status != 200
     */
    public String getMessage() { return message; }

    /*
     * Urls list
     */
    public java.util.List<JsonNode> getUrls(){ return urls; }

    public void setStatus(int status) { this.status = status; }

    public void setMessage(String message) { this.message = message; }

    public void setUrls(java.util.List<JsonNode> urls) { this.urls = urls; }

    /*
     * Extract urls
     */
    public java.util.Map<String, java.util.List<JsonNode>> extractUrls() {
        java.util.Map<String, java.util.List<JsonNode>> h = new java.util.HashMap<String, java.util.List<JsonNode>>();
        h.put("url", getUrls());
        return h;
    }

    public String urlsToJson() {
        String jsonString = null;
        try {
            jsonString = (new ObjectMapper()).writeValueAsString(extractUrls());
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
        }
        return jsonString;
    }
}