package LRUCache.catalog;

import java.util.ArrayList;
import java.util.List;

public class Product implements CatalogCatogory{
    int price;

    @Override
    public int getProductCount() {
        return 1;
    }

    @Override
    public List<CatalogCatogory> getDetails() {
        return null;
    }
}
