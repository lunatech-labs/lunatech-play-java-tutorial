package actors;

import actors.messages.DBUpdateImageActorMsg;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import dto.ProductDto;
import play.Logger;
import scala.compat.java8.FutureConverters;
import services.ProductService;
import services.SearchService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static akka.pattern.Patterns.ask;

public class ProductManagerActor extends UntypedAbstractActor {

    @Inject
    private SearchService searchService;

    @Inject
    private ProductService productService;

    @Inject
    @Named("downloadActor")
    ActorRef downloadActor;

    @Inject
    @Named("dbUpdateImageActor")
    ActorRef dbUpdateImageActor;

    public static Props getProps() {
        return Props.create(ProductManagerActor.class);
    }

    /**
     * This method check that all products have a picture, otherwise it starts the search, download, and update actors till the end
     *  of the updatable products available.
     *
     * @param searchTerm {String} The search term we are looking for.
     */
    protected void sanitizeProduct(String searchTerm) {
        List<ProductDto> productToUpdate = productService.findByDescriptionAndUrlIsEmpty(searchTerm);

        if (productToUpdate.size() != 0) {
            Logger.debug("Sanitize products pictures. " + productToUpdate + " to update ...");
            try {
                this.searchService.search(searchTerm)
                        .thenApply(filePath ->
                                FutureConverters.toJava(ask(this.downloadActor, filePath, 5000))).toCompletableFuture().get()
                        .thenApply((response) -> {
                            List<String> downloadedFiles = (List<String>) response;

                            List<CompletableFuture> tasks = new ArrayList<>();

                            for (String assetPath : downloadedFiles) {
                                // Truncate only the public asset path from the full path
                                String formattedAssetPath = assetPath.substring(assetPath.indexOf("/public") + "/public".length());

                                tasks.add(FutureConverters.toJava(ask(this.dbUpdateImageActor, new DBUpdateImageActorMsg(searchTerm, formattedAssetPath), 5000)).toCompletableFuture());
                            }

                            return CompletableFuture.allOf(tasks.toArray(new CompletableFuture[tasks.size()]))
                                    .whenComplete((response2, throwable) -> {
                                        Logger.info("Sanitize done!");
                                        self().tell(searchTerm, ActorRef.noSender());
                                    });
                        });
            } catch (InterruptedException e) {
                Logger.error("An error occured when trying to download a file", e);
            } catch (ExecutionException e) {
                Logger.error("An error occured when trying to download a file", e);
            }

        } else {
            Logger.debug("All products updated");
        }
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        sanitizeProduct((String) message);
    }
}
