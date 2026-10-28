package com.roomwise.model;

/**
 * Represents a student user.
 *
 * <p>Demonstrates: <b>Inheritance</b> (extends User),
 * <b>runtime polymorphism</b> (overrides canBook).</p>
 */
public class Student extends User {

    /**
     * Constructs a Student.
     * Demonstrates: <b>Inheritance</b> — delegates to User constructor.
     */
    public Student(int userId, String name, String email) {
        super(userId, name, email, UserRole.STUDENT);
    }

    /**
     * Students may book rooms for study-related purposes only.
     * Demonstrates: <b>runtime polymorphism</b> — overridden from User.
     *
     * @param purpose the intended use of the room
     * @return true if the purpose is study-related
     */
    @Override
    public boolean canBook(String purpose) {
        if (purpose == null || purpose.isBlank()) return false;
        String lower = purpose.toLowerCase();
        return lower.contains("study") || lower.contains("group")
                || lower.contains("project") || lower.contains("seminar")
                || lower.contains("meeting") || lower.contains("coding")
                || lower.contains("viva");
    }
}
