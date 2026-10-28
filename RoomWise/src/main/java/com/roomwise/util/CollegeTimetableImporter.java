package com.roomwise.util;

import com.roomwise.exception.DatabaseException;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Imports the supplied theory and lab exports into the normalized RoomWise
 * schema using only prepared statements.
 *
 * <p>Usage: {@code CollegeTimetableImporter theory.json labs.json}. Run
 * {@code schema.sql} first and configure {@code db.properties} before use.</p>
 */
public final class CollegeTimetableImporter {

    private static final DateTimeFormatter SOURCE_TIME = DateTimeFormatter.ofPattern("H:mm", Locale.ROOT);

    private CollegeTimetableImporter() {
    }

    /**
     * Imports both complete college exports in one database transaction.
     *
     * @param arguments theory-file path followed by lab-file path
     */
    public static void main(String[] arguments) {
        if (arguments.length != 2) {
            System.err.println("Usage: CollegeTimetableImporter <theory.json> <labs.json>");
            return;
        }
        try {
            List<Map<String, String>> theory = TimetableDataParser.parse(Path.of(arguments[0]));
            List<Map<String, String>> labs = TimetableDataParser.parse(Path.of(arguments[1]));
            importData(theory, "THEORY");
            importData(labs, "LAB");
            System.out.printf("Imported %d theory and %d lab timetable records.%n", theory.size(), labs.size());
        } catch (IOException exception) {
            throw new DatabaseException("Could not read timetable data: " + exception.getMessage(), exception);
        }
    }

