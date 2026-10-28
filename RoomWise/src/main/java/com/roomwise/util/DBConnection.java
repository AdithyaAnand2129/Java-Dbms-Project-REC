package com.roomwise.util;

import com.roomwise.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory that provides JDBC {@link Connection} objects to the DAO layer.
 * Credentials are read from {@code db.properties} on the classpath — never hard-coded.
 *
 * <p><b>OOP concepts demonstrated here:</b>
 * <ul>
 *   <li><b>Encapsulation</b> — private constructor prevents instantiation;
 *       the static factory method is the only way to obtain a connection.</li>
 *   <li><b>Exception Handling</b> — {@link IOException} and {@link SQLException}
 *       are caught and rethrown as {@link DatabaseException}; try-with-resources
 *       used to close the Properties stream.</li>
 *   <li><b>Packages</b> — lives in {@code com.roomwise.util}; imported by DAO
 *       implementations only, never by the service or UI layers directly.</li>
 * </ul>
 * </p>
 *
 * <h3>How to set up {@code db.properties}</h3>
 * <pre>
 * Copy src/main/resources/db.properties.example
 *   to src/main/resources/db.properties
 * Fill in the credentials that match your .env file.
 * </pre>
 */
public final class DBConnection {

    /** Utility class — no instances allowed. */
    private DBConnection() {}

    /**
     * Opens and returns a new JDBC connection for each call.
     * <p>Callers must close the connection when done (use try-with-resources).</p>
     *
     * @return a live, open {@link Connection} to the MySQL database
     * @throws DatabaseException if the connection cannot be established
     */
    public static Connection getConnection() {
        try {
            Properties props = loadProperties();
            String url      = props.getProperty("db.url");
            String user     = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            if (url == null || url.isBlank())
                throw new DatabaseException("db.url is missing in db.properties", null);

            return DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            throw new DatabaseException(
                    "Could not connect to the database. " +
                    "Is Docker running? (docker-compose up -d) Error: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new DatabaseException("Failed to read db.properties: " + e.getMessage(), e);
        }
    }

    // ── Private helper ────────────────────────────────────────────────────────

    /**
     * Loads {@code db.properties} from the classpath.
     * Demonstrates: try-with-resources (Exception Handling).
     *
     * @return loaded {@link Properties} object
     * @throws IOException if the file is missing or unreadable
     */
    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        // try-with-resources: InputStream is closed automatically — Demonstrates Exception Handling
        try (InputStream is = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new IOException(
                        "db.properties not found on classpath. " +
                        "Copy db.properties.example -> db.properties and fill in your credentials.");
            }
            props.load(is);
        }
        return props;
    }
}
