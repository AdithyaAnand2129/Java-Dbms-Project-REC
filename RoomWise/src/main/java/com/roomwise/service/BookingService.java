package com.roomwise.service;

import com.roomwise.dao.BookingDao;
import com.roomwise.exception.BookingConflictException;
import com.roomwise.model.Booking;

import java.util.Objects;

/** Business-facing booking service; it delegates persistence to the DAO layer. */
public class BookingService {
    private final BookingDao bookingDao;

    /** Creates the service with a conflict-safe booking DAO. */
    public BookingService(BookingDao bookingDao) {
        this.bookingDao = Objects.requireNonNull(bookingDao, "bookingDao must not be null");
    }

    /** Creates an approved booking or reports a timetable, room, or teacher conflict. */
    public Booking createBooking(Booking booking) throws BookingConflictException {
        return bookingDao.createApprovedIfAvailable(booking);
    }
}
