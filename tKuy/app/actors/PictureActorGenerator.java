package actors;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import hexagon.IProductDao;
import hexagon.IProductWsPicture;
import hexagon.Product;
import play.Logger;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.*;

public class PictureActorGenerator extends AbstractLoggingActor {
    @Inject
    private IProductWsPicture productWsPicture;
    @Inject
    private IProductDao productDao;
    public static Props getProps() {
        // You need to specify the actual type of the returned actor
        // since Java 8 lambdas have some runtime type information erased
        //return Props.create(PictureActorGenerator.class, () -> new PictureActorGenerator(url));
        return Props.create(PictureActorGenerator.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Product.class, product -> {
                    Logger.info(product.toString());
                    product.picture = "image" + product.ean + ".jpg";
                    productWsPicture.randomPicture(product.ean);
                    productDao.update(product);
                    getSender().tell(product, getSelf());
                })
                .build();
    }
    public static CompletionStage<Object> generatePictureWithAsk(Product product, ActorRef pictureActorGenerator) {
        return ask(pictureActorGenerator, product, 3000);
    }

}
