import dao.ProductDao;
import hexagon.IProductService;
import hexagon.Product;
import hexagon.ProductService;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import play.data.Form;
import play.mvc.Http;
import services.ProductWsPicture;
import views.html.helper.form;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest{


    @Test
    public void loadSample() {
        List<Product> products = new ArrayList<>();

        //Mock
        ProductDao productDaoMock = mock(ProductDao.class);

        ProductService productService = new ProductService(productDaoMock, null);
        productService.loadSample();

        verify(productDaoMock, atLeast(100)).create(any(Product.class));
    }

    @Test
    public void generateMissingPictures() {
        List<Product> products = Arrays.asList(new Product(1, "a", "a"),
                new Product(2, "b", "b"),
                new Product(3, "b", "b", "default"));

        //Mocks
        ProductDao productDaoMock = mock(ProductDao.class);
        when(productDaoMock.findAll()).thenReturn(products);

        ProductWsPicture productWsPictureMock = mock(ProductWsPicture.class);
        when(productWsPictureMock.randomPicture(anyInt())).thenReturn(Optional.of("default-picture.png"));

        //WHEN
        ProductService productService = new ProductService(productDaoMock, productWsPictureMock);
        productService.generateMissingPictures();

        //Then
        System.out.println(products.stream().filter(x->x.picture!=null).collect(Collectors.toList()));
        verify(productDaoMock,times(1)).findAll();
        verify(productWsPictureMock, times(2)).randomPicture(anyLong());
    }

    @Test
    public void generateMissingPicture() {
        //Mocks
        ProductDao productDaoMock = mock(ProductDao.class);

        ProductWsPicture productWsPictureMock = mock(ProductWsPicture.class);
        when(productWsPictureMock.randomPicture(1)).thenReturn(Optional.of("default-picture.png"));

        //WHEN
        ProductService productService = new ProductService(productDaoMock, productWsPictureMock);
        System.out.println(productService.generateMissingPicture(new Product(1, "a", "a")));
        verify(productWsPictureMock, times(1)).randomPicture(anyLong());
    }

}
