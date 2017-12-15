package controllers;

import play.mvc.Result;
import services.DownloaderImage;
import services.MatcherImage;
import views.html.index;

import static play.mvc.Results.*;

public class ImageController {

    public Result testActorMessages() {

        return TODO;
    }

    public Result testWebservice() {

        return new WebSearchController().refill();
    }

    public Result testUpdateProductMessage() {
        MatcherImage.matching();
        return redirect(routes.Products.list());
    }

    public Result testDownloadImageMessage() {
        return ok(index.render(new DownloaderImage().downloadImageFromURLInDB()));
    }

    public Result testSearchImageMessage(String term) {

        return TODO;
    }
}
