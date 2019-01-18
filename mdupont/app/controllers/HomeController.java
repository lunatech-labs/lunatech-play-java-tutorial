package controllers;

import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.*;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        Messages messages = Http.Context.current().messages();
        return ok(index.render(messages));
    }

    public Result lang(String code) {
        ctx().changeLang(Lang.forCode(code));
        Messages messages = Http.Context.current().messages();
        return ok(index.render(messages));
    }
}
