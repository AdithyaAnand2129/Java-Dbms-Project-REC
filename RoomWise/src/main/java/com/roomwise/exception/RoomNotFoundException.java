package com.roomwise.exception;

/**
 * Thrown when a room lookup by ID or code returns no result.
 * Demonstrates: <b>Exception Handling</b> — custom checked exception.
 */
public class RoomNotFoundException extends Exception {

    public RoomNotFoundException(String message) {
        super(message);
    }
}
