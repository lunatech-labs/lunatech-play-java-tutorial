package controllers;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import models.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.*;
import views.html.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


@Singleton
public class Products extends Controller {

    private final FormFactory formFactory;

    @Inject
    public Products(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    @Inject
    public SearchImageService searchImageService;

    @Inject
    public DownloaderImageService downloaderImageService;

    @Inject
    public UpdaterProductService updaterProductService;

    private final static String catalogFilePath = "public/datas/ikea-names.csv";

    public Result loadSamples() {
        Product.flushAll();

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);

        parser.beginParsing(new java.io.File(catalogFilePath));

        String[] row;
        while ((row = parser.parseNext()) != null) {
            final String ean = RandomStringUtils.randomAlphanumeric(10);
            final String name = row[0];
            final String description = row[1];
            final Product p = new Product(ean,name,description,null,null);
            p.save();
        }
        parser.stopParsing();
        flash("success", "Catalog loaded from CSV file");
        return redirect(routes.Products.list());
    }

    public Result list() {
        List<Product> products = Product.findAll();
        return ok(list.render(products));
    }

    public Result listSearch(String query) {
        List<Product> products = Product.findByQuery(query);
        return ok(list.render(products));
    }

    public Result newProduct() {
        Form<Product> form = formFactory.form(Product.class);
        return ok(newProduct.render(form));
    }

    public Result modifyProduct(String ean) {
        Form<Product> form = formFactory.form(Product.class);

        Product product = Product.findByEan(ean);
        if(product == null) {
            return notFound(String.format("Product with ean %s not found", ean));
        } else {
            form = form.fill(product);
            return ok(modifyProduct.render(ean, form));
        }
    }

    public Result details(String ean) {
        Product product = Product.findByEan(ean);
        if(product == null) {
            return notFound(String.format("Product with ean %s not found", ean));
        } else {
            return ok(details.render(product));
        }
    }

    public Result images() {
        return ok(images.render(formFactory.form()));
    }

    public Result downloadPicture(String ean)  {
        Product product = Product.findByEan(ean);
        ByteArrayInputStream input;

        if(product == null || product.picture == null) {
            return notFound(String.format("Picture of ean %s not found", ean));
        } else {
            input = new ByteArrayInputStream(product.picture);
        }

        return ok(input).as("image/jpeg");
    }

    public Result saveNewProduct() {
        Form<Product> form = formFactory.form(Product.class).bindFromRequest();

        if(form.hasErrors()) {
            return badRequest(newProduct.render(form));
        } else {
            Product product = form.get();

            try {
                product.picture = uploadPicture();
            } catch (IOException ioe) {
                return internalServerError("Unable to extract picture");
            }

            product.add();
            flash("success", "New product created");
            return redirect(routes.Products.list());
        }
    }

    public Result saveModifiedProduct(String oldEan) {
        Form<Product> form = formFactory.form(Product.class).bindFromRequest();

        if(form.hasErrors()) {
            return badRequest(newProduct.render(form));
        } else {
            Product product = form.get();

            try {
                product.picture = uploadPicture();
            } catch (IOException ioe) {
                return internalServerError("Unable to extract picture");
            }

            Product oldProduct = Product.findByEan(oldEan);

            if(oldProduct != null)
                oldProduct.remove();

            product.add();
            flash("success", "Product modified");
            return redirect(routes.Products.list());
        }
    }

    public Result saveDeleteProduct(String ean) {
        Product product = Product.findByEan(ean);
        if(product == null) {
            return notFound(String.format("Product with ean %s not found", ean));
        } else {
            product.remove();
            flash("success", "Product deleted");
            return redirect(routes.Products.list());
        }
    }

    public CompletionStage<Result> searchImage() {
        final Http.Flash flash = flash();
        final DynamicForm form = formFactory.form().bindFromRequest();
        final String action = form.get("action");

        if("searchImage".equals(action)) {
            final String query = form.get("query");

            if(query == null || query.isEmpty()) {
                form.reject("query", "Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(images.render(form)));
            }

            return searchImageService.search(query)
                    .thenApply(searchFileName -> {
                        flash.put("success", String.format("File search: %s", searchFileName));
                        return redirect(routes.Products.images());
                    })
                    .exceptionally(e -> {
                        flash.put("error", e.getCause().getMessage());
                        return redirect(routes.Products.images());
                    });

        } else if("searchDownloadImage".equals(action)) {
            final String query = form.get("query2");

            if(query == null || query.isEmpty()) {
                form.reject("query2","Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(images.render(form)));
            }

            return searchImageService.search(query)
                    .thenCompose(searchFileName -> downloaderImageService.download(query, searchFileName))
                    .thenApply(data -> {
                        flash.put("success", String.format("File %s is created ==> directory %s is created with %d image(s)",
                                data.searchFileName, data.imgDirectory, data.imgDownloaded));
                        return redirect(routes.Products.images());
                    })
                    .exceptionally(e -> {
                        flash.put("error", e.getCause().getMessage());
                        return redirect(routes.Products.images());
                    });

        } else if("searchDownloadUpdateImage".equals(action)) {
            final String query = form.get("query3");

            if(query == null || query.isEmpty()) {
                form.reject("query3","Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(images.render(form)));
            }

            return searchImageService.search(query)
                    .thenCompose(searchFileName -> downloaderImageService.download(query, searchFileName))
                    .thenCompose(data -> updaterProductService.update(query, data))
                    .thenApply(data -> {
                        flash.put("success", String.format("File %s is created ==> Directory %s is created with %d image(s) ==> %d product(s) updated",
                                data.searchFileName, data.imgDirectory, data.imgDownloaded, data.productUpdated));
                        return redirect(routes.Products.images());
                    })
                    .exceptionally(e -> {
                        flash.put("error", e.getCause().getMessage());
                        return redirect(routes.Products.images());
                    });

        } else if("searchProducts".equals(action)) {
            final String query = form.get("query4");

            if(query == null || query.isEmpty()) {
                form.reject("query4","Query can't be empty");
                return CompletableFuture.completedFuture(badRequest(images.render(form)));
            }

            return CompletableFuture.completedFuture(redirect(routes.Products.listSearch(query)));

        } else {
            return CompletableFuture.completedFuture(badRequest("No action found: "+action));
        }
    }

    @Nullable
    private byte[] uploadPicture() throws IOException {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        if(body!=null) {
            Http.MultipartFormData.FilePart file = body.getFile("picture");

            if (file != null) {
                File picture = (File) file.getFile();
                return com.google.common.io.Files.toByteArray(picture);
            }
        }
      
        return null;
    }

}
