package catalog.stockmanagement;

import java.util.concurrent.atomic.AtomicInteger;

public class Inventory {
    private String inventoryId;
    private String ProductId;
    private String wareHouseId;
    private AtomicInteger availableStock = new AtomicInteger(0);


    public int getInvetory(){
       return availableStock.get();
    }


    public void BookIfAvailable(int required){

        if(required <=0 ){
            throw new IllegalArgumentException("Required quantity is not valid");
        }

        while (true){
            int currentAvailable = availableStock.get();

            if(required > currentAvailable){
                throw new IllegalStateException("Insufficient stock");
            }
            if(availableStock.compareAndSet(currentAvailable, currentAvailable - required)){
                return;
            }
        }
    }


}
