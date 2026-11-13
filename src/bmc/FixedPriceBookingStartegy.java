package LRUCache.bmc;

import java.util.List;

public class FixedPriceBookingStartegy implements BookingStrategy{
    @Override
    public int calculatePrice(List<Seat> seats) {
        return seats.stream().mapToInt(seat -> seat.seatType.getPrice()).sum();
    }
}
