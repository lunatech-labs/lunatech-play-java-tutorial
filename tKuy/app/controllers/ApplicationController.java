package controllers;

import actors.PictureActorGenerator;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import hexagon.IProductService;
import hexagon.Product;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class ApplicationController extends Controller {

    final ActorRef pictureActor;
    @Inject
    private IProductService productService;

    @Inject public ApplicationController(@Named("Picture") ActorRef pictureActor) {
        this.pictureActor = pictureActor;
    }

    public Result loadMissingPicture(Long ean) {
        Optional<Product> opt = productService.productById(ean);
        if(opt.isPresent()) {
            pictureActor.tell(productService.productById(ean).get(), pictureActor);
        }
        return ok();
    }
    public Result loadAllWithActor() {
        ActorSystem system = ActorSystem.create("MyActorSystem");
        RunnableGraph<CompletionStage<List<Object>>> graph = Source.from(productService.allProducts())
                .filter(p -> p.picture==null)
                .mapAsyncUnordered(15, p -> PictureActorGenerator.generatePictureWithAsk(p, pictureActor))
                .toMat(Sink.seq(), Keep.right());
        graph.run(ActorMaterializer.create(system));
        //productService.allProducts().forEach(p -> pictureActor.tell(p, pictureActor));
        return ok();
    }
}
