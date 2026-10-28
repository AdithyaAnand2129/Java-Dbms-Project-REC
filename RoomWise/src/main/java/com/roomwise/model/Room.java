package com.roomwise.model;

import java.util.List;

/**
 * Abstract base class for all room types in the system.
 *
 * <p><b>OOP concepts demonstrated here:</b>
 * <ul>
 *   <li><b>Abstraction</b> — abstract class; concrete type depends on room_type column.</li>
 *   <li><b>Encapsulation</b> — all fields are private; setters validate before assigning.</li>
 *   <li><b>Inheritance</b> — Classroom, Lab, SeminarRoom extend this class.</li>
 *   <li><b>Polymorphism (runtime)</b> — {@code getAllowedPurposes()} is overridden
 *       by each subclass and called through the Room reference type.</li>
 * </ul>
 * </p>
 */
public abstract class Room implements Bookable {

    // ── Private fields (Encapsulation) ────────────────────────────────────────
    private int      roomId;
    private int      buildingId;
    private String   roomCode;
    private int      floor;
    private int      capacity;
    private final RoomType roomType;   // immutable after construction

    /**
     * Protected constructor — only subclasses may call it.
     *
     * @param roomId     DB primary key (0 for a not-yet-persisted object)
     * @param buildingId FK to buildings table
     * @param roomCode   unique human-readable code, e.g. "A-204"
     * @param floor      floor number (>= 0)
     * @param capacity   maximum occupancy (must be > 0)
     * @param roomType   type discriminator — set once at construction
     */
    protected Room(int roomId, int buildingId, String roomCode,
                   int floor, int capacity, RoomType roomType) {
        this.roomId     = roomId;
        this.buildingId = buildingId;
        setRoomCode(roomCode);   // validated
        setFloor(floor);         // validated
        setCapacity(capacity);   // validated
        this.roomType = roomType;
    }

    // ── Abstract method (Abstraction + Polymorphism) ──────────────────────────

    /**
     * Returns the list of booking purposes allowed for this room type.
     * Each subclass provides its own implementation.
     * Demonstrates: <b>runtime polymorphism</b> — called through the Room type.
     *
     * @return immutable list of purpose strings
     */
    @Override
    public abstract List<String> getAllowedPurposes();

    // ── Getters (Encapsulation) ───────────────────────────────────────────────

    /** @return DB primary key of this room */
    public int      getRoomId()     { return roomId; }

    /** @return FK to the buildings table */
    public int      getBuildingId() { return buildingId; }

    /** @return unique room code such as "CS-201" */
    public String   getRoomCode()   { return roomCode; }

    /** @return floor number (ground = 0) */
    public int      getFloor()      { return floor; }

    /** @return maximum number of occupants */
    public int      getCapacity()   { return capacity; }

    /** @return room type enum value */
    public RoomType getRoomType()   { return roomType; }

    // ── Validated setters (Encapsulation) ────────────────────────────────────

    /**
     * Sets the room's database ID.
     * @throws IllegalArgumentException if roomId is not positive
     */
    public void setRoomId(int roomId) {
        if (roomId <= 0) throw new IllegalArgumentException("roomId must be positive, got: " + roomId);
        this.roomId = roomId;
    }

    /**
     * Sets the building foreign key.
     * @throws IllegalArgumentException if buildingId is not positive
     */
    public void setBuildingId(int buildingId) {
        if (buildingId <= 0)
            throw new IllegalArgumentException("buildingId must be positive, got: " + buildingId);
        this.buildingId = buildingId;
    }

    /**
     * Sets the room code after trimming whitespace.
     * @throws IllegalArgumentException if roomCode is null or blank
     */
    public void setRoomCode(String roomCode) {
        if (roomCode == null || roomCode.isBlank())
            throw new IllegalArgumentException("roomCode must not be blank");
        this.roomCode = roomCode.trim();
    }

    /**
     * Sets the floor number.
     * @throws IllegalArgumentException if floor is negative
     */
    public void setFloor(int floor) {
        if (floor < 0) throw new IllegalArgumentException("floor must be >= 0, got: " + floor);
        this.floor = floor;
    }

    /**
     * Sets the room capacity.
     * Demonstrates: <b>Encapsulation</b> — business rule enforced in the setter.
     * @throws IllegalArgumentException if capacity is not positive
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("capacity must be > 0, got: " + capacity);
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-12s | Floor %-2d | Capacity: %3d | Type: %s",
                roomCode, "(bldg " + buildingId + ")", floor, capacity, roomType);
    }
}
