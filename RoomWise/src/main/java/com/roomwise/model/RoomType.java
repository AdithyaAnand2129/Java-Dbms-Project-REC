package com.roomwise.model;

/**
 * Distinguishes the physical type of a room.
 * Used as a discriminator column in the rooms table.
 * Demonstrates: Encapsulation of room categorisation as a type-safe enum.
 */
public enum RoomType {
    CLASSROOM,
    LAB,
    SEMINAR_ROOM
}
