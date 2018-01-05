package services;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import javax.inject.Inject;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import models.BingResult;
import play.libs.akka.InjectedActorSupport;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionStage;

public class TaskActor extends UntypedActor implements InjectedActorSupport {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private final ImagesServices imagesService;

    @Override
    public void preStart() {
        log.debug("Starting");
    }

    @Override
    public void postStop() {
        log.debug("Stopping");
    }

    @Inject
    public TaskActor(ImagesServices imagesService) {
        this.imagesService = imagesService;
    }

    public void onReceive(Object msg) throws Exception {
        if (msg instanceof TaskActorProtocol.SearchUrls)
            searchUrl(((TaskActorProtocol.SearchUrls)msg).keyword);

        if (msg instanceof TaskActorProtocol.DownloadImages)
            downloadImages(((TaskActorProtocol.DownloadImages)msg).keyword);

        if (msg instanceof TaskActorProtocol.SaveImagesToDb)
            saveImagesToDb(((TaskActorProtocol.SaveImagesToDb)msg).keyword);
    }

    private void searchUrl(String q) {
        ActorRef actorSender = sender();
        log.info("Search url="+q);
        try {
            imagesService.searchImageService(q)
                .thenAccept(bingResult -> {
                    if (bingResult.getStatus() != 200)
                        log.debug("status != 200");
                    else {
                        String jsonString = bingResult.urlsToJson();
                        // To check error management with actor
                        // comment this
                        log.debug("jsonString="+jsonString);
                        if (jsonString != null)
                            imagesService.saveUrl(jsonString, q);
                        else
                            throw new CancellationException("Json parsing error");
                    }
                    // and uncomment this
                    //try { Thread.sleep(1000); } catch (Exception e) {}
                    //throw new CancellationException("Json parsing error");
                 })
                .thenAccept(response -> {
                    log.info("searchUrl\tme=" + self().toString() + "\tsender:\t" + actorSender.toString());
                    self().tell(new TaskActorProtocol.DownloadImages(q), self());
                })
                .exceptionally(throwable -> {
                    log.error(throwable, "actor nok 2");
                    actorSender.tell(new akka.actor.Status.Failure(throwable), self());
                    return null;
                });
        } catch (Throwable throwable) {
            log.error(throwable, "");
            actorSender.tell(new akka.actor.Status.Failure(throwable), self());
        }
    }

    private void downloadImages(String q) {
        log.info("downloadImages="+q);
        ActorRef actorSender = sender();
        try {
            imagesService.loadImagesFromUrls(q)
                    .thenAccept(response -> {
                        log.info("downloadImages\tme="+self().toString() + "\tsender:\t" + actorSender);
                        self().tell(new TaskActorProtocol.SaveImagesToDb(q), self());
                    });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("downloadImages\tme="+self().toString() + "\tsender:\t" + actorSender);
            actorSender.tell(new akka.actor.Status.Failure(throwable), self());
        }
    }

    private void saveImagesToDb(String q) {
        ActorRef actorSender = sender();
        ActorRef me = self();

        log.info("SaveImages url="+q);
        try {
            imagesService.saveImagesToDb(q)
                    .thenAccept(response -> {
                        log.info("saveImagesToDb\tme="+self().toString() + "\tsender:\t" + actorSender.toString());
                        actorSender.tell("success", me);
                        log.info("=>saveImagesToDb\tme="+self().toString() + "\tsender:\t" + actorSender.toString());
                    });
            //throw new CancellationException("test");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.info("saveImagesToDb:throwable\tme="+self().toString() + "\tsender:\t" + actorSender.toString());
            actorSender.tell(new akka.actor.Status.Failure(throwable), self());
        }
    }
}