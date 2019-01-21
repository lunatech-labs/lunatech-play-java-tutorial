package services;

import com.google.inject.Singleton;
import dto.ProductDto;
import models.Product;
import models.ProductDao;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class ProductService {

    @Inject
    ProductDao productDao;

    public ProductService() {
        // Usually build by Injection
    }

    public ProductDto findByEan(long ean) {
        return toProductDto(this.productDao.findByEan(ean));
    }

    public ProductDto toProductDto(Product product) {
        return new ProductDto(product.getEan(), product.getName(), product.getDescription(), product.getUrl());
    }

    public List<ProductDto> findPage(int page, int length, String description) {
        return this.productDao.findPage(page, length, description).stream().map(product -> toProductDto(product)).collect(Collectors.toList());
    }

    public int count(String description) {
        return this.productDao.count(description);
    }

    public void save(ProductDto product) {
        this.productDao.save(new Product(product.getEan(), product.getName(), product.getDescription(), product.getUrl()));
    }

    public void update(ProductDto product) {
        this.productDao.update(new Product(product.getEan(), product.getName(), product.getDescription(), product.getUrl()));
    }

    public void delete(ProductDto product) {
        this.productDao.delete(new Product(product.getEan(), product.getName(), product.getDescription(), product.getUrl()));
    }

    public List<ProductDto> findByDescription(String description) {
        return this.productDao.findByDescription(description)
                .stream()
                .map(product -> toProductDto(product))
                .collect(Collectors.toList());
    }

    public List<ProductDto> findByDescriptionAndUrlIsEmpty(String searchTerm) {
        return this.productDao.findByDescriptionAndUrlIsEmpty(searchTerm)
                .stream()
                .map(product -> toProductDto(product))
                .collect(Collectors.toList());
    }
}
