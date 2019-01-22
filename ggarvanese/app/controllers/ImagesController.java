package controllers;

import akka.actor.ActorRef;
import akka.pattern.Patterns;
import models.ImageSearchDatas;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import services.ProductService;
import views.html.products;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ImagesController extends Controller {

    @Inject
    private ProductService productService;
    @Inject
    @Named("imagesUpdaterActor")
    private ActorRef imagesUpdaterActor;

    public Result searchImages(String searchTerm) {
        Messages messages = Http.Context.current().messages();
        ImageSearchDatas imageDatas = new ImageSearchDatas();
        imageDatas.setSearchTerm(searchTerm);
        FutureConverters.toJava(Patterns.ask(imagesUpdaterActor, imageDatas, 10000));
        return ok(products.render(productService.findByCriteria("description", searchTerm)));
    }
}

