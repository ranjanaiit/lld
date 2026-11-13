package LRUCache.bmc;

import java.util.List;

public interface BookingStrategy {
    int calculatePrice(List<Seat> seats);
}
