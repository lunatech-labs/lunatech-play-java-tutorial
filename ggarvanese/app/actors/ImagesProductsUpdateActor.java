package actors;

import akka.actor.AbstractActor;
import models.ImageSearchDatas;
import models.Product;
import services.ProductService;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ImagesProductsUpdateActor extends AbstractActor {

    private final ProductService productService;

    @Inject
    public ImagesProductsUpdateActor(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ImageSearchDatas.class, imageSearchDatas -> doUpdateProducts(imageSearchDatas)
                        .whenComplete((t, e) -> sender().tell(imageSearchDatas, self()))
                        .toCompletableFuture()
                        .join())
                .build();
    }

    private CompletionStage<List<Product>> doUpdateProducts(ImageSearchDatas imageSearchDatas){
        return CompletableFuture.completedFuture(productService.updateProducts(imageSearchDatas));
    }
}
