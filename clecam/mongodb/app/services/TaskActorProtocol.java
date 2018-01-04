package services;

public class TaskActorProtocol {

    public TaskActorProtocol() {

    }

    public static class SearchUrls {
        public String keyword;

        public SearchUrls(String keyword) {
            this.keyword = keyword;
        }
    }

    public static class DownloadImages {
        public String keyword;

        public DownloadImages(String keyword) {
            this.keyword = keyword;
        }
    }

    public static class SaveImagesToDb {
        public String keyword;

        public SaveImagesToDb(String keyword) {
            this.keyword = keyword;
        }
    }

}