package actors;

import actors.messages.DBUpdateImageActorMsg;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import models.SearchImageData;
import actors.DbUpdateImageActor;
import play.Logger;
import services.DownloadService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    protected void doDownloadFromFile(File filePath) {

        ObjectMapper mapper = new ObjectMapper();
        List<SearchImageData> searchImageDatas;
        try {
            searchImageDatas = mapper.readValue(filePath, new TypeReference<ArrayList<SearchImageData>>() {
            });
        } catch (IOException e) {
            play.Logger.error("Error with deserialization", e);
            return;
        }


        // Find the directory where we will add files based on the filePath to read
        String dirPath = filePath.getName().substring(0, filePath.getName().lastIndexOf('.'));

        // Run async thread to dowload and save files.
        for (SearchImageData searchImageData : searchImageDatas) {
            this.downloadService.downloadImageFile(searchImageData.getUrl(), dirPath)
                    .whenComplete((publicAssetPath, throwable) -> {
                        if (throwable != null) {
                            Logger.error("Can't download file", throwable);
                        }

                        publicAssetPath.ifPresent(assetPath -> {
                            // Truncate only the public asset path from the full path
                            String formattedAssetPath = assetPath.toString().substring(assetPath.toString().indexOf("/public") + "/public".length());

                            // Start the update.
                            this.dbUpdateImageActor.tell(new DBUpdateImageActorMsg(dirPath, formattedAssetPath), ActorRef.noSender());
                        });
                    });

        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof File) {
            doDownloadFromFile((File) message);
        } else {
            Logger.error("Wrong message type expected by DownloadActor");
        }
    }
}
