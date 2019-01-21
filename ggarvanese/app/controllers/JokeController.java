package controllers;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import views.html.joke;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

@Singleton
public class JokeController extends Controller {

    @Inject
    private WSClient wsClient;

    @Inject
    @Named("theActor")
    private ActorRef jokeActor;

    public CompletionStage<Result> chuckNorrisPage(){
        CompletionStage<WSResponse> response = wsClient.url("https://api.chucknorris.io/jokes/random").get();
        return response.thenApply(wsResponse -> ok(joke.render(wsResponse.asJson().findPath("value").asText())));
    }

    public CompletionStage<Result> publishMessage(String message){
        return FutureConverters.toJava(Patterns.ask(jokeActor, message, 10000))
                .thenApply(response -> ok(joke.render((String) response)));
    }

    public Result testActorMessage(){
        return ok(joke.render(""));
    }
}
