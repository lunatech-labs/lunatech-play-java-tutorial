package controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import hexagon.IProductService;
import hexagon.Product;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import view.Pagination;

import javax.inject.Inject;
import java.util.List;

public class Products extends Controller {
    private final static int PAGE_SIZE=20;
    @Inject
    private FormFactory formFactory;
    @Inject
    private IProductService productService;
    @Inject
    private Pagination pagination;

    public Result generatingMissingPicturesTask() {
        return list();
    }

    public Result list() {
        return listPaginated(1);
    }
    public Result newProduct() {
        Form<Product> productForm = formFactory.form(Product.class);
        return ok(views.html.createProduct.render(productForm));
    }

    public Result create() {
        Form<Product> form = formFactory.form(Product.class).bindFromRequest();
        if(form.hasErrors()) {
            return badRequest(views.html.createProduct.render(form));
        } else {
            productService.createProduct(form.get(), request());
        }
        return redirect(routes.Products.list());
    }
    public Result detail(Long ean) {
        return productService.productById(ean).map(product -> ok(views.html.detail.render(product))).orElse(notFound());
    }

    public Result delete(String ean) {
        productService.deleteProduct(Long.valueOf(ean));
        return redirect(routes.Products.list());
    }

    public Result loadSamples() {
        productService.loadSample();
        return redirect(routes.Products.list());
    }
    public Result generatingMissingPictures() {
        productService.generateMissingPictures();
        return redirect(routes.Products.list());
    }
    @JsonFormat
    public Result productsAsJson() {
        return ok(Json.toJson(productService.allProducts()));
    }

    public Result listPaginated(int page) {
        List<Product> list = productService.productListByPage(page, PAGE_SIZE);
        return pagination.productsPagination(list, page, PAGE_SIZE);
    }
}
