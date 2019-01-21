package models;

import java.util.List;
import java.util.Optional;

public class ImageSearchDatas {

    private String searchTerm;

    private int numberOfResults;

    private Optional<String> urlsFilePath;

    private List<String> imagesPaths;

    public ImageSearchDatas() {
    }

    public Optional<String> getUrlsFilePath() {
        return urlsFilePath;
    }

    public void setUrlsFilePath(Optional<String> urlsFilePath) {
        this.urlsFilePath = urlsFilePath;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(int numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public List<String> getImagesPaths() {
        return imagesPaths;
    }

    public void setImagesPaths(List<String> imagesPaths) {
        this.imagesPaths = imagesPaths;
    }

    @Override
    public String toString() {
        return "ImageSearchDatas{" +
                "searchTerm='" + searchTerm + '\'' +
                ", numberOfResults=" + numberOfResults +
                ", urlsFilePath=" + urlsFilePath +
                ", imagesPaths=" + imagesPaths +
                '}';
    }
}
