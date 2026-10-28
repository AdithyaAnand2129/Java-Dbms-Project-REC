package com.roomwise.dao;

import com.roomwise.model.Room;
import com.roomwise.exception.RoomNotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Data-access contract for rooms.
 * Demonstrates: <b>abstraction</b> through a DAO interface and full room CRUD.
 */
public interface RoomDao {

    /** Creates a room and assigns its generated database ID. */
    Room create(Room room);

    /** Finds one room by primary key. */
    Optional<Room> findById(int roomId);

    /** Returns all rooms. */
    List<Room> findAll();

    /** Persists changes to an existing room. */
    void update(Room room) throws RoomNotFoundException;

    /** Deletes a room by primary key. */
    void delete(int roomId) throws RoomNotFoundException;

    /**
     * Returns rooms that have no overlapping recurring timetable slot or
     * approved booking for the requested period.
     */
    List<Room> findAvailable(DayOfWeek day, LocalDate date, LocalTime start,
                             LocalTime end, int minimumCapacity);
}
