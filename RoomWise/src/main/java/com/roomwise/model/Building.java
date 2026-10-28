package com.roomwise.model;

/**
 * Represents a physical building on campus.
 * Demonstrates: <b>Encapsulation</b> — private fields with validated setters.
 */
public class Building {

    private int    buildingId;
    private String name;
    private String location;
    private int    totalFloors;

    public Building(int buildingId, String name, String location, int totalFloors) {
        this.buildingId = buildingId;
        setName(name);
        this.location   = location;
        setTotalFloors(totalFloors);
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int    getBuildingId()  { return buildingId; }
    public String getName()        { return name; }
    public String getLocation()    { return location; }
    public int    getTotalFloors() { return totalFloors; }

    // ── Validated setters ─────────────────────────────────────────────────────
    public void setBuildingId(int buildingId)  { this.buildingId = buildingId; }
    public void setLocation(String location)   { this.location = location; }

    /** @throws IllegalArgumentException if name is null or blank */
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Building name must not be blank");
        this.name = name.trim();
    }

    /** @throws IllegalArgumentException if totalFloors < 1 */
    public void setTotalFloors(int totalFloors) {
        if (totalFloors < 1)
            throw new IllegalArgumentException("totalFloors must be >= 1, got: " + totalFloors);
        this.totalFloors = totalFloors;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %d floor(s)", name, location, totalFloors);
    }
}
