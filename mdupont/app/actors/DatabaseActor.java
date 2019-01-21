package actors;

import actors.messages.DBUpdateImageActorMsg;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import com.google.inject.Inject;
import play.Logger;
import services.ProductService;

public class DatabaseActor extends UntypedAbstractActor {

    @Inject
    private ProductService productService;

    public static Props getProps() {
        return Props.create(DatabaseActor.class);
    }

    /**
     * This method updates a product with an image URL. It find the first product corresponding with the description wanted
     * and update it with the image
     *
     * @param dbUpdateImageActorMsg {DBUpdateImageActorMsg} The description of the image with its public asset path
     */
    protected void doUpdateProductWithAnImage(DBUpdateImageActorMsg dbUpdateImageActorMsg) {
        // TODO
        Logger.info("Description:" + dbUpdateImageActorMsg.getDescription() + ", Asset: " + dbUpdateImageActorMsg.getFilePath());
        this.productService.findByDescriptionAndUrlIsEmpty(dbUpdateImageActorMsg.getDescription())
                .stream()
                .findFirst()
                .ifPresent(product -> {
                    Logger.info("Product is saved with " + dbUpdateImageActorMsg.getFilePath());
                    product.setUrl(dbUpdateImageActorMsg.getFilePath());

                    this.productService.update(product);
                });

    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof DBUpdateImageActorMsg) {
            doUpdateProductWithAnImage((DBUpdateImageActorMsg) message);
        } else {
            Logger.error("Wrong message type expected by DatabaseActor");
        }
    }
}
