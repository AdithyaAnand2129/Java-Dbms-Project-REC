package com.roomwise;

import com.roomwise.dao.JdbcBookingDao;
import com.roomwise.dao.JdbcRoomDao;
import com.roomwise.service.AvailabilityService;
import com.roomwise.service.BookingService;
import com.roomwise.ui.ConsoleMenu;

/**
 * Application entry point and composition root. It creates infrastructure
 * dependencies, then hands service objects to the UI.
 */
public final class Main {

    private Main() {
    }

    /** Starts the RoomWise console application. */
    public static void main(String[] arguments) {
        AvailabilityService availabilityService = new AvailabilityService(new JdbcRoomDao());
        BookingService bookingService = new BookingService(new JdbcBookingDao());
        new ConsoleMenu(availabilityService, bookingService).start();
    }
}
