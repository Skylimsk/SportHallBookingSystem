import java.util.ArrayList;

public class BookingManager {
    private ArrayList<Booking> bookings;
    private static final String VALID_BOOKING_TYPES = "Basketball|Badminton|Squash|Table Tennis";
    private static final String DATE_PATTERN = "\\d{2}/\\d{2}/\\d{4}";
    private static final String TIME_PATTERN = "\\d{2}:\\d{2}";

    public BookingManager() {
        this.bookings = new ArrayList<>();
    }

    public void addBooking(String bookingType, String bookingDate, String startTime, 
                          String endTime, String bookedBy, String bookedById) 
                          throws InvalidInputException {
        
        // PRE-CONDITION 1: Validate booking type
        if (!bookingType.matches(VALID_BOOKING_TYPES)) {
            throw new InvalidInputException("Invalid booking type: " + bookingType);
        }
        
        // PRE-CONDITION 2: Validate date format
        if (!bookingDate.matches(DATE_PATTERN)) {
            throw new InvalidInputException("Invalid date format. Use DD/MM/YYYY");
        }
        
        // PRE-CONDITION 3: Validate time format
        if (!startTime.matches(TIME_PATTERN) || !endTime.matches(TIME_PATTERN)) {
            throw new InvalidInputException("Invalid time format. Use HH:MM");
        }
        
        // PRE-CONDITION 4: Ensure end time is after start time
        if (!isEndTimeAfterStartTime(startTime, endTime)) {
            throw new InvalidInputException("End time must be after start time");
        }
        
        // PRE-CONDITION 5: Validate user information
        if (bookedBy.trim().isEmpty() || bookedById.trim().isEmpty()) {
            throw new InvalidInputException("User name and ID must not be empty");
        }
        
        // PRE-CONDITION 6: Check for overlapping bookings
        if (hasOverlappingBookings(bookingDate, startTime, endTime)) {
            throw new InvalidInputException("There is already a booking for this time slot");
        }
        
        // PROCESS: Create and add booking
        Booking newBooking = new BookingImpl(bookingType, bookingDate, startTime, endTime, bookedBy, bookedById);
        bookings.add(newBooking);
        
        // POST-CONDITION: Check booking was added
        assert bookings.contains(newBooking) : "Booking was not added successfully";
        assert getBookingsByUser(bookedById).contains(newBooking) : "Booking not retrievable by user ID";
        
        // INVARIANT: Check all bookings have valid time periods
        checkInvariant();
    }
    
    private boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        return endTime.compareTo(startTime) > 0;
    }
    
    private boolean hasOverlappingBookings(String date, String startTime, String endTime) {
        for (Booking booking : bookings) {
            if (booking.getBookingDate().equals(date)) {
                boolean startsBeforeOtherEnds = startTime.compareTo(booking.getEndTime()) < 0;
                boolean endsAfterOtherStarts = endTime.compareTo(booking.getStartTime()) > 0;
                
                if (startsBeforeOtherEnds && endsAfterOtherStarts) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public ArrayList<Booking> getBookingsByUser(String userId) throws InvalidInputException {
        // PRE-CONDITION: Check user ID
        if (userId == null || userId.trim().isEmpty()) {
            throw new InvalidInputException("User ID must not be empty");
        }
        
        ArrayList<Booking> userBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getBookedById().equals(userId)) {
                userBookings.add(booking);
            }
        }
        
        // POST-CONDITION check
        for (Booking booking : userBookings) {
            assert booking.getBookedById().equals(userId) : "Retrieved booking doesn't match user ID";
        }
        
        return userBookings;
    }
    
    public void deleteBooking(Booking bookingToDelete) throws InvalidInputException {
        // PRE-CONDITION: Check if booking exists
        if (!bookings.contains(bookingToDelete)) {
            throw new InvalidInputException("Booking does not exist in the system");
        }
        
        int beforeSize = bookings.size();
        bookings.remove(bookingToDelete);
        
        // POST-CONDITION checks
        assert !bookings.contains(bookingToDelete) : "Booking was not removed";
        assert bookings.size() == beforeSize - 1 : "Booking count did not decrease";
        
        // INVARIANT check
        checkInvariant();
        
        System.out.println("Booking for " + bookingToDelete.getBookingType() + 
                          " on " + bookingToDelete.getBookingDate() + 
                          " has been successfully deleted.");
    }
    
    private void checkInvariant() {
        for (Booking booking : bookings) {
            assert isEndTimeAfterStartTime(booking.getStartTime(), booking.getEndTime()) : 
                   "Invariant violation: Invalid time period in booking";
        }
    }
    
    public int getBookingCount() {
        return bookings.size();
    }
    
    public ArrayList<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}