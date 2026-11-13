package LRUCache.catalog;

import java.util.ArrayList;

public class Catagory {

    ArrayList<CatalogCatogory> catalogCatogories = new ArrayList<>();

    public void addSubCatagory(CatalogCatogory catalogCatogory){

        if(catalogCatogory instanceof SubCategory){
            catalogCatogories.add(catalogCatogory);
        }else{
            throw new IllegalArgumentException("Invalid state");
        }

    }

    public void remove(CatalogCatogory catalogCatogory){
        if(catalogCatogory instanceof SubCategory){
            catalogCatogories.remove(catalogCatogory);
        }else{
            throw new IllegalArgumentException("Invalid state");
        }
    }
}
