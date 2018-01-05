package controllers;

import models.*;
import play.api.Play;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import play.data.Form;
import play.data.FormFactory;
import javax.inject.Singleton;
import javax.inject.*;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import repository.ProductDAO;

import play.i18n.Messages;
import play.cache.Cached;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static play.libs.Scala.asScala;

@Singleton
public class Products extends Controller {

    private play.Configuration configuration;
    private FormFactory formFactory;
    private play.Logger.ALogger logger = play.Logger.of(getClass());
    private HttpExecutionContext ec;
    private ProductDAO productDao;

    @Inject
    public Products(FormFactory formFactory, play.Configuration configuration, HttpExecutionContext ec,
                    ProductDAO productDao) {

        this.formFactory = formFactory;
        this.configuration = configuration;
        this.ec = ec;
        this.productDao = productDao;
    }

    public Result dbclear() {
        productDao.eraseAll();
        //flash("success", message.at("product.flushed.ok"));
        // executed even if there is a javascript redirection !
        return redirect(routes.HomeController.database());
    }

    public Result dbClearSuccess() {
        Messages message = play.mvc.Http.Context.current().messages();
        flash("success", message.at("product.flushed.ok"));
        return redirect(routes.HomeController.database());
    }
    public Result dbClearError() {
        Messages message = play.mvc.Http.Context.current().messages();
        flash("error", message.at("product.flushed.nok"));
        return redirect(routes.HomeController.database());
    }


    public Result dbload() {
        Messages message = play.mvc.Http.Context.current().messages();
        String csvpath = configuration.getString("ikeacsvpath");
        if (csvpath == null) {
            return notFound(message.at("csvpath.error"));
        }
        productDao.loadCsv(csvpath);
        flash("success", message.at("product.downloaded.ok"));
        return redirect(routes.HomeController.database());
    }

    public Result dbloadSuccess() {
        Messages message = play.mvc.Http.Context.current().messages();
        flash("success", message.at("product.downloaded.ok"));
        return redirect(routes.HomeController.database());
    }

    public Result dbloadError() {
        Messages message = play.mvc.Http.Context.current().messages();
        flash("success", message.at("product.downloaded.nok"));
        return redirect(routes.HomeController.database());
    }

    public CompletionStage<Result> imagesDownload() {
        return CompletableFuture.supplyAsync(() -> {
            return redirect(routes.Products.list());
        });
    }

    public CompletionStage<Result> list()  {
        Messages message = play.mvc.Http.Context.current().messages();
        return productDao.findAll().thenApply(lProducts -> {
            lProducts.sort((Product p1, Product p2) -> p1.ean.toLowerCase().compareTo(p2.ean.toLowerCase()));
            return ok(views.html.products.list.render(lProducts, message)
            );
        });
    }

    public Result newProduct() {
        Messages message = play.mvc.Http.Context.current().messages();
        Form<ProductData> form = formFactory.form(ProductData.class);
        // empty form
        session().put("id", "#");
        return ok(views.html.products.details.render(form, message));
    }

    public CompletionStage<Result> details(String ean) {
        Messages message = play.mvc.Http.Context.current().messages();
        return productDao.findByEan(ean).thenApply(product -> {
            if (product == null)
                return notFound(message.at("ean.invalid", ean));
            Form<ProductData> form = formFactory.form(ProductData.class);
            Form<ProductData> filledForm = form.fill(new ProductData(product));
            session().put("id", product.id.toHexString());
            return ok(views.html.products.details.render(filledForm, message));
        });
    }

    /*
     * Call by url show
     */
    //@Cached(key="showImage")
    public CompletionStage<Result> showImage(String ean) {
        return productDao.findByEan(ean).thenApply(product -> {
            Messages message = play.mvc.Http.Context.current().messages();
            if (product != null) {
                byte[] picture = product.picture;
                if (picture != null)
                    return ok(picture).as("image/jpeg");
            }
            return redirect("/assets/images/empty.png");
        });
    }

    // TODO: optimize code
    public Result save() {
        Form<ProductData> form = formFactory.form(ProductData.class);
        final Form<ProductData> boundForm = form.bindFromRequest();
        if (boundForm.hasErrors()) {
            Messages message = play.mvc.Http.Context.current().messages();
            flash("error", message.at("form.error.correction"));
            return badRequest(views.html.products.details.render(boundForm, message));
        }
        ProductData productData = boundForm.get();
        String hexId = session("id");
        Product product = null;
        if (hexId.equals("#"))
            product = new Product();
        else {
            try {
                product = productDao.findById(hexId).toCompletableFuture().get();
            } catch (Throwable throwable) {
                return internalServerError("error");
            }
        }
        product.ean = productData.getEan();
        product.name = productData.getName();
        product.description = productData.getDescription();
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        // b is true if picture downloaded
        boolean b = true;
        play.mvc.Http.MultipartFormData.FilePart<File> part = body.getFile("picture");
        if (part != null && part.getFilename() != null && part.getFilename().length() >0) {
            File file = part.getFile();
            try
            {
                byte[] picture = Files.readAllBytes(file.toPath());
                if (picture != null)
                    product.picture = picture;
            } catch (Throwable throwable) {
                b = false;
                logger.error("Image download error");
                return internalServerError("image download error");
            }
        }
        productDao.save(product);
        Messages message = play.mvc.Http.Context.current().messages();
        if (b)
            flash("success", message.at("product.added.ok", product.ean));
        else
            flash("success", message.at("product.updated.ok", product.ean));
        return redirect(routes.Products.list());
    }

    // curl -X "DELETE" http://localhost:9000/product/10000
    public CompletionStage<Result> delete(String ean) {
        Messages message = play.mvc.Http.Context.current().messages();
        return productDao.findByEan(ean).thenApply(product -> {

            if (product == null) {
                flash("error", message.at("form.error.correction"));
                return notFound(message.at("ean.invalid"));
            }
            productDao.delete(product);
            flash("success", message.at("product.deleted.ok"));
            return redirect(routes.Products.list());
        });
    }
}
