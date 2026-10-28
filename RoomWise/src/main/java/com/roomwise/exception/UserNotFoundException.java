package com.roomwise.exception;

/**
 * Thrown when a user lookup by ID or email returns no result.
 * Demonstrates: <b>Exception Handling</b> — custom checked exception.
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }
}
