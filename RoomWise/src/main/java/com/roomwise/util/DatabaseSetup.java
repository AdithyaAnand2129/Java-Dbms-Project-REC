package com.roomwise.util;

import com.roomwise.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Creates the RoomWise database objects from the project's schema file.
 * It is safe to run repeatedly because the schema uses {@code IF NOT EXISTS}.
 */
public final class DatabaseSetup {

    private DatabaseSetup() {
    }

    /** Runs every statement in {@code schema.sql} against the local MySQL server. */
    public static void main(String[] arguments) {
        try {
            Properties properties = loadProperties();
            String serverUrl = serverUrl(properties.getProperty("db.url"));
            try (Connection connection = DriverManager.getConnection(serverUrl,
                    properties.getProperty("db.user"), properties.getProperty("db.password"));
                 Statement statement = connection.createStatement()) {
                if (arguments.length == 1 && "--reset".equals(arguments[0])) {
                    statement.execute("DROP DATABASE IF EXISTS roomwise_db");
                }
                for (String sql : removeLineComments(readResource("schema.sql")).split(";")) {
                    if (!sql.trim().isEmpty()) {
                        try {
                            statement.execute(sql);
                        } catch (SQLException exception) {
                            if (exception.getErrorCode() != 1061) {
                                throw exception;
                            }
                        }
                    }
                }
            }
            System.out.println("RoomWise database schema is ready.");
        } catch (IOException | SQLException exception) {
            throw new DatabaseException("Could not create RoomWise database", exception);
        }
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream stream = DatabaseSetup.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (stream == null) {
                throw new IOException("db.properties is missing");
            }
            properties.load(stream);
        }
        return properties;
    }

    private static String readResource(String name) throws IOException {
        try (InputStream stream = DatabaseSetup.class.getClassLoader().getResourceAsStream(name)) {
            if (stream == null) {
                throw new IOException(name + " is missing");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String serverUrl(String databaseUrl) {
        int queryStart = databaseUrl.indexOf('?');
        String query = queryStart < 0 ? "" : databaseUrl.substring(queryStart);
        String withoutQuery = queryStart < 0 ? databaseUrl : databaseUrl.substring(0, queryStart);
        return withoutQuery.substring(0, withoutQuery.lastIndexOf('/') + 1) + query;
    }

    private static String removeLineComments(String sql) {
        return sql.replaceAll("(?m)^\\s*--.*$", "");
    }
}
