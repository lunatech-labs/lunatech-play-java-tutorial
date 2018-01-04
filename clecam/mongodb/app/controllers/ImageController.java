package controllers;

import akka.actor.ActorSystem;
import akka.pattern.AskTimeoutException;
import models.BingResult;
import models.KeywordData;
import play.libs.Json;
import play.mvc.*;
import javax.inject.Inject;
import javax.inject.Named;
//import javax.inject.Named;
import java.security.Key;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeoutException;
import com.fasterxml.jackson.databind.*;
import play.libs.concurrent.HttpExecutionContext;
import scala.compat.java8.FutureConverters;
import services.ImagesServices;
import akka.actor.*;
import services.TaskActor;
import services.TaskActorProtocol;
import static akka.pattern.Patterns.ask;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ImageController extends Controller {

    private Object value;
    private play.Logger.ALogger logger = play.Logger.of(getClass());
    private HttpExecutionContext ec;
    private final ImagesServices imagesService;
    final ActorRef taskActor;

    private FormFactory formFactory;

    @Inject
    public ImageController(FormFactory formFactory, HttpExecutionContext ec, ImagesServices imagesService, @Named("actor-bing") ActorRef taskActor) {
        this.formFactory = formFactory;
        this.ec = ec;
        this.imagesService = imagesService;
        this.taskActor = taskActor;
    }

    public Result selectKeywords() {
        Messages message = play.mvc.Http.Context.current().messages();
        Form<KeywordData> form = formFactory.form(KeywordData.class);
        Form<KeywordData> filledForm = form.fill(new KeywordData("chair"));
        return ok(views.html.keyword.render(filledForm, message));
    }

    public Result searchKeywordSubmit() {
        Messages message = play.mvc.Http.Context.current().messages();
        Form<KeywordData> form = formFactory.form(KeywordData.class);
        final Form<KeywordData> boundForm = form.bindFromRequest();
        if (boundForm.hasErrors()) {
            flash("error", message.at("form.error.correction"));
            return badRequest(views.html.keyword.render(boundForm, message));
        }
        KeywordData keywordData = boundForm.get();
        String keyword = keywordData.getKeyword();
        // supposed to be all right.
        searchImageService(keyword);
        flash("success", message.at("web.service.launched"));
        return redirect(routes.ImageController.selectKeywords());
    }

   /*
    * For keywords q,
    *  request bing image service and save url,
    *  downlioad image,
    *  save image to database.
    */
    public CompletionStage<Result> searchImageService(String q) {
        /*
        logger.warn("searchImageService for " + q);
        return CompletableFuture.supplyAsync(() -> {
            return redirect(routes.HomeController.bing());
        });
        */
        // TODO: change ask by tell: not waiting for a result, don't care about timeout
        return FutureConverters.toJava(ask(taskActor, new TaskActorProtocol.SearchUrls(q), 1000))
        .handle((response, throwable) -> {
            if (throwable != null)
                System.out.println("searchImageService:throwable="+throwable.toString());
            System.out.println("response="+response.toString());
            if (throwable != null && !(throwable instanceof AskTimeoutException)) {
                logger.error("", throwable);
            }
            Messages message = play.mvc.Http.Context.current().messages();
            flash("success", message.at("web.service.launched"));
            return redirect(routes.ImageController.selectKeywords());
        });
    }

    /*
     * Load all url from file relative to the q description parameter
     * and then save each image to the file system with _<number>.[jpg, png...]
     */
    public CompletionStage<Result> loadImagesFromUrls(String q) {
        // TODO: change ask by tell
        return FutureConverters.toJava(ask(taskActor, new TaskActorProtocol.DownloadImages(q), 1000))
        .handle((response, throwable) -> {
            if (throwable != null)
                System.out.println("loadImagesFromUrl:throwable="+throwable.toString());
            if (throwable != null && !(throwable instanceof AskTimeoutException)) {
                logger.error("", throwable);
            }
            return redirect(routes.ImageController.selectKeywords());
        });
    }

    public CompletionStage<Result> saveImagesForKeywordInDb(String q) {
        // TODO: change ask by tell
        return FutureConverters.toJava(ask(taskActor, new TaskActorProtocol.SaveImagesToDb(q), 1000))
        .handle((response, throwable) -> {
            if (throwable != null)
                System.out.println("saveImagesForKeywordInDb:throwable="+throwable.toString());
        if (throwable != null && !(throwable instanceof AskTimeoutException)) {
                throwable.printStackTrace();
                //return internalServerError(throwable.getMessage());
            }
            return redirect(routes.ImageController.selectKeywords());
        });
    }

    /*
     * callable from javascript
     */
//    public Result formatKeyword(String q) {
//        return ok(Json.toJson(imagesService.normalize(q)));
//    }

    public Result showUrlsFromJsonFile(String q) {
        return ok(imagesService.loadUrlsFromJsonFile(q)).as("application/json");
    }

    /*
     * Save image for each product description *
     * in the database                         *
     */

    public CompletionStage<Result> saveAllImagesInDb() {
        return imagesService.saveAllImagesInDb().thenApply(s -> {
            if (s.equals("success"))
                return redirect(routes.HomeController.bing());
            return internalServerError(s);
        });
    }

    /*******************************************/
    /*******************************************/

    /*
     * Retrieve url from product having no images downloaded
     */
    public CompletionStage<Result> retrieveAllUrl() {
        return imagesService.retrieveAllUrl().thenApply(s -> {
            if (s.equals("success"))
                return redirect(routes.HomeController.bing());
            return internalServerError(s);

        });
    }

    /*******************************************
     * Save image for each product description *
     * in the repository public/images         *
     *******************************************/

    /*
     * For each keyword, get description and upload images
     */
    public CompletionStage<Result> saveAllImagesInFiles() {
        return imagesService.saveAllImagesInFiles().thenApply(s -> {
            if (s.equals("success"))
                return redirect(routes.HomeController.bing());
            return internalServerError(s);
        });
    }

}

