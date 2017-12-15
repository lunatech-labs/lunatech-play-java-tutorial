package actors;

import models.ProcessData;

public class ProcessMessageProtocol {

    public static class SearchMessage {
        final String query;
        final Boolean doDownload;
        final Boolean doUpdate;
        final Boolean withCallback;

        SearchMessage(String query, Boolean doDownload, Boolean doUpdate, Boolean withCallback) {
            this.query = query;
            this.doDownload = doDownload;
            this.doUpdate = doUpdate;
            this.withCallback = withCallback;
        }

        public static SearchMessage getSearchMessage(final String query, Boolean withCallback) {
            return new SearchMessage(query, false, false, withCallback);
        }

        public static SearchMessage getSearchDownloadMessage(final String query, Boolean withCallback) {
            return new SearchMessage(query, true, false, withCallback);
        }

        public static SearchMessage getSearchDownloadUpdateMessage(final String query, Boolean withCallback) {
            return new SearchMessage(query, true, true, withCallback);
        }
    }

    static class DownloadMessage {
        final String query;
        final String searchFileName;
        final Boolean doUpdate;
        final Boolean withCallback;

        DownloadMessage(String query, String searchFileName, Boolean doUpdate, Boolean withCallback) {
            this.query = query;
            this.searchFileName = searchFileName;
            this.doUpdate = doUpdate;
            this.withCallback = withCallback;
        }
    }

    static class UpdateMessage {
        final String query;
        final ProcessData data;
        final Boolean withCallback;

        UpdateMessage(String query, ProcessData data, Boolean withCallback) {
            this.query = query;
            this.data = data;
            this.withCallback = withCallback;
        }
    }
}
