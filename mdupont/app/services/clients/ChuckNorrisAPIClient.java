package services.clients;

import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

@Singleton
public class ChuckNorrisAPIClient {
    private final WSClient ws;

    @Inject
    public ChuckNorrisAPIClient(WSClient ws) {
        this.ws = ws;
    }

    public CompletionStage<WSResponse> getJoke() {
        return ws.url("https://api.chucknorris.io/jokes/random").get();
    }

    public CompletionStage<WSResponse> getJokeByCategories(String category) {
        return ws.url("https://api.chucknorris.io/jokes/random?category=" + category).get();
    }
}
