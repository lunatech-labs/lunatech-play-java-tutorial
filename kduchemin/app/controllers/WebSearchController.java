package controllers;

import models.Product;
import models.WebSearch;
import play.mvc.Controller;
import play.mvc.Result;
import services.WebSearchService;
import views.html.testList;

import javax.inject.Singleton;

@Singleton
public class WebSearchController extends Controller {

    public WebSearchController() {

    }

    public static Result list() {

        return ok(testList.render(WebSearch.findAll()));
    }

    public static Result refill() {
        Product.findAll().forEach(prod -> {
            if ((prod.picture == null || prod.picture.length == 0) && !WebSearch.findAll().contains(prod.description))
                new WebSearchService().searchTerm(prod.description);
        });
        return ok(testList.render(WebSearch.findAll()));
    }

}