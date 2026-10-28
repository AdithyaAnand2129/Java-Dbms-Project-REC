RoomWise: Agent Instructions

Read this file completely before doing anything. Then read AGENT_MEMORY.md to see what has already been done. When you finish any task, update AGENT_MEMORY.md before you stop.

1. What this project is

RoomWise is a Smart Campus Room Finder and Scheduling System. It is a college project for a second-year CSE course, built in Java with a relational database.

It helps people with three jobs:

Students with a free period find an available room (filter by type, capacity, purpose, equipment).
Faculty view room timetables and book rooms for classes or meetings.
The system rejects conflicting bookings and suggests other available rooms.

Optional last feature: simple room-utilization reports (most used rooms, idle time slots, number of rejected conflict attempts).

Example: a student has a free period at 11 AM and wants a room for a Java project. RoomWise shows rooms with no class or approved booking for the whole requested period, such as "Classroom A-204, Block A, 2nd floor, capacity 60, available until 12:00 PM".

2. Why the rules below are strict (faculty requirements)

The faculty have said, in writing:

A web application alone is NOT enough. Projects that focus only on web development will not meet requirements.
The project must be implemented in Java.
It must clearly demonstrate ALL core OOP concepts: Encapsulation, Inheritance, Polymorphism, Packages, Exception Handling, Abstraction.
It must have proper database design: appropriate tables, primary keys, foreign keys and relationships.
It must have database connectivity and full CRUD (Create, Read, Update, Delete).
OOP and DBMS must be clearly integrated, and both carry equal weight.
There must be documentation and a demo explaining how each concept is implemented. In evaluation the team has to point to each concept in the code.

Consequence for you: every decision should make OOP and DBMS more visible, not less. Do not spend effort on UI polish at the expense of these.

3. Tech stack (defaults, unless AGENT_MEMORY.md records a different decision)
Language: Java 17 or newer
UI: Swing (javax.swing), kept thin. A console menu is an acceptable fallback.
Database: MySQL, accessed with plain JDBC (PreparedStatement only)
Testing: JUnit 5
Build: either plain IDE project or Maven. Follow whatever is already in the repo.

Do not introduce Spring, Hibernate/JPA, web frameworks, React, or any ORM. Writing the JDBC and DAO layer by hand is part of what is being graded.

4. Package structure
com.roomwise
 ├── model       Entities and the class hierarchies (Room, User, Booking...)
 ├── dao         Interfaces plus JDBC implementations (all SQL lives here)
 ├── service     Business rules (availability, conflict detection, booking)
 ├── exception   Custom exception classes
 ├── util        DB connection helper, time/date helpers, validators
 ├── ui          Swing screens (talk ONLY to the service layer)
 └── Main.java   Entry point

Rules:

ui never imports from dao. It calls service only.
service never contains raw SQL. It calls dao only.
All SQL stays in dao and uses PreparedStatement, never string concatenation.
5. Database design

Core tables (adjust only through the process in section 8):

buildings (building_id PK, name, ...)
rooms (room_id PK, building_id FK, room_code, floor, capacity, room_type)
equipment (equipment_id PK, name)
room_equipment (room_id FK, equipment_id FK, composite PK): many-to-many
users (user_id PK, name, email, role: STUDENT / FACULTY / ADMIN)
courses (course_id PK, name, faculty_id FK -> users)
timetable_slots (slot_id PK, room_id FK, course_id FK, day_of_week, start_time, end_time)
bookings (booking_id PK, room_id FK, user_id FK, purpose, booking_date, start_time, end_time, status)
optional conflict_log (log_id PK, room_id FK, user_id FK, attempted_at, reason) for analytics

Conflict rule: a room is unavailable if an existing timetable slot or approved booking overlaps the requested period:

existingStart < requestedEnd  AND  existingEnd > requestedStart

Booking creation must run inside a transaction (for example SELECT ... FOR UPDATE on the room's rows, or a unique constraint on the slot) so two simultaneous bookings cannot both succeed. The database must be the final judge, not just Java code. Also check that the same teacher is not double-booked at the same time.

6. Where each OOP concept must live

Every concept needs a clear, easy-to-find example. Keep this mapping accurate and record the real class names in AGENT_MEMORY.md.

Abstraction: abstract class Room and abstract class User; interface Bookable; DAO interfaces.
Inheritance: Classroom, SeminarRoom, Lab extend Room. Student, Faculty, Admin extend User.
Polymorphism: overridden methods such as getAllowedPurposes() on rooms and canBook(...) on users, called through the parent type (runtime). Overloaded search(...) methods (compile time).
Encapsulation: private fields, public getters and validated setters (capacity > 0, end time after start time, valid email).
Exception handling: custom checked exceptions such as BookingConflictException, InvalidTimeRangeException, RoomNotFoundException, DatabaseException. Wrap SQLException at the DAO layer. Use try-with-resources for connections.
Packages: the structure in section 4.
CRUD integration: each DAO maps result rows into model objects and back. Every main entity has create, read, update and delete working end to end.
7. Coding standards
Meaningful names, small methods, one responsibility per class.
Javadoc comments on every public class and method, mentioning which OOP or DBMS concept it demonstrates where relevant (for example // Demonstrates runtime polymorphism). This helps the evaluation.
No hard-coded database passwords in committed code. Read them from a config file that is not committed, and provide an example config.
No catch (Exception e) {} that swallows errors.
Add a JUnit test for any non-trivial logic, especially the overlap check and booking service.
Provide a schema.sql and a sample_data.sql so the database can be rebuilt from scratch.
8. How to work (the protocol)

At the start of every session:

Read this file.
Read AGENT_MEMORY.md completely.
State in one or two lines what you understand the current status to be, then continue from "Next steps".

While working:

Do one clearly scoped task at a time, and prefer small, working increments over big rewrites.
Do not delete or rename existing classes, tables or packages without recording the reason in AGENT_MEMORY.md.
Schema changes: update schema.sql, update the schema section of AGENT_MEMORY.md, and check that DAOs and models still match.
If a request conflicts with the faculty requirements in section 2, say so and propose a compliant alternative instead of silently complying.
If something is ambiguous, write down your assumption in AGENT_MEMORY.md under "Assumptions" and continue, rather than guessing silently.

At the end of every session:

Make sure the project compiles and the tests you touched pass. If not, say so clearly.
Update AGENT_MEMORY.md: add a session log entry, update status, concept-to-code table, decisions, open issues and next steps.
Be honest. Record what is unfinished or broken, not just what worked.
9. Build order (for reference)
Schema, sample data, and the Room and User hierarchies.
Room, user and booking DAOs with full CRUD.
Availability query, then conflict detection inside a transaction.
Faculty booking flow, then the student room finder as filters over the same query.
Swing UI (thin).
Utilization reports (SQL queries) as an optional last step.
JUnit tests and the concept-to-code documentation.
10. Definition of done

A task is done only when:

the code compiles and runs,
it respects the package rules in section 4,
any new logic has a test or a clear manual test described in memory,
the relevant OOP concept is identifiable in the code and listed in the concept-to-code table,
AGENT_MEMORY.md is updated.