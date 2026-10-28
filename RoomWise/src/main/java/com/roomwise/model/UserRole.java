package com.roomwise.model;

/**
 * Roles a system user can hold.
 * Maps to the role ENUM column in the users table.
 * Used to select the correct User subclass at runtime.
 * Demonstrates: Abstraction (discriminator for the User hierarchy).
 */
public enum UserRole {
    STUDENT,
    FACULTY,
    ADMIN
}
