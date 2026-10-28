package com.roomwise.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * A fixed weekly timetable slot — a course held in a specific room on a given weekday.
 *
 * <p>Uses {@link java.time.DayOfWeek} whose {@code name()} values
 * (MONDAY, TUESDAY…) match the MySQL ENUM column exactly.</p>
 *
 * <p>Demonstrates: <b>Encapsulation</b> — validated time-range setter
 * enforces end > start at the object level.</p>
 */
public class TimetableSlot {

    private int       slotId;
    private int       roomId;
    private int       courseId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    /**
     * @param slotId    DB primary key (0 for not-yet-persisted)
     * @param roomId    FK to rooms
     * @param courseId  FK to courses
     * @param dayOfWeek day of the week this slot repeats on
     * @param startTime slot start (must be before endTime)
     * @param endTime   slot end (must be after startTime)
     * @throws IllegalArgumentException if time range is invalid
     */
    public TimetableSlot(int slotId, int roomId, int courseId,
                          DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.slotId    = slotId;
        this.roomId    = roomId;
        this.courseId  = courseId;
        this.dayOfWeek = dayOfWeek;
        setTimeRange(startTime, endTime);
    }

    // ── Validated setter (Encapsulation) ─────────────────────────────────────

    /**
     * Updates both times atomically, enforcing end > start.
     * Demonstrates: <b>Encapsulation</b> — business rule in setter.
     * @throws IllegalArgumentException if times are null or end is not after start
     */
    public void setTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null)
            throw new IllegalArgumentException("Times must not be null");
        if (!endTime.isAfter(startTime))
            throw new IllegalArgumentException("endTime must be after startTime");
        this.startTime = startTime;
        this.endTime   = endTime;
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public int       getSlotId()    { return slotId; }
    public int       getRoomId()    { return roomId; }
    public int       getCourseId()  { return courseId; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime()   { return endTime; }

    public void setSlotId(int slotId)       { this.slotId = slotId; }
    public void setDayOfWeek(DayOfWeek d)   { this.dayOfWeek = d; }

    @Override
    public String toString() {
        return String.format("%s %s–%s (Room %d, Course %d)",
                dayOfWeek, startTime, endTime, roomId, courseId);
    }
}
