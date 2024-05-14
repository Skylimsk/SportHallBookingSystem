import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class SportHallBookingApp {
    private static final String USER_CREDENTIALS_FILE = "credentials.txt";
    private static final String BOOKINGS_FILE = "bookings_detail.txt";
    private static Scanner scanner = new Scanner(System.in);
    private static SportHallBookingSystem bookingSystem;

    public static void main(String[] args) {
        bookingSystem = new SportHallBookingSystem("Sport Hall Booking System");

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

    private static void printMainMenu() {
        System.out.println("\n--- " + bookingSystem.getSystemName() + " ---");
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
        System.out.println("\n--- " + bookingSystem.getSystemName() + " ---");
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


        BookingImpl newBooking = new BookingImpl(bookingType, bookingDate, startTime, endTime, user.getName(), user.getId());
        bookingSystem.addBooking(newBooking);

        if (saveBooking(newBooking)) {
            System.out.println("Booking successful!");
        } else {
            System.out.println("Booking failed. Please try again.");
        }
    }

    private static boolean saveBooking(BookingImpl booking) {
        try (FileWriter fileWriter = new FileWriter(BOOKINGS_FILE, true)) {
            String bookingData = booking.getBookingType() + "," + booking.getBookingDate() + "," +
                    booking.getStartTime() + "," + booking.getEndTime() + "," +
                    booking.getBookedBy() + "," + booking.getBookedById() + "\n";
            fileWriter.write(bookingData);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while saving the booking.");
            return false;
        }
    }

    private static void viewBookings(RegisteredUser user) {
        System.out.println("\n--- View Bookings ---");
        ArrayList<BookingImpl> userBookings = getUserBookings(user);
     

        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings.");
        } else {
            System.out.println("Your bookings:");
            for (BookingImpl booking : userBookings) {
                System.out.println("Booking Type: " + booking.getBookingType());
                System.out.println("Booking Date: " + booking.getBookingDate());
                System.out.println("Start Time: " + booking.getStartTime());
                System.out.println("End Time: " + booking.getEndTime());
                System.out.println("Booked By: " + booking.getBookedBy());
                System.out.println("ID: " + booking.getBookedById());
                System.out.println("--------------------");
            }
        }
    }

    private static ArrayList<BookingImpl> getUserBookings(RegisteredUser user) {
        ArrayList<BookingImpl> userBookings = new ArrayList<>();

        try (Scanner fileScanner = new Scanner(new File(BOOKINGS_FILE))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] bookingData = line.split(",");
                if (bookingData.length == 6 && bookingData[4].equals(user.getName()) && bookingData[5].equals(user.getId())) {
                    BookingImpl booking = new BookingImpl(bookingData[0], bookingData[1], bookingData[2], bookingData[3], bookingData[4], bookingData[5]);
                    userBookings.add(booking);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Bookings file not found.");
        }

        return userBookings;
    }

    private static void updateBooking(RegisteredUser user) {
        System.out.println("\n--- Update Booking ---");
        ArrayList<BookingImpl> userBookings = getUserBookings(user);

        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings to update.");
            return;
        }

        System.out.println("Your bookings:");
        for (int i = 0; i < userBookings.size(); i++) {
            BookingImpl booking = userBookings.get(i);
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

        BookingImpl selectedBooking = userBookings.get(choice - 1);
        System.out.println("\nSelected booking:");
        System.out.println("Booking Type: " + selectedBooking.getBookingType());
        System.out.println("Booking Date: " + selectedBooking.getBookingDate());
        System.out.println("Start Time: " + selectedBooking.getStartTime());
        System.out.println("End Time: " + selectedBooking.getEndTime());

        String empty = scanner.nextLine();
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

        selectedBooking = new BookingImpl(newBookingType, newBookingDate, newStartTime, newEndTime, user.getName(), user.getId());
        bookingSystem.removeBooking(userBookings.get(choice - 1));
        bookingSystem.addBooking(selectedBooking);

        if (updateBookingFile(userBookings.get(choice - 1), selectedBooking)) {
            System.out.println("Booking updated successfully!");
        } else {
            System.out.println("Failed to update the booking. Please try again.");
        }
    }

    private static boolean updateBookingFile(BookingImpl oldBooking, BookingImpl newBooking) {
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
                    String updatedBooking = newBooking.getBookingType() + "," +
                            newBooking.getBookingDate() + "," +
                            newBooking.getStartTime() + "," +
                            newBooking.getEndTime() + "," +
                            newBooking.getBookedBy() + "," +
                            newBooking.getBookedById();
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
        ArrayList<BookingImpl> userBookings = getUserBookings(user);

        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings to delete.");
            return;
        }

        System.out.println("Your bookings:");
        for (int i = 0; i < userBookings.size(); i++) {
            BookingImpl booking = userBookings.get(i);
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

        BookingImpl selectedBooking = userBookings.get(choice - 1);
        bookingSystem.removeBooking(selectedBooking);

        if (deleteBookingFromFile(selectedBooking)) {
            System.out.println("Booking deleted successfully!");
        } else {
            System.out.println("Failed to delete the booking. Please try again.");
        }
    }

    private static boolean deleteBookingFromFile(BookingImpl booking) {
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
                    continue;
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
