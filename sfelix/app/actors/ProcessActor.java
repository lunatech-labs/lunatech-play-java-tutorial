package actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import play.Logger;
import services.*;

import javax.inject.Inject;

import static actors.ProcessMessageProtocol.*;

public class ProcessActor extends UntypedActor {

    @Inject
    SearchImageService searchImageService;

    @Inject
    DownloaderImageService downloaderImageService;

    @Inject
    UpdaterProductService updaterProductService;

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof SearchMessage) {
            search((SearchMessage)msg);
        } else if (msg instanceof DownloadMessage) {
            download((DownloadMessage)msg);
        } else if (msg instanceof UpdateMessage) {
            update((UpdateMessage)msg);
        }
    }

    private void search(SearchMessage msg) {
        Logger.info("ProcessActor receive SearchMessage with query={} - thread={}", msg.query, Thread.currentThread().getName());

        final ActorRef target = sender();

        searchImageService.search(msg.query)
                .whenComplete((searchFileName, e) -> {
                    if(e!=null && msg.withCallback)
                        target.tell(e.getCause()!=null?e.getCause():e, self());
                    else if(msg.doDownload)
                        self().tell(new DownloadMessage(msg.query, searchFileName, msg.doUpdate, msg.withCallback), target);
                    else if(msg.withCallback)
                        target.tell(searchFileName, self());
                });
    }

    private void download(DownloadMessage msg) {
        Logger.info("ProcessActor receive DownloadMessage with searchFileName={} - thread={}", msg.searchFileName, Thread.currentThread().getName());

        final ActorRef target = sender();

        downloaderImageService.download(msg.query, msg.searchFileName)
                .whenComplete((data, e) -> {
                    if(e!=null && msg.withCallback)
                        target.tell(e.getCause()!=null?e.getCause():e, self());
                    else if(msg.doUpdate)
                        self().tell(new UpdateMessage(msg.query, data, msg.withCallback), target);
                    else if(msg.withCallback)
                        target.tell(data, self());
                });
    }

    private void update(UpdateMessage msg) {
        Logger.info("ProcessActor receive UpdateMessage with imgDirectory={} - thread={}",
                (msg.data.imgDirectory!=null?msg.data.imgDirectory:""), Thread.currentThread().getName());

        final ActorRef target = sender();

        updaterProductService.update(msg.query, msg.data)
                .whenComplete((data, e) -> {
                    if(e!=null && msg.withCallback)
                        target.tell(e.getCause()!=null?e.getCause():e, self());
                    else if(msg.withCallback)
                        target.tell(data, self());
                });
    }
}

