package com.roomwise.model;

/**
 * Represents a system administrator.
 *
 * <p>Demonstrates: <b>Inheritance</b> (extends User),
 * <b>runtime polymorphism</b> (overrides canBook — always true).</p>
 */
public class Admin extends User {

    public Admin(int userId, String name, String email) {
        super(userId, name, email, UserRole.ADMIN);
    }

    /**
     * Admins may book for any purpose without restriction.
     * Demonstrates: <b>runtime polymorphism</b>.
     */
    @Override
    public boolean canBook(String purpose) {
        return true;
    }
}
