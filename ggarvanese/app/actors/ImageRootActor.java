package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import models.ImageSearchDatas;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletionStage;

public class ImageRootActor extends AbstractActor {

    @Inject
    @Named("imagesSearchActor")
    private ActorRef imageSearchActor;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ImageSearchDatas.class, imageSearchDatas -> doSearchImages(imageSearchDatas)
                        .whenComplete((datasWithUrlsPath, t) -> Patterns.ask(imageSearchActor, datasWithUrlsPath, 10000))
                        .toCompletableFuture()
                        .join())
                .build();
    }

    private CompletionStage<ImageSearchDatas> doSearchImages(ImageSearchDatas imageSearchDatas){

        return null;
    }
}
