package controllers;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import dto.ProductDto;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.ProductService;
import views.html.product;
import views.html.productDetail;
import views.html.productForm;

import javax.inject.Inject;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class ProductController extends Controller {

    private static final int DEFAULT_LENGTH = 10;
    private final FormFactory formFactory;
    private final ProductService productService;

    @Inject
    ProductController(FormFactory formFactory, ProductService productService) {
        this.formFactory = formFactory;
        this.productService = productService;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index(Integer page, String description) {
        Messages messages = Http.Context.current().messages();

        final Map<String, String[]> entries = request().queryString();

//        int page = 0;
//        String description = "";
        int length = DEFAULT_LENGTH;
//        if (!entries.isEmpty()) {
//            if (entries.get("page") != null) {
//                page = Integer.valueOf(entries.get("page")[0]);
//            }
//            if (entries.get("description") != null) {
//                description = entries.get("description")[0];
//            }
//        }

        return ok(product.render(
                messages,
                this.productService.findPage(page, length, description),
                this.productService.count(description),
                page,
                length,
                description));
    }

    public Result filter() {
        final DynamicForm form = this.formFactory.form().bindFromRequest();
        String searchStr = form.get("search");

        return this.index(0, searchStr);
    }

    public Result getUpdate(Long ean) {
        ProductDto result = this.productService.findByEan(Long.valueOf(ean));

        if (result != null) {
            Form<ProductDto> form = this.formFactory.form(ProductDto.class).fill(result);
            return ok(productDetail.render(form));
        }

        return badRequest("Product doesn't exist");
    }

    public Result modify() {
        Form<ProductDto> form = this.formFactory.form(ProductDto.class).bindFromRequest();

        if (form.hasErrors()) {
            return badRequest(views.html.productDetail.render(form));
        } else {
            ProductDto productFormDetail = form.value().get();

            ProductDto result = this.productService.findByEan(productFormDetail.getEan());

            if (result != null) {
                // Set data to the actual object.
                result.setName(productFormDetail.getName());
                result.setDescription(productFormDetail.getDescription());

                this.productService.update(result);

                return redirect(routes.ProductController.index(0, ""));
            }
        }

        return badRequest("Server error");
    }

    public Result getCreateNew() {
        Form<ProductDto> form = this.formFactory.form(ProductDto.class);
        return ok(productForm.render(form));
    }

    public Result setCreateNew() {
        final DynamicForm form = this.formFactory.form().bindFromRequest();
        Integer ean = Integer.valueOf(form.get("ean"));
        String name = form.get("name");
        String description = form.get("description");

        if (form.hasErrors()) {
            Logger.error("Product setCreateNew errors");
            return badRequest();
        } else {
            if (ean == 0) { // Create new
                ProductDto product = new ProductDto(ean, name, description);

                this.productService.save(product);
            } else {
                ProductDto product = this.productService.findByEan(ean);
                product.setDescription(description);
                product.setName(name);

                this.productService.update(product);
            }

            return redirect(routes.ProductController.index(0, ""));
        }
    }

    public Result delete(Long ean) {
        ProductDto product = this.productService.findByEan(Long.valueOf(ean));

        if (product != null) {
            // Set data to the actual object.
            this.productService.delete(product);

            return redirect(routes.ProductController.index(0, ""));
        }

        return badRequest("Server error");
    }

    public Result loadSamples() throws UnsupportedEncodingException {
        CsvParserSettings settings = new CsvParserSettings();

        //the file used in the example uses '\n' as the line separator sequence.
        //the line separator sequence is defined here to ensure systems such as MacOS and Windows
        //are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
        settings.getFormat().setLineSeparator("\n");

        // creates a CSV parser
        CsvParser parser = new CsvParser(settings);

        // parses all rows in one go.
        List<String[]> allRows = parser.parseAll(new InputStreamReader(this.getClass().getResourceAsStream("/public/hikea-names.csv"), "UTF-8"));

        for (String[] oneRow : allRows) {
            this.productService.save(new ProductDto(0, oneRow[0], oneRow[1], null));
        }

        return redirect(routes.ProductController.index(0, ""));
    }
}
