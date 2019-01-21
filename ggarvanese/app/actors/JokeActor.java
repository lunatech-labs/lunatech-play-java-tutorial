package actors;

import akka.actor.AbstractActor;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class JokeActor extends AbstractActor {

    @Inject
    private WSClient ws;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    doSendService().whenComplete((t, e) -> sender().tell(message + " : " + t, self()))
                            .toCompletableFuture()
                            .join();
                })
                .build();
    }

    private CompletionStage<String> doSendService(){
        return ws.url("https://api.chucknorris.io/jokes/random")
                .get()
                .thenApply(wsResponse -> {
                    JsonNode value = wsResponse.asJson().get("value");
                    return value.asText();
                });
    }
}