    private static void importData(List<Map<String, String>> records, String sourceKind) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (Map<String, String> record : records) {
                    importRecord(connection, record, sourceKind);
                }
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Could not import timetable data", exception);
        }
    }

    private static void importRecord(Connection connection, Map<String, String> record, String sourceKind) throws SQLException {
        int buildingId = ensureBuilding(connection, required(record, "block"));
        int roomId = ensureRoom(connection, buildingId, record, sourceKind);
        int facultyId = ensureFaculty(connection, record);
        int courseId = ensureCourse(connection, record);
        int offeringId = ensureOffering(connection, record, sourceKind, courseId, facultyId);
        insertSlot(connection, record, sourceKind, roomId, offeringId);
    }

    private static int ensureBuilding(Connection connection, String name) throws SQLException {
        return upsertAndId(connection, "INSERT INTO buildings (name, location, total_floors) VALUES (?, ?, 1) "
                + "ON DUPLICATE KEY UPDATE building_id = LAST_INSERT_ID(building_id)", name, "College campus");
    }

    private static int ensureRoom(Connection connection, int buildingId, Map<String, String> record, String sourceKind) throws SQLException {
        int capacity = positiveInteger(record.get(sourceKind.equals("LAB") ? "capacity" : "student_count"), 1);
        String type = sourceKind.equals("LAB") ? "LAB" : "CLASSROOM";
        int collegeRoomId = positiveInteger(record.get("room_id"), 0);
        String sql = "INSERT INTO rooms (building_id, college_room_id, room_code, floor, capacity, room_type) "
                + "VALUES (?, ?, ?, 0, ?, ?) ON DUPLICATE KEY UPDATE room_id = LAST_INSERT_ID(room_id), "
                + "capacity = GREATEST(capacity, VALUES(capacity))";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, buildingId);
            statement.setInt(2, collegeRoomId);
            statement.setString(3, required(record, "room_number"));
            statement.setInt(4, capacity);
            statement.setString(5, type);
            statement.executeUpdate();
        }
        return lastInsertId(connection);
    }

    private static int ensureFaculty(Connection connection, Map<String, String> record) throws SQLException {
        int teacherId = positiveInteger(record.get("teacher_id"), 0);
        String staffCode = record.get("staff_code");
        String sql = "INSERT INTO users (name, role, college_teacher_id, staff_code) VALUES (?, 'FACULTY', ?, ?) "
                + "ON DUPLICATE KEY UPDATE user_id = LAST_INSERT_ID(user_id), name = VALUES(name)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, required(record, "teacher_name"));
            statement.setInt(2, teacherId);
            statement.setString(3, staffCode);
            statement.executeUpdate();
        }
        return lastInsertId(connection);
    }

    private static int ensureCourse(Connection connection, Map<String, String> record) throws SQLException {
        return upsertAndId(connection, "INSERT INTO courses (course_code, name) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE course_id = LAST_INSERT_ID(course_id), name = VALUES(name)",
                required(record, "course_code"), required(record, "course_name"));
    }

    private static int ensureOffering(Connection connection, Map<String, String> record, String sourceKind,
                                      int courseId, int facultyId) throws SQLException {
        String sourceInstance = record.get("course_instance_id");
        if (sourceInstance == null) {
            sourceInstance = required(record, "course_code") + "-" + required(record, "group_name");
        }
        String sql = "INSERT INTO course_offerings (source_instance_id, course_id, faculty_id, group_name, department, semester, schedule_type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE offering_id = LAST_INSERT_ID(offering_id)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sourceInstance);
            statement.setInt(2, courseId);
            statement.setInt(3, facultyId);
            statement.setString(4, required(record, "group_name"));
            statement.setString(5, required(record, "department"));
            statement.setInt(6, positiveInteger(record.get("semester"), 1));
            statement.setString(7, sourceKind);
            statement.executeUpdate();
        }
        return lastInsertId(connection);
    }

    private static void insertSlot(Connection connection, Map<String, String> record, String sourceKind,
                                   int roomId, int offeringId) throws SQLException {
        String range = required(record, sourceKind.equals("LAB") ? "time_range" : "time_slot");
        String[] times = range.split("\\s*-\\s*", 2);
        if (times.length != 2) {
            throw new SQLException("Invalid timetable time range: " + range);
        }
        LocalTime start = LocalTime.parse(times[0].trim(), SOURCE_TIME);
        LocalTime end = LocalTime.parse(times[1].trim(), SOURCE_TIME);
        // The export omits AM/PM. A range such as 11:40 - 1:20 is the
        // afternoon session (13:20), not an overnight booking.
        if (!end.isAfter(start) && end.getHour() < 12) {
            end = end.plusHours(12);
        }
        if (!end.isAfter(start)) {
            throw new SQLException("Invalid timetable time range: " + range);
        }
        String sql = "INSERT INTO timetable_slots (room_id, offering_id, day_of_week, start_time, end_time, source_kind, source_slot_label) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            statement.setInt(2, offeringId);
            statement.setString(3, parseDay(required(record, "day")).name());
            statement.setObject(4, start);
            statement.setObject(5, end);
            statement.setString(6, sourceKind);
            statement.setString(7, record.get(sourceKind.equals("LAB") ? "session_name" : "slot_index"));
            statement.executeUpdate();
        }
    }

    private static int upsertAndId(Connection connection, String sql, String first, String second) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, first);
            statement.setString(2, second);
            statement.executeUpdate();
        }
        return lastInsertId(connection);
    }

    private static int lastInsertId(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT LAST_INSERT_ID()"); ResultSet results = statement.executeQuery()) {
            results.next();
            return results.getInt(1);
        }
    }

    private static DayOfWeek parseDay(String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "mon", "monday" -> DayOfWeek.MONDAY;
            case "tue", "tuesday" -> DayOfWeek.TUESDAY;
            case "wed", "wednesday" -> DayOfWeek.WEDNESDAY;
            case "thu", "thur", "thursday" -> DayOfWeek.THURSDAY;
            case "fri", "friday" -> DayOfWeek.FRIDAY;
            case "sat", "saturday" -> DayOfWeek.SATURDAY;
            case "sun", "sunday" -> DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Unknown timetable day: " + value);
        };
    }

    private static int positiveInteger(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return (int) Double.parseDouble(value);
    }

    private static String required(Map<String, String> record, String field) {
        String value = record.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required timetable field: " + field);
        }
        return value.trim();
    }
}
