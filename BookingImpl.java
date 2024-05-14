public class BookingImpl implements Booking {
    private String bookingType;
    private String bookingDate;
    private String startTime;
    private String endTime;
    private String bookedBy;
    private String bookedById;

    public BookingImpl(String bookingType, String bookingDate, String startTime, String endTime, String bookedBy, String bookedById) {
        this.bookingType = bookingType;
        this.bookingDate = bookingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookedBy = bookedBy;
        this.bookedById = bookedById;
    }

    public String getBookingType() {
        return bookingType;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getBookedById() {
        return bookedById;
    }
     // Method overloading
    public void setBookingTime(String startTime){
        this.startTime = startTime;
    }

    public void setBookingTime(String startTime, String endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }
}