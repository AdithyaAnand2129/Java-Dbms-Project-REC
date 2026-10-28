package com.roomwise.exception;

/**
 * Unchecked wrapper around {@link java.sql.SQLException}.
 *
 * <p>DAO implementations catch {@code SQLException} and rethrow it as this class,
 * so the service layer never leaks JDBC types into its API.</p>
 *
 * <p>Demonstrates: <b>Exception Handling</b> — wrapping low-level infrastructure
 * exceptions in a meaningful domain exception; use of RuntimeException so callers
 * are not forced to catch it (fail-fast approach for unrecoverable DB errors).</p>
 */
public class DatabaseException extends RuntimeException {

    /**
     * @param message human-readable description of what failed
     * @param cause   the underlying {@link java.sql.SQLException}
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
