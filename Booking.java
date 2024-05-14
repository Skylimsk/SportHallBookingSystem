// Interface for sport hall bookings
public interface Booking {
    String getBookingType();
    String getBookingDate();
    String getStartTime();
    String getEndTime();
    String getBookedBy();
    String getBookedById();
}