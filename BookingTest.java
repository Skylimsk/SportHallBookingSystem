// SC - PS3 Question 2
public class BookingTest {
    public static void main(String[] args) {
        BookingManager manager = new BookingManager();

        try {
            // Valid booking
            manager.addBooking("Badminton", "19/04/2025", "10:00", "12:00",
                               "John Doe", "U123");

            // Invalid booking: same start and end time (Post-condition violation)
            manager.addBooking("Squash", "19/04/2025", "10:00", "10:00",
                               "Jane Smith", "U124");

        } catch (InvalidInputException e) {
            System.out.println("Caught InvalidInputException: " + e.getMessage());
        } catch (AssertionError ae) {
            System.out.println("Assertion failed: " + ae.getMessage());
        }

        try {
            // Violate invariant: Duplicate booking
            manager.addBooking("Badminton", "19/04/2025", "10:00", "12:00",
                               "John Doe", "U123");
        } catch (InvalidInputException | AssertionError e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            // View bookings for a user
            var userBookings = manager.getBookingsByUser("U123");
            System.out.println("Retrieved " + userBookings.size() + " booking(s) for user U123.");
        } catch (InvalidInputException | AssertionError e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("Total bookings: " + manager.getBookingCount());
    }
}

