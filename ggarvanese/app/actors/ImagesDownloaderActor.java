package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import models.ImageSearchDatas;
import scala.compat.java8.FutureConverters;
import services.ImageDownloadService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletionStage;

public class ImagesDownloaderActor extends AbstractActor {

    @Inject
    private ImageDownloadService imageDownloadService;

    @Inject
    @Named("imagesProductsUpdateActor")
    private ActorRef imagesProductsUpdateActor;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ImageSearchDatas.class, imageSearchDatas -> doDownloadAndStoreImages(imageSearchDatas)
                        .whenComplete((dataWithFilesPaths, t) -> FutureConverters.toJava(Patterns.ask(imagesProductsUpdateActor, dataWithFilesPaths, 10000)))
                        .toCompletableFuture()
                        .join())
                .build();
    }

    private CompletionStage<ImageSearchDatas> doDownloadAndStoreImages(ImageSearchDatas imageSearchDatas){
        return imageDownloadService.downloadAndStoreImages(imageSearchDatas);
    }
}
