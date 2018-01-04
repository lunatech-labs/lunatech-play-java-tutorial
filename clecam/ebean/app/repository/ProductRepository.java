package repository;

import com.google.inject.ImplementedBy;
import java.util.List;
import java.util.concurrent.CompletionStage;
import models.Product;


@ImplementedBy(EbeanProductRepository.class)
public interface ProductRepository {

    public abstract CompletionStage<String> eraseAll();

    public abstract CompletionStage<String> loadCsv(String fileRelativePath);

    public abstract CompletionStage<List<Product>> findAll();

    public abstract CompletionStage<Product> findById(Long id);

    public abstract CompletionStage<Product> findByEan(String ean);

    public abstract CompletionStage<List<Product>> findByName(String name);

    public abstract CompletionStage<List<Product>> findByDescription(String description);

    /*
     * return true if new product.
     */
}
