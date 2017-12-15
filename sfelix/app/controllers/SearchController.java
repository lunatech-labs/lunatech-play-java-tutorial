package controllers;

import akka.actor.ActorRef;
import akka.pattern.AskTimeoutException;
import models.ProcessData;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import views.html.searchImages;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;
import static actors.ProcessMessageProtocol.SearchMessage;

@Singleton
public class SearchController extends Controller {

    @Inject
    FormFactory formFactory;

    @Inject
    @Named("processActor")
    ActorRef processActor;

    public Result images(String withCallbackStr) {
        return ok(searchImages.render(formFactory.form(), "1".equals(withCallbackStr)));
    }

    public CompletionStage<Result> searchImages(String withCallbackStr) {
        final Http.Flash flash = flash();
        final DynamicForm form = formFactory.form().bindFromRequest();
        final String action = form.get("action");
        final Boolean withCallback = "1".equals(withCallbackStr);

        if(!withCallback && "searchImage".equals(action)) {
            final String query = form.get("query");

            if(query == null || query.isEmpty()) {
                form.reject("query", "Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(searchImages.render(form, false)));
            }

            Logger.info("TELL SearchMessage to processActor - thread={}", Thread.currentThread().getName());

            processActor.tell(SearchMessage.getSearchMessage(query, false), ActorRef.noSender());
            flash.put("success", "Process search is started, u can go drink some coffee");
            return CompletableFuture.completedFuture(redirect(routes.SearchController.images(null)));

        } else if(!withCallback && "searchDownloadImage".equals(action)) {
            final String query = form.get("query2");

            if(query == null || query.isEmpty()) {
                form.reject("query2","Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(searchImages.render(form, false)));
            }

            Logger.info("TELL SearchMessage to processActor - thread={}", Thread.currentThread().getName());

            processActor.tell(SearchMessage.getSearchDownloadMessage(query, false), ActorRef.noSender());
            flash.put("success", "Process search + download is started, u can go drink some coffee");
            return CompletableFuture.completedFuture(redirect(routes.SearchController.images(null)));

        } else if(!withCallback && "searchDownloadUpdateImage".equals(action)) {
            final String query = form.get("query3");

            if(query == null || query.isEmpty()) {
                form.reject("query3","Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(searchImages.render(form, false)));
            }

            Logger.info("TELL SearchMessage to processActor - thread={}", Thread.currentThread().getName());

            processActor.tell(SearchMessage.getSearchDownloadUpdateMessage(query, false), ActorRef.noSender());
            flash.put("success", "Process search + download + update is started, u can go drink some coffee");
            return CompletableFuture.completedFuture(redirect(routes.SearchController.images(null)));

        } else if(withCallback && "searchImage".equals(action)) {
            final String query = form.get("query");

            if(query == null || query.isEmpty()) {
                form.reject("query", "Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(searchImages.render(form, true)));
            }

            Logger.info("ASK SearchMessage to processActor - thread={}", Thread.currentThread().getName());

            return FutureConverters.toJava(
                    ask(processActor, SearchMessage.getSearchMessage(query, true), 15000))
                    .handle((response, e) -> {
                        if(e != null && e.getCause() instanceof AskTimeoutException)
                            flash.put("error", "Timeout, no answer will arrive");
                        else if(response instanceof Throwable) {
                            e = (Throwable)response;
                            flash.put("error", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                        } else
                            flash.put("success", String.format("File search: %s", (String)response));

                        return redirect(routes.SearchController.images("1"));
                    });

        } else if(withCallback && "searchDownloadImage".equals(action)) {
            final String query = form.get("query2");

            if(query == null || query.isEmpty()) {
                form.reject("query2","Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(searchImages.render(form, true)));
            }

            Logger.info("ASK SearchMessage to processActor - thread={}", Thread.currentThread().getName());

            return FutureConverters.toJava(
                    ask(processActor, SearchMessage.getSearchDownloadMessage(query, true), 15000))
                    .handle((response, e) -> {
                        if(e != null && e.getCause() instanceof AskTimeoutException)
                            flash.put("error", "Timeout, no answer will arrive");
                        else if(response instanceof Throwable) {
                            e = (Throwable)response;
                            flash.put("error", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                        } else {
                            ProcessData data = (ProcessData)response;
                            flash.put("success", String.format("File %s is created ==> directory %s is created with %d image(s)",
                                    data.searchFileName, data.imgDirectory, data.imgDownloaded));
                        }

                        return redirect(routes.SearchController.images("1"));
                    });

        } else if(withCallback && "searchDownloadUpdateImage".equals(action)) {
            final String query = form.get("query3");

            if(query == null || query.isEmpty()) {
                form.reject("query3","Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(searchImages.render(form,true)));
            }

            Logger.info("ASK SearchMessage to processActor - thread={}", Thread.currentThread().getName());

            return FutureConverters.toJava(
                        ask(processActor, SearchMessage.getSearchDownloadUpdateMessage(query, true), 15000))
                    .handle((response, e) -> {
                        if(e != null && e.getCause() instanceof AskTimeoutException)
                            flash.put("error", "Timeout, no answer will arrive");
                        else if(response instanceof Throwable) {
                            e = (Throwable)response;
                            flash.put("error", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
                        } else {
                            ProcessData data = (ProcessData)response;
                            flash.put("success", String.format("File %s is created ==> Directory %s is created with %d image(s) ==> %d product(s) updated",
                                    data.searchFileName, data.imgDirectory, data.imgDownloaded, data.productUpdated));
                        }

                        return redirect(routes.SearchController.images("1"));
                    });

        } else {
            return CompletableFuture.completedFuture(badRequest("No action found: "+action));
        }
    }
}
