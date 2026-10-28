package com.roomwise.dao;

import com.roomwise.exception.BookingConflictException;
import com.roomwise.model.Booking;

/** Data-access contract for conflict-safe booking creation. */
public interface BookingDao {

    /** Creates an approved booking only when the room and requester are free. */
    Booking createApprovedIfAvailable(Booking booking) throws BookingConflictException;
}
