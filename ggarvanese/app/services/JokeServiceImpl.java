package services;

import com.fasterxml.jackson.databind.JsonNode;
import models.Joke;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class JokeServiceImpl implements JokeService {

    private final WSClient wsClient;

    @Inject
    public JokeServiceImpl(WSClient wsClient) {
        this.wsClient = wsClient;
    }


   /* public CompletionStage<Joke> getJoke(){
        String request = "https://api.chucknorris.io/jokes/random";
        CompletionStage<WSResponse> response = wsClient.url(request).get();
        CompletionStage<JsonNode> jsonNode = response.thenApply(wsResponse -> wsResponse.asJson().get("value"));
        System.out.println(">>>>>>>>>>>> " + jsonNode);
        return null;*/
  //  }

}
