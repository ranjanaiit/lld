package bmc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class BMCDriver {
    static void main() throws Exception{

        Show show = new Show(1, 12, 3,
                List.of(
                new ShowSeat(1, new Seat(1, SeatType.SILVER, "1")),
                        new ShowSeat(2, new Seat(2, SeatType.SILVER, "2")),
                                new ShowSeat(3, new Seat(3, SeatType.SILVER, "3"))), new Movie(1,"DHURANDHAR"));

        BookingService bookingService = new BookingService(new FixedPricingStrategy(), 5000);

        Booking booking1 =    bookingService.createBooking(show, List.of(1,2));

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Booking> booking2 = executorService.submit(() ->{
           return bookingService.createBooking(show, List.of(1,3));
        });

        Booking bookingA = booking2.get();
        System.out.println("User B Attempt to reserve S1, S3 -> Result: " + (bookingA != null ? bookingA.id : "FAILED (Conflict - Rollback worked)"));


        Thread.sleep(6000);

        Future<Booking> booking3 = executorService.submit(() ->{
            return bookingService.createBooking(show, List.of(1,3));
        });

        Booking bookingC = booking3.get();
        System.out.println("User B Attempt to reserve S1, S3 -> Result: " + (bookingC != null ? bookingC.id : "FAILED (Conflict - Rollback worked)"));

        executorService.shutdown();


    }
}


class BookingService{
    HashMap<String, Booking> bookingMapping = new HashMap<>();

    PricingStrategy  pricingStrategy;
    int ttl;

    public BookingService(PricingStrategy pricingStrategy, int ttl) {
        this.pricingStrategy = new FixedPricingStrategy();
        this.ttl = ttl;
    }




    List<ShowSeat> getAvailableSeat(Show show){
        if(show == null) throw new IllegalArgumentException("Invalid show ID");
       return show.seats();

    }
     Booking createBooking(Show show, List<Integer> seatsId){

        List<ShowSeat> aquiredSeats = new ArrayList<>();

        boolean success = true;
         for(int seatId : seatsId){
             ShowSeat showSeat = show.seats()
                     .stream()
                     .filter(seat -> seat.getId() == seatId)
                     .findFirst()
                     .orElseThrow(() ->
                             new IllegalArgumentException("Seat not found: " + seatId)
                     );
             showSeat.getLock().lock();
             try{
                 if(showSeat.reserve(ttl)){
                     aquiredSeats.add(showSeat);
                 }else{
                     success = false;
                     break;
                 }
             }finally {
                 showSeat.getLock().unlock();
             }}

         if(!success){
             for(ShowSeat showSeat: aquiredSeats){
                 showSeat.getLock().lock();
                 try{
                     showSeat.release();
                 }finally {
                     showSeat.getLock().unlock();
                 }
             }
             return null;
         }

         double amount = 0;

         for(ShowSeat showSeat : aquiredSeats){
             amount += pricingStrategy.calculatePrice(showSeat.getSeat());
         }
         String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
         Booking booking = new Booking(bookingId, show, aquiredSeats, BookingStatus.PENDING, amount);
         bookingMapping.put(bookingId, booking);
         return booking;
    }

    public void confirmBooking(Booking booking){

        if(booking == null) throw new IllegalArgumentException("Booking does not exits");

        synchronized (booking){
            if(booking.status != BookingStatus.PENDING) throw new IllegalStateException("Booking state is not correct");
            if(System.currentTimeMillis() - booking.bookingTime > ttl ){
                booking.cancel();
            }
            booking.confirm();
        }
    }


}

class Booking{
    String id;
    Show show;
    List<ShowSeat> seats;
    BookingStatus status = BookingStatus.FREE;
    long bookingTime;
    double totalAmount;


    public Booking(String id, Show show, List<ShowSeat> seats, BookingStatus status, double totalAmount) {
        this.id = id;
        this.show = show;
        this.seats = seats;
        this.status = status;
        this.totalAmount = totalAmount;
        this.bookingTime = System.currentTimeMillis();
    }

    public void cancel() {
        if(this.status != BookingStatus.PENDING) throw  new IllegalArgumentException("cant cancel as booking was not created");
       this.status = BookingStatus.FREE;
      for(ShowSeat showSeat : seats){
          showSeat.release();
      }
    }

    public void confirm(){
        this.status = BookingStatus.BOOKED;
        for(ShowSeat showSeat : seats){
            showSeat.confirm();
        }
    }
}


enum BookingStatus{
    PENDING,
    FREE,
    BOOKED;
}
class ShowSeat{
    int id;
    Seat seat;
    SeatStatus status = SeatStatus.AVAILABLE;
    ReentrantLock lock = new ReentrantLock();
    long reservationTime =0;

    public ShowSeat(int id, Seat seat) {
        this.id = id;
        this.seat = seat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeatId(Seat seat) {
        this.seat = seat;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public void release(){
        if(status == SeatStatus.HOLD){
            status = SeatStatus.AVAILABLE;
            reservationTime =0;
        }
    }

    public void confirm(){
        if(status == SeatStatus.HOLD){
            status = SeatStatus.BOOKED;
            reservationTime = 0;
        }
    }

    public boolean reserve(int ttl){
        if(getSeatStatus(ttl) == SeatStatus.AVAILABLE){
            status = SeatStatus.HOLD;
            reservationTime = System.currentTimeMillis();
            return  true;
        }
        return false;
    }

    public  SeatStatus getSeatStatus(int ttl){
        if(status == SeatStatus.HOLD &&  System.currentTimeMillis() - reservationTime > ttl){
            status = SeatStatus.AVAILABLE;
            reservationTime =0;
        }
        return status;
    }

}

enum SeatStatus{
    AVAILABLE,
    HOLD,
    BOOKED
}

record Show(int showId,
        int startTime,
        int endTime,
        List<ShowSeat> seats,
        Movie movie) {}

record Movie(int movieId,
             String name){ }


interface PricingStrategy{
    double calculatePrice(Seat seat);
}

class FixedPricingStrategy implements PricingStrategy{

    @Override
    public double calculatePrice(Seat seat) {
        return seat.seatType().getPrice();
    }
}

record Seat(
    int id,
    SeatType seatType,
    String seatNumber
){}

enum SeatType {
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

