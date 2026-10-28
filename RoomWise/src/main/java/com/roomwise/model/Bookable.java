package com.roomwise.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents any campus entity that can be booked.
 *
 * <p>Demonstrates: <b>Abstraction</b> — defines a contract without
 * providing implementation; both Room and (optionally) Equipment
 * can implement this interface.</p>
 */
public interface Bookable {

    /**
     * Checks whether this entity is free for the requested slot.
     * The actual DB lookup is performed by the service layer;
     * this method provides a local, object-level guard.
     *
     * @param date  calendar date of the requested booking
     * @param start start time of the requested slot
     * @param end   end time of the requested slot (must be after start)
     * @return true if the entity is locally considered available
     */
    boolean isAvailable(LocalDate date, LocalTime start, LocalTime end);

    /**
     * Returns the list of purposes this entity permits for bookings.
     * Demonstrates: runtime polymorphism when called through the Bookable type.
     *
     * @return immutable list of allowed purpose strings
     */
    List<String> getAllowedPurposes();
}
