package com.roomwise.ui;

import com.roomwise.exception.BookingConflictException;
import com.roomwise.model.Booking;
import com.roomwise.model.BookingStatus;
import com.roomwise.model.Room;
import com.roomwise.service.AvailabilityService;
import com.roomwise.service.BookingService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Text interface for RoomWise's first milestone.
 * Demonstrates: <b>package separation</b>; this UI calls services only and
 * never imports a DAO or executes SQL.
 */
public class ConsoleMenu {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    private final AvailabilityService availabilityService;
    private final BookingService bookingService;
    private final Scanner input;

    /** Creates the console UI with its service-layer dependencies. */
    public ConsoleMenu(AvailabilityService availabilityService, BookingService bookingService) {
        this.availabilityService = availabilityService;
        this.bookingService = bookingService;
        this.input = new Scanner(System.in);
    }

    /** Starts the interactive menu until the user selects Exit. */
    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            switch (readLine("Choose an option: ")) {
                case "1" -> showAvailableNow();
                case "2" -> searchPeriod();
                case "3" -> createBooking();
                case "0" -> running = false;
                default -> System.out.println("Please enter 1, 2, 3, or 0.");
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println("\n=== RoomWise ===");
        System.out.println("1. Find rooms available now");
        System.out.println("2. Search rooms for a date and time range");
        System.out.println("3. Create an approved booking");
        System.out.println("0. Exit");
    }

    private void showAvailableNow() {
        Integer capacity = readPositiveInteger("Minimum capacity: ");
        if (capacity != null) {
            printRooms(availabilityService.search(capacity));
        }
    }

    private void searchPeriod() {
        try {
            LocalDate date = LocalDate.parse(readLine("Date (YYYY-MM-DD): "));
            LocalTime start = parseTime("Start time (HH:MM): ");
            LocalTime end = parseTime("End time (HH:MM): ");
            Integer capacity = readPositiveInteger("Minimum capacity: ");
            if (capacity != null) {
                printRooms(availabilityService.search(date, start, end, capacity));
            }
        } catch (DateTimeParseException exception) {
            System.out.println("Use YYYY-MM-DD for dates and H:MM or HH:MM for times.");
        } catch (IllegalArgumentException exception) {
            System.out.println("Search failed: " + exception.getMessage());
        }
    }

    private void createBooking() {
        try {
            Integer roomId = readPositiveInteger("Room ID: ");
            Integer userId = readPositiveInteger("Your user ID: ");
            if (roomId == null || userId == null) {
                return;
            }
            String purpose = readLine("Purpose: ");
            LocalDate date = LocalDate.parse(readLine("Date (YYYY-MM-DD): "));
            LocalTime start = parseTime("Start time (HH:MM): ");
            LocalTime end = parseTime("End time (HH:MM): ");
            Booking request = new Booking(0, roomId, userId, purpose, date, start, end, BookingStatus.PENDING);
            Booking approved = bookingService.createBooking(request);
            System.out.println("Booking approved. Booking ID: " + approved.getBookingId());
        } catch (DateTimeParseException exception) {
            System.out.println("Use YYYY-MM-DD for dates and H:MM or HH:MM for times.");
        } catch (BookingConflictException exception) {
            System.out.println("Booking rejected: " + exception.getMessage());
        } catch (IllegalArgumentException exception) {
            System.out.println("Booking failed: " + exception.getMessage());
        }
    }

    private void printRooms(List<Room> rooms) {
        if (rooms.isEmpty()) {
            System.out.println("No matching rooms are available.");
            return;
        }
        System.out.println("Available rooms:");
        rooms.forEach(room -> System.out.printf("  ID %d — %s%n", room.getRoomId(), room));
    }

    private Integer readPositiveInteger(String prompt) {
        try {
            int value = Integer.parseInt(readLine(prompt));
            if (value < 1) {
                throw new NumberFormatException();
            }
            return value;
        } catch (NumberFormatException exception) {
            System.out.println("Enter a positive whole number.");
            return null;
        }
    }

    private LocalTime parseTime(String prompt) {
        return LocalTime.parse(readLine(prompt), TIME_FORMAT);
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }
}
