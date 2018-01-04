package controllers;

import play.mvc.*;

import views.html.*;
import play.i18n.Messages;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private Object value;
    static final String MENU_DB = "Database";
    static final String MENU_BING = "Bing";

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result database() {
        Messages message = play.mvc.Http.Context.current().messages();
        return ok(index.render( MENU_DB, message, ctx().lang()));
    }

    public Result bing() {
        Messages message = play.mvc.Http.Context.current().messages();
        return ok(index.render( MENU_BING, message, ctx().lang()));
    }

    public Result index() {
        Messages message = play.mvc.Http.Context.current().messages();
        return redirect(routes.HomeController.database());
    }

    public Result changeLanguage(String language) {
        ctx().changeLang(language);
        //never called since javascript is executed before
        return redirect(routes.HomeController.database());
    }

}
