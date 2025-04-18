import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SportHallBookingApp {
    private static final String USER_CREDENTIALS_FILE = "credentials.txt";
    private static final String BOOKINGS_FILE = "bookings_detail.txt";
    private static Scanner scanner = new Scanner(System.in);
    private static BookingManager bookingManager;
    private static String systemName = "Sport Hall Booking System";

    public static void main(String[] args) {
        bookingManager = new BookingManager();
        loadExistingBookings();

        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Load existing bookings from file into the BookingManager
     * Silently load bookings without printing success messages
     */
    private static void loadExistingBookings() {
        try (Scanner fileScanner = new Scanner(new File(BOOKINGS_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] bookingData = line.split(",");
                    if (bookingData.length == 6) {
                        try {
                            // Add the booking silently without printing success message
                            bookingManager.addBooking(
                                bookingData[0],   // bookingType
                                bookingData[1],   // bookingDate
                                bookingData[2],   // startTime
                                bookingData[3],   // endTime
                                bookingData[4],   // bookedBy
                                bookingData[5]    // bookedById
                            );
                        } catch (InvalidInputException e) {
                            System.out.println("Warning: Invalid booking found in file: " + line);
                            System.out.println("Reason: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Bookings file not found. A new one will be created when needed.");
        }
    }

    private static void printMainMenu() {
        System.out.println("\n--- " + systemName + " ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
    }

    private static int getUserChoice() {
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }

    private static void registerUser() {
        scanner.nextLine();
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        System.out.print("Enter your ID: ");
        String id = scanner.next();

        System.out.print("Enter your password: ");
        String password = scanner.next();

        RegisteredUser newUser = new RegisteredUser(name, id, password);

        if (saveUserCredentials(newUser)) {
            System.out.println("Registration successful!");
            loginUser();
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private static boolean saveUserCredentials(RegisteredUser user) {
        try (FileWriter fileWriter = new FileWriter(USER_CREDENTIALS_FILE, true)) {
            String userCredentials = user.getName() + "," + user.getId() + "," + user.getPassword() + "\n";
            fileWriter.write(userCredentials);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while saving user credentials.");
            return false;
        }
    }

    private static void loginUser() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter your ID: ");
        String id = scanner.next();

        System.out.print("Enter your password: ");
        String password = scanner.next();

        RegisteredUser user = authenticateUser(id, password);
        if (user != null) {
            System.out.println("Login successful!");
            handleUserOperations(user);
        } else {
            System.out.println("Invalid ID or password. Please try again.");
        }
    }

    private static RegisteredUser authenticateUser(String id, String password) {
        try (Scanner fileScanner = new Scanner(new File(USER_CREDENTIALS_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] userData = line.split(",");
                if (userData.length == 3 && userData[1].equals(id) && userData[2].equals(password)) {
                    return new RegisteredUser(userData[0], userData[1], userData[2]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User credentials file not found.");
        }
        return null;
    }

    private static void handleUserOperations(RegisteredUser user) {
        boolean logout = false;
        while (!logout) {
            printUserMenu(user);
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    bookSportHall(user);
                    break;
                case 2:
                    viewBookings(user);
                    break;
                case 3:
                    updateBooking(user);
                    break;
                case 4:
                    deleteBooking(user);
                    break;
                case 5:
                    logout = true;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void printUserMenu(RegisteredUser user) {
        System.out.println("\n--- " + systemName + " ---");
        System.out.println("Welcome, " + user.getName() + "!");
        System.out.println("1. Book Sport Hall");
        System.out.println("2. View Bookings");
        System.out.println("3. Update Booking");
        System.out.println("4. Delete Booking");
        System.out.println("5. Logout");
    }

    private static void bookSportHall(RegisteredUser user) {
        scanner.nextLine();
        System.out.println("\n--- Book Sport Hall --- \n");
        System.out.println("===================");
        System.out.println("Basketball" + "\nBadminton" + "\nSquash" + "\nTable Tennis");
        System.out.println("===================");
        System.out.print("Enter booking type: ");
        String bookingType = scanner.nextLine();

        System.out.print("Enter booking date (e.g., DD/MM/YYYY): ");
        String bookingDate = scanner.next();

        System.out.print("Enter start time (e.g., HH:MM): ");
        String startTime = scanner.next();

        System.out.print("Enter end time (e.g., HH:MM): ");
        String endTime = scanner.next();

        try {
            bookingManager.addBooking(bookingType, bookingDate, startTime, endTime, user.getName(), user.getId());

            if (saveBooking(bookingType, bookingDate, startTime, endTime, user.getName(), user.getId())) {
                System.out.println("\n====================");
                System.out.println("Booking successful!");
                System.out.println("Your " + bookingType + " court has been booked successfully for "
                                 + bookingDate + " from " + startTime + " to " + endTime + ".");
                System.out.println("====================\n");
            } else {
                System.out.println("Booking was validated but could not be saved to file. Please try again.");
            }
        } catch (InvalidInputException e) {
            System.out.println("Booking failed: " + e.getMessage());
        }
    }

    private static boolean saveBooking(String bookingType, String bookingDate, String startTime,
                                     String endTime, String bookedBy, String bookedById) {
        try (FileWriter fileWriter = new FileWriter(BOOKINGS_FILE, true)) {
            String bookingData = bookingType + "," + bookingDate + "," +
                    startTime + "," + endTime + "," +
                    bookedBy + "," + bookedById + "\n";
            fileWriter.write(bookingData);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while saving the booking.");
            return false;
        }
    }

    private static void viewBookings(RegisteredUser user) {
        System.out.println("\n--- View Bookings ---");

        try {
            ArrayList<Booking> userBookings = bookingManager.getBookingsByUser(user.getId());

            if (userBookings.isEmpty()) {
                System.out.println("You have no bookings.");
            } else {
                System.out.println("Your bookings:");
                int index = 1;
                for (Booking booking : userBookings) {
                    System.out.println("Booking #" + (index++));
                    System.out.println("Booking Type: " + booking.getBookingType());
                    System.out.println("Booking Date: " + booking.getBookingDate());
                    System.out.println("Start Time: " + booking.getStartTime());
                    System.out.println("End Time: " + booking.getEndTime());
                    System.out.println("Booked By: " + booking.getBookedBy());
                    System.out.println("ID: " + booking.getBookedById());
                    System.out.println("--------------------");
                }
            }
        } catch (InvalidInputException e) {
            System.out.println("Error retrieving bookings: " + e.getMessage());
        }
    }

    private static void updateBooking(RegisteredUser user) {
        System.out.println("\n--- Update Booking ---");

        try {
            ArrayList<Booking> userBookings = bookingManager.getBookingsByUser(user.getId());

            if (userBookings.isEmpty()) {
                System.out.println("You have no bookings to update.");
                return;
            }

            System.out.println("Your bookings:");
            for (int i = 0; i < userBookings.size(); i++) {
                Booking booking = userBookings.get(i);
                System.out.println((i + 1) + ". Booking Type: " + booking.getBookingType() +
                        " | Booking Date: " + booking.getBookingDate() +
                        " | Start Time: " + booking.getStartTime() +
                        " | End Time: " + booking.getEndTime());
            }

            System.out.print("Enter the number of the booking you want to update: ");
            int choice = scanner.nextInt();

            if (choice < 1 || choice > userBookings.size()) {
                System.out.println("Invalid choice! Please try again.");
                return;
            }

            Booking selectedBooking = userBookings.get(choice - 1);
            System.out.println("\nSelected booking:");
            System.out.println("Booking Type: " + selectedBooking.getBookingType());
            System.out.println("Booking Date: " + selectedBooking.getBookingDate());
            System.out.println("Start Time: " + selectedBooking.getStartTime());
            System.out.println("End Time: " + selectedBooking.getEndTime());

            scanner.nextLine(); // Consume the newline
            System.out.println("\nEnter new details:");
            System.out.println("===================");
            System.out.println("Basketball" + "\nBadminton" + "\nSquash" + "\nTable Tennis");
            System.out.println("===================");
            System.out.print("Enter booking type: ");
            String newBookingType = scanner.nextLine();

            System.out.print("Enter booking date (e.g., DD/MM/YYYY): ");
            String newBookingDate = scanner.next();

            System.out.print("Enter start time (e.g., HH:MM): ");
            String newStartTime = scanner.next();

            System.out.print("Enter end time (e.g., HH:MM): ");
            String newEndTime = scanner.next();

            // First remove the old booking
            bookingManager.deleteBooking(selectedBooking);

            // Then add the new one
            try {
                bookingManager.addBooking(newBookingType, newBookingDate, newStartTime, newEndTime,
                                        user.getName(), user.getId());

                if (updateBookingFile(selectedBooking, newBookingType, newBookingDate, newStartTime,
                                    newEndTime, user.getName(), user.getId())) {
                    System.out.println("Booking updated successfully!");
                } else {
                    System.out.println("Failed to update the booking file. Please try again.");
                }
            } catch (InvalidInputException e) {
                System.out.println("Update failed: " + e.getMessage());
                // Try to add the old booking back
                try {
                    bookingManager.addBooking(
                        selectedBooking.getBookingType(),
                        selectedBooking.getBookingDate(),
                        selectedBooking.getStartTime(),
                        selectedBooking.getEndTime(),
                        selectedBooking.getBookedBy(),
                        selectedBooking.getBookedById()
                    );
                } catch (InvalidInputException ex) {
                    System.out.println("Error restoring previous booking. Please check your bookings.");
                }
            }
        } catch (InvalidInputException e) {
            System.out.println("Error retrieving bookings: " + e.getMessage());
        }
    }

    private static boolean updateBookingFile(Booking oldBooking, String newBookingType,
                                          String newBookingDate, String newStartTime,
                                          String newEndTime, String bookedBy, String bookedById) {
        try {
            File bookingsFile = new File(BOOKINGS_FILE);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(bookingsFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookingData = line.split(",");
                if (bookingData.length == 6 &&
                        bookingData[0].equals(oldBooking.getBookingType()) &&
                        bookingData[1].equals(oldBooking.getBookingDate()) &&
                        bookingData[2].equals(oldBooking.getStartTime()) &&
                        bookingData[3].equals(oldBooking.getEndTime()) &&
                        bookingData[4].equals(oldBooking.getBookedBy()) &&
                        bookingData[5].equals(oldBooking.getBookedById())) {
                    String updatedBooking = newBookingType + "," +
                            newBookingDate + "," +
                            newStartTime + "," +
                            newEndTime + "," +
                            bookedBy + "," +
                            bookedById;
                    writer.write(updatedBooking + "\n");
                } else {
                    writer.write(line + "\n");
                }
            }

            reader.close();
            writer.close();

            if (bookingsFile.delete()) {
                return tempFile.renameTo(bookingsFile);
            } else {
                System.out.println("Failed to update the bookings file.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the bookings file.");
            return false;
        }
    }

    private static void deleteBooking(RegisteredUser user) {
        System.out.println("\n--- Delete Booking ---");

        try {
            ArrayList<Booking> userBookings = bookingManager.getBookingsByUser(user.getId());

            if (userBookings.isEmpty()) {
                System.out.println("You have no bookings to delete.");
                return;
            }

            System.out.println("Your bookings:");
            for (int i = 0; i < userBookings.size(); i++) {
                Booking booking = userBookings.get(i);
                System.out.println((i + 1) + ". Booking Type: " + booking.getBookingType() +
                        " | Booking Date: " + booking.getBookingDate() +
                        " | Start Time: " + booking.getStartTime() +
                        " | End Time: " + booking.getEndTime());
            }

            System.out.print("Enter the number of the booking you want to delete: ");
            int choice = scanner.nextInt();

            if (choice < 1 || choice > userBookings.size()) {
                System.out.println("Invalid choice! Please try again.");
                return;
            }

            Booking selectedBooking = userBookings.get(choice - 1);

            try {
                bookingManager.deleteBooking(selectedBooking);

                if (deleteBookingFromFile(selectedBooking)) {
                    System.out.println("Booking deleted successfully!");
                } else {
                    System.out.println("Failed to delete the booking from file. Please try again.");
                }
            } catch (InvalidInputException e) {
                System.out.println("Delete failed: " + e.getMessage());
            }
        } catch (InvalidInputException e) {
            System.out.println("Error retrieving bookings: " + e.getMessage());
        }
    }

    private static boolean deleteBookingFromFile(Booking booking) {
        try {
            File bookingsFile = new File(BOOKINGS_FILE);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(bookingsFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] bookingData = line.split(",");
                if (bookingData.length == 6 &&
                        bookingData[0].equals(booking.getBookingType()) &&
                        bookingData[1].equals(booking.getBookingDate()) &&
                        bookingData[2].equals(booking.getStartTime()) &&
                        bookingData[3].equals(booking.getEndTime()) &&
                        bookingData[4].equals(booking.getBookedBy()) &&
                        bookingData[5].equals(booking.getBookedById())) {
                    continue; // Skip this line
                }
                writer.write(line + "\n");
            }

            reader.close();
            writer.close();

            if (bookingsFile.delete()) {
                return tempFile.renameTo(bookingsFile);
            } else {
                System.out.println("Failed to delete the booking from the bookings file.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the booking from the bookings file.");
            return false;
        }
    }
}