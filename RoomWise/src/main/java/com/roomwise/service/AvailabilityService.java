package com.roomwise.service;

import com.roomwise.dao.RoomDao;
import com.roomwise.model.Room;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
 * Business service for room-availability searches.
 * Demonstrates: <b>compile-time polymorphism</b> through overloaded
 * {@code search} methods, and layer separation because it delegates data access
 * to {@link RoomDao} without containing SQL.
 */
public class AvailabilityService {

    private static final ZoneId CAMPUS_ZONE = ZoneId.of("Asia/Kolkata");
    private final RoomDao roomDao;

    /** Creates the service with its room data source. */
    public AvailabilityService(RoomDao roomDao) {
        this.roomDao = Objects.requireNonNull(roomDao, "roomDao must not be null");
    }

    /**
     * Searches whether rooms are free at the current campus time. A one-minute
     * interval is used so a slot currently in progress is excluded.
     */
    public List<Room> search(int minimumCapacity) {
        LocalDate today = LocalDate.now(CAMPUS_ZONE);
        LocalTime now = LocalTime.now(CAMPUS_ZONE).withSecond(0).withNano(0);
        LocalTime end = now.equals(LocalTime.of(23, 59)) ? LocalTime.MAX : now.plusMinutes(1);
        return search(today, now, end, minimumCapacity);
    }

    /** Searches a requested period on a particular calendar date. */
    public List<Room> search(LocalDate date, LocalTime start, LocalTime end, int minimumCapacity) {
        validateSearch(date, start, end, minimumCapacity);
        return roomDao.findAvailable(date.getDayOfWeek(), date, start, end, minimumCapacity);
    }

    /** Searches a requested period today, useful for the console UI. */
    public List<Room> search(LocalTime start, LocalTime end, int minimumCapacity) {
        return search(LocalDate.now(CAMPUS_ZONE), start, end, minimumCapacity);
    }

    private void validateSearch(LocalDate date, LocalTime start, LocalTime end, int minimumCapacity) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(end, "end must not be null");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Search end time must be after start time");
        }
        if (minimumCapacity < 1) {
            throw new IllegalArgumentException("Minimum capacity must be at least 1");
        }
    }
}
