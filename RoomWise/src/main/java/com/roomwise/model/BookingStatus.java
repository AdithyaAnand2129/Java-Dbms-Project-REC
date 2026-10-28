package com.roomwise.model;

/**
 * Lifecycle states for a booking request.
 * Maps to the status ENUM column in the bookings table.
 */
public enum BookingStatus {
    /** Submitted but not yet reviewed by admin/faculty. */
    PENDING,
    /** Confirmed — the room is reserved. Conflict checks look for APPROVED bookings. */
    APPROVED,
    /** Declined due to conflict or policy. */
    REJECTED,
    /** Withdrawn by the user who made it. */
    CANCELLED
}
