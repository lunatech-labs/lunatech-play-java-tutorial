package models;

public class ProcessData {

    public String searchFileName;
    public String imgDirectory;
    public int imgDownloaded;
    public int productUpdated;

    public ProcessData(String searchFileName, String imgDirectory, int imgDownloaded, int productUpdated) {
        this.searchFileName = searchFileName;
        this.imgDirectory = imgDirectory;
        this.imgDownloaded = imgDownloaded;
        this.productUpdated = productUpdated;
    }
}
