package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.pattern.Patterns;
import models.ImageSearchDatas;
import models.Product;
import models.ProductDAO;
import scala.compat.java8.FutureConverters;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ImagesUpdaterActor extends AbstractActor {

    @Inject
    @Named("imagesSearchActor")
    private ActorRef imageSearchActor;

    @Inject
    ProductDAO productDAO;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ImageSearchDatas.class, imageSearchDatas -> {
                    run(imageSearchDatas)
                            .whenComplete((datas, e) -> sender().tell(datas, self()))
                            .toCompletableFuture()
                            .join();
                })
                .build();
    }

    private CompletionStage<ImageSearchDatas> run(ImageSearchDatas imageSearchDatas) {
        List<Product> products = productDAO.findEmptyFieldsByDescription(imageSearchDatas.getSearchTerm(), "imagePath");
        imageSearchDatas.setNumberOfResults(products.size());
        if (!products.isEmpty()) {
                imageSearchDatas.setProductsToUpdate(products);
                FutureConverters.toJava(Patterns.ask(imageSearchActor, imageSearchDatas, 10000));
        }
        return CompletableFuture.completedFuture(imageSearchDatas);
    }
}
