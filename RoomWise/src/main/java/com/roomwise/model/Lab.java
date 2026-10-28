package com.roomwise.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * A computer or science laboratory.
 *
 * <p>Demonstrates: <b>Inheritance</b> (extends Room) and
 * <b>runtime polymorphism</b> (overrides getAllowedPurposes).</p>
 */
public class Lab extends Room {

    public Lab(int roomId, int buildingId, String roomCode, int floor, int capacity) {
        super(roomId, buildingId, roomCode, floor, capacity, RoomType.LAB);
    }

    /**
     * Labs are restricted to hands-on and research activities.
     * Demonstrates: <b>runtime polymorphism</b> — distinct list from Classroom and SeminarRoom.
     */
    @Override
    public List<String> getAllowedPurposes() {
        return List.of("Practical", "Project Work", "Research", "Workshop", "Coding Session");
    }

    @Override
    public boolean isAvailable(LocalDate date, LocalTime start, LocalTime end) {
        return true; // Definitive check in BookingService.
    }
}
