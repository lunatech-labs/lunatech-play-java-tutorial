package controllers;

import akka.actor.ActorRef;

import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import views.html.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

@Singleton
public class ChuckNorrisController extends Controller {

    @Inject
    @Named("theActor")
    ActorRef chuckNorrisActor;

    public CompletionStage<Result> getJoke(String message) {
        Messages messages = Http.Context.current().messages();

        return FutureConverters.toJava(
                ask(chuckNorrisActor, message, 5000))
                .thenApply(response -> ok(jokes.render(messages, (String) response)));
    }
}
