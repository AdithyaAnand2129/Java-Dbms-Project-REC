package com.roomwise.dao;

import com.roomwise.exception.DatabaseException;
import com.roomwise.exception.RoomNotFoundException;
import com.roomwise.model.Classroom;
import com.roomwise.model.Lab;
import com.roomwise.model.Room;
import com.roomwise.model.RoomType;
import com.roomwise.model.SeminarRoom;
import com.roomwise.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link RoomDao}. All SQL is isolated in this DAO and
 * every external value is bound through a {@link PreparedStatement}.
 */
public class JdbcRoomDao implements RoomDao {

    @Override
    public Room create(Room room) {
        String sql = "INSERT INTO rooms (building_id, room_code, floor, capacity, room_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindRoom(statement, room);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DatabaseException("Room was created but no generated ID was returned", null);
                }
                room.setRoomId(keys.getInt(1));
                return room;
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Could not create room", exception);
        }
    }

    @Override
    public Optional<Room> findById(int roomId) {
        String sql = "SELECT room_id, building_id, room_code, floor, capacity, room_type FROM rooms WHERE room_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomId);
            try (ResultSet results = statement.executeQuery()) {
                return results.next() ? Optional.of(mapRoom(results)) : Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Could not read room " + roomId, exception);
        }
    }

    @Override
    public List<Room> findAll() {
        return queryRooms("SELECT room_id, building_id, room_code, floor, capacity, room_type FROM rooms ORDER BY room_code",
                statement -> { });
    }

    @Override
    public void update(Room room) throws RoomNotFoundException {
        String sql = "UPDATE rooms SET building_id = ?, room_code = ?, floor = ?, capacity = ?, room_type = ? WHERE room_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindRoom(statement, room);
            statement.setInt(6, room.getRoomId());
            if (statement.executeUpdate() == 0) {
                throw new RoomNotFoundException("Room does not exist: " + room.getRoomId());
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Could not update room " + room.getRoomId(), exception);
        }
    }

    @Override
    public void delete(int roomId) throws RoomNotFoundException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM rooms WHERE room_id = ?")) {
            statement.setInt(1, roomId);
            if (statement.executeUpdate() == 0) {
                throw new RoomNotFoundException("Room does not exist: " + roomId);
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Could not delete room " + roomId, exception);
        }
    }

    @Override
    public List<Room> findAvailable(DayOfWeek day, LocalDate date, LocalTime start,
                                    LocalTime end, int minimumCapacity) {
        String sql = "SELECT r.room_id, r.building_id, r.room_code, r.floor, r.capacity, r.room_type "
                + "FROM rooms r WHERE r.capacity >= ? "
                + "AND NOT EXISTS (SELECT 1 FROM timetable_slots ts WHERE ts.room_id = r.room_id "
                + "AND ts.day_of_week = ? AND ts.start_time < ? AND ts.end_time > ?) "
                + "AND NOT EXISTS (SELECT 1 FROM bookings b WHERE b.room_id = r.room_id "
                + "AND b.booking_date = ? AND b.status = 'APPROVED' AND b.start_time < ? AND b.end_time > ?) "
                + "ORDER BY r.capacity, r.room_code";
        return queryRooms(sql, statement -> {
            statement.setInt(1, minimumCapacity);
            statement.setString(2, day.name());
            statement.setObject(3, end);
            statement.setObject(4, start);
            statement.setObject(5, date);
            statement.setObject(6, end);
            statement.setObject(7, start);
        });
    }

    private List<Room> queryRooms(String sql, StatementBinder binder) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            binder.bind(statement);
            try (ResultSet results = statement.executeQuery()) {
                List<Room> rooms = new ArrayList<>();
                while (results.next()) {
                    rooms.add(mapRoom(results));
                }
                return rooms;
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Could not query rooms", exception);
        }
    }

    private void bindRoom(PreparedStatement statement, Room room) throws SQLException {
        statement.setInt(1, room.getBuildingId());
        statement.setString(2, room.getRoomCode());
        statement.setInt(3, room.getFloor());
        statement.setInt(4, room.getCapacity());
        statement.setString(5, room.getRoomType().name());
    }

    private Room mapRoom(ResultSet results) throws SQLException {
        int roomId = results.getInt("room_id");
        int buildingId = results.getInt("building_id");
        String code = results.getString("room_code");
        int floor = results.getInt("floor");
        int capacity = results.getInt("capacity");
        RoomType type = RoomType.valueOf(results.getString("room_type"));
        return switch (type) {
            case CLASSROOM -> new Classroom(roomId, buildingId, code, floor, capacity);
            case LAB -> new Lab(roomId, buildingId, code, floor, capacity);
            case SEMINAR_ROOM -> new SeminarRoom(roomId, buildingId, code, floor, capacity);
        };
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
