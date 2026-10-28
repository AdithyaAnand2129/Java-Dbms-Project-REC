package com.roomwise.service;

import com.roomwise.dao.RoomDao;
import com.roomwise.exception.RoomNotFoundException;
import com.roomwise.model.Room;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests the availability-service inputs and DAO delegation. */
class AvailabilityServiceTest {

    @Test
    void manualSearchUsesTheRequestedDateAndTimeRange() {
        CapturingRoomDao dao = new CapturingRoomDao();
        AvailabilityService service = new AvailabilityService(dao);
        LocalDate date = LocalDate.of(2026, 11, 2);

        service.search(date, LocalTime.of(10, 0), LocalTime.of(11, 0), 40);

        assertEquals(DayOfWeek.MONDAY, dao.day);
        assertEquals(date, dao.date);
        assertEquals(LocalTime.of(10, 0), dao.start);
        assertEquals(LocalTime.of(11, 0), dao.end);
        assertEquals(40, dao.minimumCapacity);
    }

    @Test
    void searchRejectsAnInvalidTimeRange() {
        AvailabilityService service = new AvailabilityService(new CapturingRoomDao());

        assertThrows(IllegalArgumentException.class,
                () -> service.search(LocalDate.of(2026, 11, 2), LocalTime.NOON, LocalTime.NOON, 1));
    }

    private static final class CapturingRoomDao implements RoomDao {
        private DayOfWeek day;
        private LocalDate date;
        private LocalTime start;
        private LocalTime end;
        private int minimumCapacity;

        @Override public Room create(Room room) { return room; }
        @Override public Optional<Room> findById(int roomId) { return Optional.empty(); }
        @Override public List<Room> findAll() { return List.of(); }
        @Override public void update(Room room) throws RoomNotFoundException { }
        @Override public void delete(int roomId) throws RoomNotFoundException { }

        @Override
        public List<Room> findAvailable(DayOfWeek day, LocalDate date, LocalTime start,
                                        LocalTime end, int minimumCapacity) {
            this.day = day;
            this.date = date;
            this.start = start;
            this.end = end;
            this.minimumCapacity = minimumCapacity;
            return List.of();
        }
    }
}
