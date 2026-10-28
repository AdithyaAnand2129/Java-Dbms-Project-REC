package com.roomwise.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a user's room-booking request.
 *
 * <p>Demonstrates: <b>Encapsulation</b> — private fields; time-range and
 * purpose validated in setters; status transitions controlled.</p>
 */
public class Booking {

    private int           bookingId;
    private int           roomId;
    private int           userId;
    private String        purpose;
    private LocalDate     bookingDate;
    private LocalTime     startTime;
    private LocalTime     endTime;
    private BookingStatus status;

    /**
     * @param bookingId   DB primary key (0 for not-yet-persisted)
     * @param roomId      FK to rooms
     * @param userId      FK to users
     * @param purpose     intended use (must not be blank)
     * @param bookingDate calendar date of the booking
     * @param startTime   start of the requested slot
     * @param endTime     end of the requested slot (must be after startTime)
     * @param status      initial lifecycle state
     */
    public Booking(int bookingId, int roomId, int userId, String purpose,
                   LocalDate bookingDate, LocalTime startTime,
                   LocalTime endTime, BookingStatus status) {
        this.bookingId   = bookingId;
        this.roomId      = roomId;
        this.userId      = userId;
        setPurpose(purpose);
        this.bookingDate = bookingDate;
        setTimeRange(startTime, endTime);
        setStatus(status);
    }

    // ── Validated setters (Encapsulation) ────────────────────────────────────

    /**
     * Updates both times atomically, enforcing end > start.
     * Demonstrates: <b>Encapsulation</b>.
     * @throws IllegalArgumentException if times are null or end is not after start
     */
    public void setTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null)
            throw new IllegalArgumentException("Times must not be null");
        if (!endTime.isAfter(startTime))
            throw new IllegalArgumentException("endTime must be after startTime");
        this.startTime = startTime;
        this.endTime   = endTime;
    }

    /** @throws IllegalArgumentException if purpose is null or blank */
    public void setPurpose(String purpose) {
        if (purpose == null || purpose.isBlank())
            throw new IllegalArgumentException("purpose must not be blank");
        this.purpose = purpose.trim();
    }

    /** @throws IllegalArgumentException if status is null */
    public void setStatus(BookingStatus status) {
        if (status == null) throw new IllegalArgumentException("status must not be null");
        this.status = status;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int           getBookingId()   { return bookingId; }
    public int           getRoomId()      { return roomId; }
    public int           getUserId()      { return userId; }
    public String        getPurpose()     { return purpose; }
    public LocalDate     getBookingDate() { return bookingDate; }
    public LocalTime     getStartTime()   { return startTime; }
    public LocalTime     getEndTime()     { return endTime; }
    public BookingStatus getStatus()      { return status; }

    public void setBookingId(int bookingId)     { this.bookingId = bookingId; }
    public void setBookingDate(LocalDate date)  { this.bookingDate = date; }

    @Override
    public String toString() {
        return String.format(
                "Booking[%d] Room %d | User %d | %s %s–%s | %-30s | %s",
                bookingId, roomId, userId, bookingDate, startTime, endTime, purpose, status);
    }
}
