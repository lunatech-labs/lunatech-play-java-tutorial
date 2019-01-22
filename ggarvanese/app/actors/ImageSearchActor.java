package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import models.ImageSearchDatas;
import services.ImageService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletionStage;

public class ImageSearchActor extends AbstractActor {

    @Inject
    private ImageService imageService;

    @Inject
    @Named("imagesDownloaderActor")
    private ActorRef imagesDownloaderActor;


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ImageSearchDatas.class, imageSearchDatas -> doSearchImages(imageSearchDatas)
                        .whenComplete((datasWithUrlsPath, t) -> Patterns.ask(imagesDownloaderActor, datasWithUrlsPath, 10000))
                        .toCompletableFuture()
                        .join())
                .build();
    }

    private CompletionStage<ImageSearchDatas> doSearchImages(ImageSearchDatas imageSearchDatas) {
        return imageService.searchAndStoreURLs(imageSearchDatas);
    }

}
