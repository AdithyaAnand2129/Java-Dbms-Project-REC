-- =============================================================================
-- RoomWise: Database Schema
-- MySQL 8.0+
-- Run this file once to create all tables.
-- Docker: this file is auto-executed by the MySQL container on first start.
-- Manual: open in MySQL Workbench and run, or:  mysql -u root -p < schema.sql
-- =============================================================================

CREATE DATABASE IF NOT EXISTS roomwise_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE roomwise_db;

-- ─────────────────────────────────────────────────────────────────────────────
-- buildings
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS buildings (
    building_id  INT          AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    location     VARCHAR(255),
    total_floors INT          NOT NULL DEFAULT 1,
    CONSTRAINT chk_floors CHECK (total_floors >= 1)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- rooms
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    room_id     INT         AUTO_INCREMENT PRIMARY KEY,
    building_id INT         NOT NULL,
    college_room_id INT     NULL,
    room_code   VARCHAR(40) NOT NULL,
    floor       INT         NOT NULL DEFAULT 0,
    capacity    INT         NOT NULL,
    room_type   ENUM('CLASSROOM','LAB','SEMINAR_ROOM') NOT NULL,
    CONSTRAINT chk_capacity CHECK (capacity > 0),
    CONSTRAINT chk_floor    CHECK (floor >= 0),
    CONSTRAINT fk_room_building
        FOREIGN KEY (building_id) REFERENCES buildings(building_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_room_building_code UNIQUE (building_id, room_code),
    CONSTRAINT uq_room_college_id UNIQUE (college_room_id)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- equipment
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS equipment (
    equipment_id INT         AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- room_equipment  (many-to-many: rooms <-> equipment)
-- Demonstrates: many-to-many relationship with composite PK
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS room_equipment (
    room_id      INT NOT NULL,
    equipment_id INT NOT NULL,
    PRIMARY KEY (room_id, equipment_id),
    CONSTRAINT fk_re_room      FOREIGN KEY (room_id)      REFERENCES rooms(room_id)          ON DELETE CASCADE,
    CONSTRAINT fk_re_equipment FOREIGN KEY (equipment_id) REFERENCES equipment(equipment_id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- users
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id INT          AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(150) NULL UNIQUE,
    role    ENUM('STUDENT','FACULTY','ADMIN') NOT NULL,
    college_teacher_id INT NULL UNIQUE,
    staff_code VARCHAR(30) NULL UNIQUE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- courses
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS courses (
    course_id  INT          AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(30) NOT NULL UNIQUE,
    name       VARCHAR(150) NOT NULL
);

-- A course can have multiple teachers and groups in the supplied college data.
CREATE TABLE IF NOT EXISTS course_offerings (
    offering_id INT AUTO_INCREMENT PRIMARY KEY,
    source_instance_id VARCHAR(80) NULL,
    course_id INT NOT NULL,
    faculty_id INT NOT NULL,
    group_name VARCHAR(180) NOT NULL,
    department VARCHAR(120) NOT NULL,
    semester INT NOT NULL,
    schedule_type ENUM('THEORY','LAB') NOT NULL,
    CONSTRAINT uq_offering_source UNIQUE (source_instance_id, faculty_id, group_name, schedule_type),
    CONSTRAINT fk_offering_course FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT,
    CONSTRAINT fk_offering_faculty FOREIGN KEY (faculty_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

-- ─────────────────────────────────────────────────────────────────────────────
-- timetable_slots
-- Fixed weekly schedule (repeats every week).
-- day_of_week matches java.time.DayOfWeek.name() values.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS timetable_slots (
    slot_id     INT  AUTO_INCREMENT PRIMARY KEY,
    room_id     INT  NOT NULL,
    offering_id INT  NOT NULL,
    day_of_week ENUM('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY') NOT NULL,
    start_time  TIME NOT NULL,
    end_time    TIME NOT NULL,
    source_kind ENUM('THEORY','LAB') NOT NULL,
    source_slot_label VARCHAR(30) NULL,
    CONSTRAINT chk_slot_times CHECK (end_time > start_time),
    CONSTRAINT fk_slot_room
        FOREIGN KEY (room_id)   REFERENCES rooms(room_id)       ON DELETE CASCADE,
    CONSTRAINT fk_slot_offering
        FOREIGN KEY (offering_id) REFERENCES course_offerings(offering_id) ON DELETE CASCADE
);

-- ─────────────────────────────────────────────────────────────────────────────
-- bookings
-- status: PENDING -> APPROVED or REJECTED; user can set CANCELLED.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS bookings (
    booking_id   INT          AUTO_INCREMENT PRIMARY KEY,
    room_id      INT          NOT NULL,
    user_id      INT          NOT NULL,
    purpose      VARCHAR(255) NOT NULL,
    booking_date DATE         NOT NULL,
    start_time   TIME         NOT NULL,
    end_time     TIME         NOT NULL,
    status       ENUM('PENDING','APPROVED','REJECTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_booking_times CHECK (end_time > start_time),
    CONSTRAINT fk_booking_room
        FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

-- Index: speeds up the availability overlap query (used in every room-search)
CREATE INDEX idx_bookings_room_date
    ON bookings(room_id, booking_date, status);

-- Index: speeds up the timetable conflict check
CREATE INDEX idx_slots_room_day
    ON timetable_slots(room_id, day_of_week);

-- ─────────────────────────────────────────────────────────────────────────────
-- conflict_log  (optional — for analytics/utilization reports)
-- Records every rejected booking attempt.
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS conflict_log (
    log_id       INT          AUTO_INCREMENT PRIMARY KEY,
    room_id      INT          NOT NULL,
    user_id      INT          NOT NULL,
    attempted_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason       VARCHAR(255),
    CONSTRAINT fk_log_room FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
