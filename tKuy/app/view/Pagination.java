package view;

import hexagon.IProductService;
import hexagon.Product;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;

import static play.mvc.Results.*;

public class Pagination {
    @Inject
    private IProductService productService;
    public Result productsPagination(List<Product> list, int page, int pageSize) {
        int start = ((page/pageSize))*pageSize;
        if(start==0) {
            start++;
        }
        int end = start + pageSize-1;
        int max=productService.numberOfPages(pageSize);
        if(end > max) {
            end=max;
        }
        return ok(views.html.products.render(list, max, pageSize, page, start, end));
    }


}
