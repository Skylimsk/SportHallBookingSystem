public class SpecialBooking extends BookingImpl {
    public SpecialBooking(String bookingType, String bookingDate, String startTime, String endTime, String bookedBy, String bookedById) {
        super(bookingType, bookingDate, startTime, endTime, bookedBy, bookedById);
    }

    @Override
    public String getBookingType() {
        return "Special " + super.getBookingType();
    }
}