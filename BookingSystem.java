import java.util.ArrayList;

public class BookingSystem {
    private String systemName;
    private ArrayList<BookingImpl> bookings;
 
    public BookingSystem(String systemName) {
        this.systemName = systemName;
        this.bookings = new ArrayList<>();
    }

    public String getSystemName() {
        return systemName;
    }

    public ArrayList<BookingImpl> getBookings() {
        return bookings;
    }

    public void addBooking(BookingImpl booking) {
        bookings.add(booking);
    }

    public void removeBooking(BookingImpl booking) {
        bookings.remove(booking);
    }
}
