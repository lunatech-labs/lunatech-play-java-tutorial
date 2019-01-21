package controllers;

import akka.actor.ActorRef;
import com.google.inject.name.Named;
import play.mvc.Controller;
import play.mvc.Result;
import services.DownloadService;
import services.SearchService;
import services.clients.PixaBayImageClient;
import views.html.images;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ImagesController extends Controller {

    @Inject
    SearchService searchService;

    @Inject
    @Named("productManagerActor")
    ActorRef productManagerActor;

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public CompletionStage<Result> search(String searchTerm) {
        return this.searchService.search(searchTerm)
                .thenApply((filePath) -> ok(images.render(filePath.getAbsolutePath())));
    }

    public Result searchAndDownload(String searchTerm) {
        this.productManagerActor.tell(searchTerm, ActorRef.noSender());

        return redirect(routes.ProductController.index(0, ""));
    }
}
