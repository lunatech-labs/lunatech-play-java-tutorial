package controllers;

import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import util.Util;
import views.html.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static akka.pattern.Patterns.ask;

@Singleton
public class ChuckNorrisController extends Controller {

    @Inject
    @Named("theActor")
    ActorRef chuckNorrisActor;

    public Result getJoke(String message) {
        String response = "";
        try {
            response = (String) FutureConverters.toJava(
                    ask(chuckNorrisActor, message, 5000)).toCompletableFuture().get();
        } catch (Exception e) {
            Logger.error("Can't fetch joke from ChuckNorrisClient", e);
        }

        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonData = mapper.convertValue(response, JsonNode.class);
        return ok(Util.createResponse(jsonData, true));
    }
}
