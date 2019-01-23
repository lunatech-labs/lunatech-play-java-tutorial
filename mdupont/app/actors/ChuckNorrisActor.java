package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import services.clients.ChuckNorrisAPIClient;
import com.google.inject.Inject;

import java.util.concurrent.CompletionStage;

public class ChuckNorrisActor extends AbstractActor {

    @Inject
    private ChuckNorrisAPIClient chuckNorrisAPIClient;

    public static Props getProps() {
        return Props.create(ChuckNorrisActor.class);
    }

    public CompletionStage<String> doSendReceive() {
        return this.chuckNorrisAPIClient.getJoke()
                .thenApply((response) -> response.asJson().findPath("value").asText());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    doSendReceive()
                            .whenComplete((response, e) -> sender().tell(response, self())).toCompletableFuture().join();
                })
                .build();
    }
}
