package com.roomwise.dao;

import com.roomwise.exception.BookingConflictException;
import com.roomwise.exception.DatabaseException;
import com.roomwise.model.Booking;
import com.roomwise.model.BookingStatus;
import com.roomwise.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** JDBC booking DAO. SQL and transaction details stay out of the service/UI layers. */
public class JdbcBookingDao implements BookingDao {

    @Override
    public Booking createApprovedIfAvailable(Booking booking) throws BookingConflictException {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                lockRoom(connection, booking);
                if (hasTimetableConflict(connection, booking) || hasBookingConflict(connection, booking)
                        || hasTeacherConflict(connection, booking)) {
                    connection.rollback();
                    throw new BookingConflictException("The room or requester is already occupied during this period.");
                }
                insertApprovedBooking(connection, booking);
                connection.commit();
                return booking;
            } catch (BookingConflictException exception) {
                connection.rollback();
                throw exception;
            } catch (SQLException exception) {
                connection.rollback();
                throw new DatabaseException("Could not create booking", exception);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Could not open booking transaction", exception);
        }
    }

    private void lockRoom(Connection connection, Booking booking) throws SQLException, BookingConflictException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT room_id FROM rooms WHERE room_id = ? FOR UPDATE")) {
            statement.setInt(1, booking.getRoomId());
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    throw new BookingConflictException("Room does not exist: " + booking.getRoomId());
                }
            }
        }
    }

    private boolean hasTimetableConflict(Connection connection, Booking booking) throws SQLException {
        String sql = "SELECT 1 FROM timetable_slots WHERE room_id = ? AND day_of_week = ? "
                + "AND start_time < ? AND end_time > ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, booking.getRoomId());
            statement.setString(2, booking.getBookingDate().getDayOfWeek().name());
            statement.setObject(3, booking.getEndTime());
            statement.setObject(4, booking.getStartTime());
            return exists(statement);
        }
    }

    private boolean hasBookingConflict(Connection connection, Booking booking) throws SQLException {
        return hasOverlap(connection, "room_id", booking.getRoomId(), booking);
    }

    private boolean hasTeacherConflict(Connection connection, Booking booking) throws SQLException {
        return hasOverlap(connection, "user_id", booking.getUserId(), booking);
    }

    private boolean hasOverlap(Connection connection, String column, int id, Booking booking) throws SQLException {
        String sql = "SELECT 1 FROM bookings WHERE " + column + " = ? AND booking_date = ? "
                + "AND status = 'APPROVED' AND start_time < ? AND end_time > ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setObject(2, booking.getBookingDate());
            statement.setObject(3, booking.getEndTime());
            statement.setObject(4, booking.getStartTime());
            return exists(statement);
        }
    }

    private boolean exists(PreparedStatement statement) throws SQLException {
        try (ResultSet results = statement.executeQuery()) {
            return results.next();
        }
    }

    private void insertApprovedBooking(Connection connection, Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (room_id, user_id, purpose, booking_date, start_time, end_time, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'APPROVED')";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, booking.getRoomId());
            statement.setInt(2, booking.getUserId());
            statement.setString(3, booking.getPurpose());
            statement.setObject(4, booking.getBookingDate());
            statement.setObject(5, booking.getStartTime());
            statement.setObject(6, booking.getEndTime());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                booking.setBookingId(keys.getInt(1));
                booking.setStatus(BookingStatus.APPROVED);
            }
        }
    }
}
