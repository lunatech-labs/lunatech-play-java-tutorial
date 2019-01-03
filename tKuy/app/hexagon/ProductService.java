package hexagon;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Http;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
@Singleton
public class ProductService implements IProductService {

    private IProductDao productDao;
    private IProductWsPicture productWsPicture;

    @Inject
    public ProductService(IProductDao productDao, IProductWsPicture productWsPicture) {
        this.productDao = productDao;
        this.productWsPicture = productWsPicture;
    }
    @Override
    public boolean createProduct(Form<Product> form, Http.Request request) {
        Messages messages = Http.Context.current().messages();
        if (form.hasErrors()) {
            Logger.error(messages.at("error.create.form"));
            return false;
        }
        Product product = form.get();
        File file = fileFromRequest(request);
        if (file != null) {
            try {
                product.picture = copyToStoreFolder(file);
            } catch (IOException e) {
                Logger.error(messages.at("picture.not.copied"));
                return false;
            }
        }
        if (!productDao.create(product)) {
            Logger.error(messages.at("ean.already.used"));
            return false;
        }
        return true;
    }
    private String copyToStoreFolder(File file) throws IOException {
        File saveFile = new File("public/images/store", file.getName() + ".png");
        Path createdFile = Files.copy(file.toPath(), saveFile.toPath());
        return createdFile.toFile().getName();
    }
    private File fileFromRequest(Http.Request request) {
        Http.MultipartFormData<File> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> picture = body.getFile("picture");
        if (picture != null) {
            File file = picture.getFile();
            return file;
        } else {
            return null;
        }
    }
    @Override
    public void loadSample() {
        productDao.flushAll();
        Path csvFile = Paths.get("public/ikea-names.csv");
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);
        parser.beginParsing(csvFile.toFile());
        String[] row;
        long cpt = 0;
        while ((row = parser.parseNext()) != null) {
            Product product = new Product(cpt, row[0], row[1]);
            cpt++;
            productDao.create(product);
        }
        parser.stopParsing();
    }
    @Override
    public void generateMissingPictures() {
        productDao.findAll().stream()
                .filter(p -> p.picture == null)
                .forEach(this::generateMissingPicture);
    }
    @Override
    public Product generateMissingPicture(Product product) {
        productWsPicture.randomPicture(product.ean).ifPresent(p -> {
            product.picture = p;
            productDao.update(product);
        });
        return product;
    }

    @Override
    public void updateProduct(Product product) {
        productDao.update(product);
    }


    @Override
    public List<Product> allProducts() {
        return productDao.findAll();
    }

    @Override
    public Optional<Product> productById(long ean) {
        return productDao.findProductById(ean);
    }

    @Override
    public void deleteProduct(long ean) {
        productDao.delete(ean);
    }

    @Override
    public List<Product> productListByPage(int page, int pageSize) {
        return productDao.paginated(page, pageSize);
    }

    @Override
    public int numberOfPages(int pageSize) {
        double nbProduct = Double.valueOf(productDao.numberOfProducts());
        return (int)Math.ceil(nbProduct/pageSize);
    }

}
