package com.roomwise.model;

/**
 * Represents a faculty member.
 *
 * <p>Demonstrates: <b>Inheritance</b> (extends User),
 * <b>runtime polymorphism</b> (overrides canBook with a permissive policy).</p>
 */
public class Faculty extends User {

    public Faculty(int userId, String name, String email) {
        super(userId, name, email, UserRole.FACULTY);
    }

    /**
     * Faculty can book for any non-blank academic or administrative purpose.
     * Demonstrates: <b>runtime polymorphism</b>.
     */
    @Override
    public boolean canBook(String purpose) {
        return purpose != null && !purpose.isBlank();
    }
}
