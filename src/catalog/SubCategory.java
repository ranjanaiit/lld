package LRUCache.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SubCategory implements CatalogCatogory{

//    ReadWriteLock lock = new ReentrantReadWriteLock();
    ArrayList<CatalogCatogory> categories = new ArrayList<>();
    SubCatagoryType type;
    public SubCategory(SubCatagoryType type){
        this.type = type;
    }

    public void addSubCatagory(CatalogCatogory catalogCatogory){
        if(type == SubCatagoryType.PRODUCT && catalogCatogory instanceof Product){
            categories.add(catalogCatogory);
            return;
        }
        if(type == SubCatagoryType.SUB_CATAGORY_TYPE && catalogCatogory instanceof SubCategory){
            categories.add(catalogCatogory);
            return;
        }
        throw  new IllegalArgumentException("Different type provided");


    }

    public void remove(CatalogCatogory catalogCatogory){
        if(!categories.contains(catalogCatogory)){
            throw new IllegalArgumentException("this cataogory is not present");
        }
        categories.remove(catalogCatogory);
    }

    @Override
    public int getProductCount() {
        int sum = 0;
       for(CatalogCatogory catogory : categories){
           sum+=catogory.getProductCount();
       }
        return sum;
    }

    @Override
    public List<CatalogCatogory> getDetails() {

        return null;

    }
}
