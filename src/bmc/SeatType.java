package LRUCache.bmc;

public enum SeatType {
    SILVER(100),
    GOLD(200),
    PLATINUM(500);

    private int price;
    SeatType(int price) {
        this.price = price;

    }

    public int getPrice() {
        return price;
    }
}
