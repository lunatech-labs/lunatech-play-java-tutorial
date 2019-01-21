package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import models.SearchImageData;
import play.Logger;
import services.DownloadService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DownloadActor extends UntypedAbstractActor {

    @Inject
    private DownloadService downloadService;

    @Inject
    @Named("dbUpdateImageActor")
    private ActorRef dbUpdateImageActor;

    public static Props getProps() {
        return Props.create(DownloadActor.class);
    }

    /**
     * This method takes a File in input and read data from it and download
     *
     * @param filePath File to read and download from.
     */
    protected List<String> doDownloadFromFile(File filePath) {
        List<String> retVal = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        List<SearchImageData> searchImageDatas;
        try {
            searchImageDatas = mapper.readValue(filePath, new TypeReference<ArrayList<SearchImageData>>() {
            });
        } catch (IOException e) {
            play.Logger.error("Error with deserialization", e);
            return retVal;
        }


        // Find the directory where we will add files based on the filePath to read
        String dirPath = filePath.getName().substring(0, filePath.getName().lastIndexOf('.'));

        CompletableFuture.runAsync(() -> {
            // Download every files we found. For every download we update the database for this corresponding result.
            for (SearchImageData searchImageData : searchImageDatas) {
                this.downloadService.downloadImageFile(searchImageData.getUrl(), dirPath)
                        .whenComplete((publicAssetPath, throwable) -> {
                            publicAssetPath.ifPresent(assetPath -> retVal.add(assetPath.toString()));
                        });

            }
        }).toCompletableFuture().join();

        return retVal;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof File) {
            List<String> downloadedFiles = doDownloadFromFile((File) message);
            sender().tell(downloadedFiles, self());
        } else {
            Logger.error("Wrong message type expected by DownloadActor");
        }
    }
}
