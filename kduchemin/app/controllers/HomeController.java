package controllers;

import actors.SearchActor;
import actors.SearchActorProtocol;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import views.html.index;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class HomeController extends Controller {

    public Result index() {
        return ok(index.render("Welcome in NoName Factory"));
    }

    private final ActorRef srchActor;

    @Inject
    public HomeController(ActorSystem system) {
        srchActor = system.actorOf(SearchActor.props);
        system.scheduler().schedule(
                Duration.create(0, TimeUnit.SECONDS), //Initial delay 20 sec
                Duration.create(60, TimeUnit.SECONDS),     //Frequency 30 minutes
                srchActor,
                new SearchActorProtocol.MatchingPics(),
                system.dispatcher(),
                null
        );
        system.scheduler().schedule(
                Duration.create(30, TimeUnit.SECONDS), //Initial delay 10 sec
                Duration.create(30, TimeUnit.SECONDS),     //Frequency 30 minutes
                srchActor,
                new SearchActorProtocol.DownloadPics(),
                system.dispatcher(),
                null
        );
        system.scheduler().schedule(
                Duration.create(40, TimeUnit.MILLISECONDS), //Initial delay 0 milliseconds
                Duration.create(90, TimeUnit.SECONDS),     //Frequency 30 minutes
                srchActor,
                new SearchActorProtocol.BingSearch(),
                system.dispatcher(),
                null
        );
    }

}
