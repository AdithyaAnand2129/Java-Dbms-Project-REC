package com.roomwise.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * A standard lecture classroom.
 *
 * <p><b>OOP concepts demonstrated here:</b>
 * <ul>
 *   <li><b>Inheritance</b> — extends Room.</li>
 *   <li><b>Polymorphism (runtime)</b> — overrides {@code getAllowedPurposes()}
 *       and {@code isAvailable()}; called through the Room or Bookable type.</li>
 * </ul>
 * </p>
 */
public class Classroom extends Room {

    /**
     * Constructs a Classroom.
     * Demonstrates: <b>Inheritance</b> — delegates to the parent constructor.
     */
    public Classroom(int roomId, int buildingId, String roomCode, int floor, int capacity) {
        super(roomId, buildingId, roomCode, floor, capacity, RoomType.CLASSROOM);
    }

    /**
     * Returns purposes allowed in a standard classroom.
     * Demonstrates: <b>runtime polymorphism</b> — overridden from Room.
     */
    @Override
    public List<String> getAllowedPurposes() {
        return List.of("Lecture", "Tutorial", "Exam", "Seminar", "Group Study");
    }

    /**
     * Local availability guard (object level).
     * The definitive check is performed by the service layer against the DB.
     * Demonstrates: <b>Polymorphism (runtime)</b> — overrides Bookable.isAvailable().
     */
    @Override
    public boolean isAvailable(LocalDate date, LocalTime start, LocalTime end) {
        // Actual DB check delegated to BookingService.
        return true;
    }
}
