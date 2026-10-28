-- =============================================================================
-- RoomWise: Sample Data
-- Run AFTER schema.sql.
-- Docker: auto-executed by the MySQL container on first start (file 02).
-- Manual: mysql -u root -p roomwise_db < sample_data.sql
-- =============================================================================

USE roomwise_db;

-- ── Buildings ─────────────────────────────────────────────────────────────────
INSERT INTO buildings (name, location, total_floors) VALUES
  ('Block A',                'North Campus', 4),
  ('Block B',                'South Campus', 3),
  ('Computer Science Block', 'East Campus',  5);

-- ── Rooms ─────────────────────────────────────────────────────────────────────
INSERT INTO rooms (building_id, room_code, floor, capacity, room_type) VALUES
  (1, 'A-101', 1, 60, 'CLASSROOM'),
  (1, 'A-204', 2, 60, 'CLASSROOM'),
  (1, 'A-301', 3, 30, 'SEMINAR_ROOM'),
  (2, 'B-102', 1, 40, 'CLASSROOM'),
  (3, 'CS-101', 1, 30, 'LAB'),
  (3, 'CS-201', 2, 30, 'LAB'),
  (3, 'CS-301', 3, 20, 'LAB');

-- ── Equipment ─────────────────────────────────────────────────────────────────
INSERT INTO equipment (name) VALUES
  ('Projector'),
  ('Whiteboard'),
  ('Computers'),
  ('AC'),
  ('Smart Board');

-- ── Room ↔ Equipment (many-to-many) ──────────────────────────────────────────
-- A-101 (room 1): Projector, Whiteboard, AC
INSERT INTO room_equipment VALUES (1,1),(1,2),(1,4);
-- A-204 (room 2): Projector, Whiteboard, AC, Smart Board
INSERT INTO room_equipment VALUES (2,1),(2,2),(2,4),(2,5);
-- A-301 seminar (room 3): Projector, AC, Smart Board
INSERT INTO room_equipment VALUES (3,1),(3,4),(3,5);
-- B-102 (room 4): Projector, Whiteboard
INSERT INTO room_equipment VALUES (4,1),(4,2);
-- CS-101 lab (room 5): Whiteboard, Computers, AC
INSERT INTO room_equipment VALUES (5,2),(5,3),(5,4);
-- CS-201 lab (room 6): Whiteboard, Computers, AC
INSERT INTO room_equipment VALUES (6,2),(6,3),(6,4);
-- CS-301 lab (room 7): Whiteboard, Computers
INSERT INTO room_equipment VALUES (7,2),(7,3);

-- ── Users ─────────────────────────────────────────────────────────────────────
INSERT INTO users (name, email, role) VALUES
  ('Admin User',    'admin@college.edu',   'ADMIN'),
  ('Dr. Sharma',    'sharma@college.edu',  'FACULTY'),
  ('Dr. Mehta',     'mehta@college.edu',   'FACULTY'),
  ('Alice Student', 'alice@student.edu',   'STUDENT'),
  ('Bob Student',   'bob@student.edu',     'STUDENT');

-- ── Courses ───────────────────────────────────────────────────────────────────
INSERT INTO courses (course_code, name) VALUES
  ('CS20001', 'Data Structures'),
  ('CS20002', 'Database Systems'),
  ('CS20003', 'Java Programming'),
  ('CS20004', 'Computer Networks');

-- A course offering identifies its teacher and student group.
INSERT INTO course_offerings
  (source_instance_id, course_id, faculty_id, group_name, department, semester, schedule_type) VALUES
  ('demo-1', 1, 2, 'CSE_S3_G1', 'Computer Science', 3, 'THEORY'),
  ('demo-2', 2, 3, 'CSE_S3_G1', 'Computer Science', 3, 'THEORY'),
  ('demo-3', 3, 2, 'CSE_S3_G2', 'Computer Science', 3, 'THEORY'),
  ('demo-4', 4, 3, 'CSE_S3_G1', 'Computer Science', 3, 'LAB');

-- ── Timetable Slots ───────────────────────────────────────────────────────────
-- Room A-101 on Monday  09:00-10:00  (Data Structures)
-- Room A-101 on Monday  10:00-11:00  (Database Systems)
-- Room A-204 on Tuesday 11:00-12:00  (Java Programming)
-- CS-101 lab on Wed     14:00-16:00  (Computer Networks)
INSERT INTO timetable_slots
  (room_id, offering_id, day_of_week, start_time, end_time, source_kind, source_slot_label) VALUES
  (1, 1, 'MONDAY',    '09:00:00', '10:00:00', 'THEORY', '1'),
  (1, 2, 'MONDAY',    '10:00:00', '11:00:00', 'THEORY', '2'),
  (2, 3, 'TUESDAY',   '11:00:00', '12:00:00', 'THEORY', '3'),
  (5, 4, 'WEDNESDAY', '14:00:00', '16:00:00', 'LAB',    'L3');

-- ── Sample Bookings ───────────────────────────────────────────────────────────
INSERT INTO bookings (room_id, user_id, purpose, booking_date, start_time, end_time, status) VALUES
  (3, 4, 'Java project group meeting', '2026-10-29', '13:00:00', '14:00:00', 'APPROVED'),
  (2, 5, 'Study session',              '2026-10-29', '15:00:00', '16:00:00', 'PENDING');
