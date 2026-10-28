package com.roomwise.exception;

/**
 * Thrown when the supplied time range is invalid (e.g., end &lt;= start).
 * Demonstrates: <b>Exception Handling</b> — custom checked exception.
 */
public class InvalidTimeRangeException extends Exception {

    public InvalidTimeRangeException(String message) {
        super(message);
    }
}
