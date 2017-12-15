package controllers;

import com.google.common.io.Files;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import models.Product;
import models.URLEntity;
import models.WebSearch;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.util.List;

import static org.h2.util.IOUtils.getReader;

@Singleton
public class Products extends Controller {

    @Inject
    FormFactory formFactory;

    public Result removeAsking(String ean) {
        Product p;
        if (null == (p = Product.findByEan(ean)))
            return notFound("product not found");
        return ok(views.html.remove_confirmation.render(formFactory.form(Product.class).fill(p)));
    }

    public Result remove() {
    /*    if(null == (p = Product.findByEan(ean)))
            return notFound("product not found");
        p.delete();
        return list();*/
        return TODO;
    }


    public Result list() {

        return ok(views.html.list.render(Product.findAll()));
    }

    public Result newProduct() {
        Form<Product> productForm = formFactory.form(Product.class);
        return ok(views.html.newproduct.render(productForm));
    }

    public Result details(String ean) {
        Product p;
        if (null == (p = Product.findByEan(ean)))
            return notFound("product not found");
        return ok(views.html.edit.render(formFactory.form(Product.class).fill(p)));
    }

    public Result saveNewProduct() {
        Form<Product> productForm = formFactory.form(Product.class).bindFromRequest();
        if (productForm.hasErrors())
            return badRequest(views.html.newproduct.render(productForm));
        else {
            Product p = productForm.get();

            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart part = body.getFile("picture");

            if (part != null) {
                File file = (File) part.getFile();
                try {
                    p.picture = Files.toByteArray(file);
                } catch (IOException io) {
                    return internalServerError("unable to extract picture");
                }
            }
            if (Product.findByEan(p.ean) != null) {
                flash("success", "Product already exist");
                return redirect(routes.Products.list());
            }
            if (p.id == null || Product.findByEan(p.ean) != null)
                p.save();
            else
                p.update();
            flash("success", "new Product added");
            return redirect(routes.Products.list());
        }
    }

    public Result updateProduct() {
        Form<Product> productForm = formFactory.form(Product.class).bindFromRequest();
        if (productForm.hasErrors())
            return badRequest(views.html.newproduct.render(productForm));
        else {
            Product p = productForm.get();

            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart part = body.getFile("picture");

            if (part != null) {
                File file = (File) part.getFile();
                try {
                    if (Files.toByteArray(file).length != 0)
                        p.picture = Files.toByteArray(file);
                } catch (IOException io) {
                    return internalServerError("unable to extract picture");
                }
            }
            p.update();
            flash("success", "Product edited");
            return redirect(routes.Products.list());
        }
    }

    public Result searchByName(String term) {
        return ok(views.html.searchlist.render(Product.findByName(term)));
    }

    public Result picture(String ean) {
        final Product p = Product.findByEan(ean);
        if (p == null)
            return notFound();
        if (p.picture != null)
            return ok(p.picture);
        return ok(picturePath(ean));
    }

    public String picturePath(String ean) {
        final Product p = Product.findByEan(ean);
        return p.pathLocalPicture;
    }

    public Result loadSamples() {
        Product.flushAll();
        loadSamplesFromCSV();
        return redirect(routes.Products.list());
    }

    private void loadSamplesFromCSV() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");

        for (WebSearch ws : WebSearch.findAll())
            ws.delete();
        for (URLEntity ue : URLEntity.findAll())
            ue.delete();

        CsvParser parser = new CsvParser(settings);
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File("./public/ikea-names.csv"));
            List<String[]> allRows = parser.parseAll(getReader(stream));
            allRows.forEach(strings -> new Product(String.valueOf(strings.hashCode()), strings[0], strings[1], null).save());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public Result allDescriptionOrdered() {
        StringBuilder sb = new StringBuilder();
        Product.allProductsDescriptionsSortedByOccurenceDesc().forEach(elt -> sb.append(elt.toString()).append("\n"));
        return ok(sb.toString());
    }
}