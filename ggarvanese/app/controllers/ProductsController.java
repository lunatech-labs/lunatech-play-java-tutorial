package controllers;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import models.Product;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.ImageDownloadService;
import services.ProductService;
import views.html.detail;
import views.html.editproductpage;
import views.html.newproduct;
import views.html.products;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

public class ProductsController extends Controller {

    private final FormFactory formFactory;

    private final ProductService productService;

    @Inject
    public ProductsController(FormFactory formFactory,
                              ProductService productService,
                              ImageDownloadService imageDownloadService) {
        this.formFactory = formFactory;
        this.productService = productService;
    }

    public Result findAll() {
        return ok(products.render(productService.findAll()));
    }

    public Result detail(String ean) {
        Optional<Product> product = productService.findByEAN(ean);
        if (product.isPresent()) {
            return ok(detail.render(product.get()));
        }
        return badRequest(ean + " as not been found");
    }

    public Result newProduct() {
        Form<Product> form = formFactory.form(Product.class);
        if(form.hasErrors()){
            return badRequest(newproduct.render(form));
        }
        return ok(newproduct.render(form));
    }

    public Result submitNewProduct() {
        Form<Product> form = formFactory.form(Product.class);
        form = form.bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(newproduct.render(form));
        }
        Product product = form.get();
        productService.create(product);
        flash("successAdded", "Product " + product.ean + " as been added");
        return ok(products.render(productService.findAll()));
    }

    public Result editProduct(String ean) {
        return productService.findByEAN(ean)
                .map(product -> ok(editproductpage.render(formFactory.form(Product.class).fill(product))))
                .orElseGet(() -> notFound("Le produit " + ean + " n'a pas été trouvé"));
    }

    public Result submitEditProduct() {
        Form<Product> form = formFactory.form(Product.class);
        form = form.bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(editproductpage.render(form));
        }
        Product product = form.get();
        productService.update(product);
        flash("successEdit", "Product " + product.ean + " as been edited");
        return ok(products.render(productService.findAll()));
    }

    public Result findByCriteria(String searchType, String term){
        if(searchType != null && term != null){
            return ok(products.render(productService.findByCriteria(searchType, term)));
        }
        return badRequest("bad request");
    }

    public Result deleteProduct(String ean) {
        Optional<Product> product = productService.findByEAN(ean);
        if (product.isPresent()) {
            productService.delete(product.get());
            return ok(products.render(productService.findAll()));
        }
        return badRequest(ean + " as not been found");
    }

    public Result loadSamples() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);
        try {
            parser.beginParsing(getReader("/public/hikea-names.csv"));
            String[] row;
            while ((row = parser.parseNext()) != null) {
                Product product = new Product();
                product.setName(row[0]);
                product.setDescription(row[1]);
                productService.create(product);
            }
            parser.stopParsing();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ok(products.render(productService.findAll()));
    }

    public Result flushDB(){
        List<Product> list = productService.findAll();
        list.forEach( p -> productService.delete(p));
        return redirect(routes.ProductsController.findAll());
    }

    private Reader getReader(String relativePath) throws UnsupportedEncodingException {
        return new InputStreamReader(this.getClass().getResourceAsStream(relativePath), "UTF-8");
    }
}