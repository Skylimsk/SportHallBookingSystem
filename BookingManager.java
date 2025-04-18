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
        assert bookingType != null : "Pre-condition violation: Booking type cannot be null";
        if (!bookingType.matches(VALID_BOOKING_TYPES)) {
            throw new InvalidInputException("Invalid booking type: " + bookingType);
        }

        // PRE-CONDITION 2: Validate date format
        assert bookingDate != null : "Pre-condition violation: Booking date cannot be null";
        if (!bookingDate.matches(DATE_PATTERN)) {
            throw new InvalidInputException("Invalid date format. Use DD/MM/YYYY");
        }

        // PRE-CONDITION 3: Validate time format
        assert startTime != null && endTime != null :
               "Pre-condition violation: Start time and end time cannot be null";
        if (!startTime.matches(TIME_PATTERN) || !endTime.matches(TIME_PATTERN)) {
            throw new InvalidInputException("Invalid time format. Use HH:MM");
        }

        // PRE-CONDITION 4: Ensure end time is after start time
        assert endTime.compareTo(startTime) != 0 :
               "Pre-condition violation: End time cannot be the same as start time";
        if (!isEndTimeAfterStartTime(startTime, endTime)) {
            throw new InvalidInputException("End time must be after start time");
        }

        // PRE-CONDITION 5: Validate user information
        assert bookedBy != null && bookedById != null :
               "Pre-condition violation: User information cannot be null";
        if (bookedBy.trim().isEmpty() || bookedById.trim().isEmpty()) {
            throw new InvalidInputException("User name and ID must not be empty");
        }

        // PRE-CONDITION 6: Check for overlapping bookings
        int initialSize = bookings.size();
        if (hasOverlappingBookings(bookingDate, startTime, endTime)) {
            throw new InvalidInputException("There is already a booking for this time slot");
        }

        // PROCESS: Create and add booking
        Booking newBooking = new BookingImpl(bookingType, bookingDate, startTime, endTime, bookedBy, bookedById);
        bookings.add(newBooking);

        // POST-CONDITION 1: Check booking was added
        assert bookings.contains(newBooking) :
               "Post-condition violation: Booking was not added successfully";

        // POST-CONDITION 2: Check booking count increased
        assert bookings.size() == initialSize + 1 :
               "Post-condition violation: Booking count did not increase";

        // POST-CONDITION 3: Check booking is retrievable by user ID
        try {
            ArrayList<Booking> userBookings = getBookingsByUser(bookedById);
            assert userBookings.contains(newBooking) :
                   "Post-condition violation: Booking not retrievable by user ID";
        } catch (InvalidInputException e) {
            assert false : "Post-condition violation: Error retrieving user bookings";
        }

        // INVARIANT: Check all bookings have valid time periods
        checkInvariant();

        //System.out.println("Booking for " + bookingType + " on " + bookingDate +
        //                 " from " + startTime + " to " + endTime + " added successfully.");
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
        assert userId != null : "Pre-condition violation: User ID cannot be null";
        if (userId.trim().isEmpty()) {
            throw new InvalidInputException("User ID must not be empty");
        }

        ArrayList<Booking> userBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getBookedById().equals(userId)) {
                userBookings.add(booking);
            }
        }

        // POST-CONDITION: Each booking belongs to the specified user
        for (Booking booking : userBookings) {
            assert booking.getBookedById().equals(userId) :
                   "Post-condition violation: Retrieved booking doesn't match user ID";
        }

        // INVARIANT: Check all bookings have valid time periods
        checkInvariant();

        return userBookings;
    }

    public void deleteBooking(Booking bookingToDelete) throws InvalidInputException {
        // PRE-CONDITION 1: Check booking is not null
        assert bookingToDelete != null : "Pre-condition violation: Booking to delete cannot be null";

        // PRE-CONDITION 2: Check if booking exists
        assert bookings.contains(bookingToDelete) :
               "Pre-condition violation: Booking does not exist in the system";
        if (!bookings.contains(bookingToDelete)) {
            throw new InvalidInputException("Booking does not exist in the system");
        }

        int beforeSize = bookings.size();
        bookings.remove(bookingToDelete);

        // POST-CONDITION 1: Check booking was removed
        assert !bookings.contains(bookingToDelete) :
               "Post-condition violation: Booking was not removed";

        // POST-CONDITION 2: Check booking count decreased
        assert bookings.size() == beforeSize - 1 :
               "Post-condition violation: Booking count did not decrease";

        // INVARIANT: Check all remaining bookings have valid time periods
        checkInvariant();

        System.out.println("Booking for " + bookingToDelete.getBookingType() +
                         " on " + bookingToDelete.getBookingDate() +
                         " has been successfully deleted.");
    }

    private void checkInvariant() {
        // INVARIANT 1: All bookings must have end time after start time
        for (Booking booking : bookings) {
            assert isEndTimeAfterStartTime(booking.getStartTime(), booking.getEndTime()) :
                   "Invariant violation: Invalid time period in booking - End time not after start time";
        }

        // INVARIANT 2: Maximum booking length should not exceed 6 hours
        for (Booking booking : bookings) {
            String startTime = booking.getStartTime();
            String endTime = booking.getEndTime();

            int startHour = Integer.parseInt(startTime.split(":")[0]);
            int endHour = Integer.parseInt(endTime.split(":")[0]);

            int duration = endHour - startHour;
            assert duration <= 6 :
                   "Invariant violation: Booking duration exceeds maximum allowed (6 hours)";
        }

        // INVARIANT 3: No duplicate bookings (same type, date, time, user)
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking1 = bookings.get(i);
            for (int j = i + 1; j < bookings.size(); j++) {
                Booking booking2 = bookings.get(j);

                boolean sameType = booking1.getBookingType().equals(booking2.getBookingType());
                boolean sameDate = booking1.getBookingDate().equals(booking2.getBookingDate());
                boolean sameTime = booking1.getStartTime().equals(booking2.getStartTime()) &&
                                 booking1.getEndTime().equals(booking2.getEndTime());
                boolean sameUser = booking1.getBookedById().equals(booking2.getBookedById());

                assert !(sameType && sameDate && sameTime && sameUser) :
                       "Invariant violation: Duplicate booking detected";
            }
        }
    }

    public int getBookingCount() {
        return bookings.size();
    }

    public ArrayList<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}