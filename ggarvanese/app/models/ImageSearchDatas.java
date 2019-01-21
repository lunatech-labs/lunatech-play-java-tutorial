package models;

import java.util.List;
import java.util.Optional;

public class ImageSearchDatas {

    private String searchTerm;

    public Optional<String> getUrlsFilePath() {
        return urlsFilePath;
    }

    public void setUrlsFilePath(Optional<String> urlsFilePath) {
        this.urlsFilePath = urlsFilePath;
    }

    private Optional<String> urlsFilePath;

    private List<String> imagesPaths;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }


    public List<String> getImagesPaths() {
        return imagesPaths;
    }

    public void setImagesPaths(List<String> imagesPaths) {
        this.imagesPaths = imagesPaths;
    }

    public ImageSearchDatas() {
    }
}
