package com.roomwise.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * A small seminar or meeting room.
 *
 * <p>Demonstrates: <b>Inheritance</b> (extends Room) and
 * <b>runtime polymorphism</b> (overrides getAllowedPurposes).</p>
 */
public class SeminarRoom extends Room {

    public SeminarRoom(int roomId, int buildingId, String roomCode, int floor, int capacity) {
        super(roomId, buildingId, roomCode, floor, capacity, RoomType.SEMINAR_ROOM);
    }

    /**
     * Seminar rooms are suited for discussions, presentations, and meetings.
     * Demonstrates: <b>runtime polymorphism</b> — distinct list from Classroom and Lab.
     */
    @Override
    public List<String> getAllowedPurposes() {
        return List.of("Meeting", "Seminar", "Presentation", "Group Discussion",
                       "Viva Voce", "Faculty Discussion");
    }

    @Override
    public boolean isAvailable(LocalDate date, LocalTime start, LocalTime end) {
        return true; // Definitive check in BookingService.
    }
}
