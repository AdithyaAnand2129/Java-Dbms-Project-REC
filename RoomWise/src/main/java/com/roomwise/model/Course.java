package com.roomwise.model;

/**
 * An academic course, taught by a faculty member (FK to users).
 * Demonstrates: <b>Encapsulation</b>, FK relationship to the users table.
 */
public class Course {

    private int    courseId;
    private String name;
    private int    facultyId;   // FK -> users.user_id

    public Course(int courseId, String name, int facultyId) {
        this.courseId  = courseId;
        setName(name);
        this.facultyId = facultyId;
    }

    public int    getCourseId()  { return courseId; }
    public String getName()      { return name; }
    public int    getFacultyId() { return facultyId; }

    public void setCourseId(int courseId)   { this.courseId = courseId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }

    /** @throws IllegalArgumentException if name is null or blank */
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Course name must not be blank");
        this.name = name.trim();
    }

    @Override
    public String toString() {
        return String.format("Course[%d]: %s (Faculty ID: %d)", courseId, name, facultyId);
    }
}
