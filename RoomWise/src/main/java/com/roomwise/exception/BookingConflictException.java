package com.roomwise.exception;

/**
 * Thrown when a booking request conflicts with an existing timetable slot
 * or an APPROVED booking.
 *
 * <p>Demonstrates: <b>Exception Handling</b> — custom checked exception.
 * Callers must either handle it or declare it in their throws clause.</p>
 */
public class BookingConflictException extends Exception {

    /** @param message human-readable description of the conflict */
    public BookingConflictException(String message) {
        super(message);
    }

    /** @param message description of the conflict
     *  @param cause   the underlying exception (if any) */
    public BookingConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
