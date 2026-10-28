package com.roomwise.model;

/**
 * Abstract base class for all user types.
 *
 * <p><b>OOP concepts demonstrated here:</b>
 * <ul>
 *   <li><b>Abstraction</b> — abstract class with abstract {@code canBook()} method.</li>
 *   <li><b>Encapsulation</b> — private fields; email setter validates format.</li>
 *   <li><b>Inheritance</b> — Student, Faculty, Admin extend this class.</li>
 *   <li><b>Polymorphism (runtime)</b> — {@code canBook()} is overridden per role
 *       and called through the User reference type.</li>
 * </ul>
 * </p>
 */
public abstract class User {

    // ── Private fields (Encapsulation) ────────────────────────────────────────
    private int      userId;
    private String   name;
    private String   email;
    private final UserRole role;   // immutable — a user's role never changes

    /**
     * Protected constructor for subclasses only.
     *
     * @param userId DB primary key (0 for not-yet-persisted)
     * @param name   full display name
     * @param email  valid email address
     * @param role   role discriminator
     */
    protected User(int userId, String name, String email, UserRole role) {
        this.userId = userId;
        setName(name);
        setEmail(email);
        this.role = role;
    }

    // ── Abstract method (Abstraction + Polymorphism) ──────────────────────────

    /**
     * Returns true if this user is permitted to book a room for the given purpose.
     * Each subclass defines its own booking policy.
     * Demonstrates: <b>runtime polymorphism</b>.
     *
     * @param purpose the intended use of the room
     * @return true if this user's role allows the booking
     */
    public abstract boolean canBook(String purpose);

    // ── Getters (Encapsulation) ───────────────────────────────────────────────

    /** @return DB primary key */
    public int      getUserId() { return userId; }

    /** @return full display name */
    public String   getName()   { return name; }

    /** @return email address (lower-cased, trimmed) */
    public String   getEmail()  { return email; }

    /** @return this user's role */
    public UserRole getRole()   { return role; }

    // ── Validated setters (Encapsulation) ────────────────────────────────────

    /**
     * Sets the user's DB primary key.
     * @throws IllegalArgumentException if userId is not positive
     */
    public void setUserId(int userId) {
        if (userId <= 0) throw new IllegalArgumentException("userId must be positive, got: " + userId);
        this.userId = userId;
    }

    /**
     * Sets the user's display name.
     * @throws IllegalArgumentException if name is null or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("name must not be blank");
        this.name = name.trim();
    }

    /**
     * Sets and validates the email address.
     * Demonstrates: <b>Encapsulation</b> — business rule (email format) enforced in setter.
     * @throws IllegalArgumentException if email format is invalid
     */
    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)*\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Invalid email address: " + email);
        this.email = email.toLowerCase().trim();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s <%s>", role, name, email);
    }
}
