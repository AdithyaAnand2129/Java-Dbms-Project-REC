package com.roomwise.model;

/**
 * A piece of equipment that can be associated with one or more rooms.
 * The room_equipment join table records these associations.
 * Demonstrates: <b>Encapsulation</b>.
 */
public class Equipment {

    private int    equipmentId;
    private String name;

    public Equipment(int equipmentId, String name) {
        this.equipmentId = equipmentId;
        setName(name);
    }

    public int    getEquipmentId() { return equipmentId; }
    public String getName()        { return name; }

    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }

    /** @throws IllegalArgumentException if name is null or blank */
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Equipment name must not be blank");
        this.name = name.trim();
    }

    @Override
    public String toString() { return name; }
}
