RoomWise: Agent Memory

This is the shared memory for every agent working on RoomWise. Read it at the start of every session. Update it at the end of every session. Keep entries short, factual and honest.

Read AGENT_INSTRUCTIONS.md first for the project rules.

1. Current status
Last updated: 2026-10-28 by Codex
Overall stage: Foundation in progress — schema, models and timetable importer created
Project compiles: Yes (manual javac compilation with JDK 21)
Database created from schema.sql: Yes (local MySQL80)
Tests passing: unknown (Maven unavailable)

One-line summary of where things stand: The local MySQL database contains all 4,167 real timetable records and the live console availability search is working.

2. Decisions made

Record every decision that affects the project, with the reason, so nobody reverses it by accident.

Language is Java, with JDBC and a relational database (faculty requirement).
Swing is the planned UI, kept thin. Console menu is the fallback. (Proposed, confirm.)
Database: MySQL Server (confirmed by the user). Accessed from Java with plain JDBC through MySQL Connector/J. No ORM.
Conflict detection must be enforced at the database level using a transaction, not only in Java.
(2026-10-28) Decision: Use the complete supplied theory and lab timetable datasets as the source of truth. Made by: user.
(2026-10-28) Decision: Live availability uses the current date, weekday and time. Made by: user.
(2026-10-28) Decision: The timetable is treated as a recurring weekly schedule; a room with no entry on a given weekday is free throughout that day, while a room with entries is unavailable only during its listed intervals. Made by: user.
(2026-10-28) Decision: Normalize theory and lab records into one timetable representation, preserving their source-specific fields where needed. Made by: user.
(2026-10-28) Decision: Represent a scheduled class as a course offering so one course can have multiple teachers/groups in the supplied data. Reason: the original course-to-one-faculty relationship cannot represent the real timetable. Made by: Codex.

Add new decisions below in this format:

(date) Decision: ... Reason: ... Made by: ...
2a. Tools and environment

The user will use MySQL Server as the database. Agents should write SQL and JDBC code for MySQL (not SQLite, PostgreSQL or others).

Database

MySQL Server (Community Edition): the actual database. Confirmed.
MySQL Workbench: GUI used to write and run schema.sql, test queries, view data and draw the EER diagram for the documentation.
Database name, user and password: not recorded here. Keep credentials in a local config file that is not committed. Add the config file name below once it exists:
Config file: (not created yet)

Java side

JDK 21.0.9 LTS (verified; javac 21.0.9).
MySQL Connector/J: the JDBC driver. Added as a library jar or as the Maven dependency mysql-connector-j.
IDE: IntelliJ IDEA Community, Eclipse, NetBeans or VS Code with the Java extension (the user has not said which one yet). Record the choice here: (not decided)
Swing for the UI (part of the JDK, nothing to download).
JUnit 5 for tests.

Project management

Git and GitHub for version control. Never commit the database password.

Optional

draw.io (diagrams.net) for class diagrams and flow charts in the documentation.

Setup check (do this first, and log the result in the session log):

java -version and javac -version both work.
MySQL Server is running and Workbench can connect to it.
A tiny Java class can connect through JDBC and print one row.

Setup status: JDK 21.0.9 verified. Local MySQL80 and JDBC connectivity verified. Maven and the MySQL command-line client are not on PATH.

3. Assumptions

Things an agent assumed because the answer was not available. A human should confirm or correct these.

2026-10-28: Both pasted files are the full dataset and live queries use the current date/time against the recurring weekly timetable.
2026-10-28: Timetable input remains outside Git under `RoomWise/data/*.json`; the importer receives paths to the files. This keeps the multi-megabyte college data out of source control.
4. Completed work log

Newest entry first. One entry per session.

Session template (copy for each session)
Date:
Agent or person:
Goal of this session:
What was done:
...
Files created or changed:
...
Tests run and result:
What is unfinished or broken:
Next steps recommended:
Log entries

### Session 3 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Create the requested Phase 1 Git checkpoint.
- **What was done:** Prepared the current RoomWise foundation and project instructions for version control, excluding generated output, IDE metadata, local timetable exports, and database credentials.
- **Files created or changed:** None beyond the existing working tree; this entry records the checkpoint operation.
- **Tests run and result:** Not run; no source changes were made.
- **What is unfinished or broken:** Existing unfinished items remain as documented below.
- **Next steps recommended:** Continue with the booking verification and remaining DAO work.

### Session 2 — 2026-10-28
- **Agent/Person:** Codex
- **Goal:** Begin the real-timetable implementation foundation.
- **What was done:** Audited the existing unrecorded Maven scaffold and model hierarchy. Updated the schema to support imported college room IDs and normalized course offerings. Added `TimetableDataParser` and `CollegeTimetableImporter`, which parse theory/lab JSON and insert buildings, rooms, faculty, courses, offerings and weekly slots using prepared statements. Added `RoomDao`/`JdbcRoomDao` with full room CRUD and overlap-based availability SQL, plus `AvailabilityService` with live campus-time and manual search overloads. Added `BookingDao`/`JdbcBookingDao` and `BookingService`; booking creation locks the room inside a transaction and rejects timetable, room, and requester overlap. Added a thin interactive console UI for live/custom availability searches and bookings. Updated sample SQL to match the schema.
- **Files created or changed:** `RoomWise/src/main/resources/schema.sql`, `sample_data.sql`, `.gitignore`; added importer, parser, room/booking DAOs, availability/booking services, console UI, entry point, test, and `RoomWise/data/README.md`.
- **Tests run and result:** `javac` compilation of all main source: passed. Parser validation against full supplied files: passed (3,030 theory and 1,137 lab records parsed). Local MySQL schema creation and import: passed (4,167 records). Live console availability search: passed. JUnit test was added but cannot be run until Maven is available.
- **What is unfinished or broken:** Maven and MySQL command-line client are unavailable on PATH. End-to-end JUnit run and remaining entity DAOs remain.
- **Next steps recommended:** Verify a live booking attempt, add remaining entity DAOs and booking CRUD/status operations, then install/configure Maven to execute JUnit tests.

5. Database schema (current)

Keep this in sync with schema.sql. List tables, keys and relationships as they actually exist.

Tables defined in `schema.sql`: buildings, rooms, equipment, room_equipment, users, courses, course_offerings, timetable_slots, bookings, conflict_log. Database created locally and populated with the supplied timetable exports.
Schema changes history:
2026-10-28: Added `college_room_id` and normalized `course_offerings`; timetable slots now point to offerings rather than a single-faculty course, matching the real college data.
6. Concept-to-code table (for the evaluation)

Faculty will ask the team to show every concept in the code. Fill in the real class and file names as they are written. Leave "not yet" until it exists.

Encapsulation: done (`model/Room.java`, `model/User.java`, `model/TimetableSlot.java`)
Inheritance: done (`Classroom`, `Lab`, `SeminarRoom` extend `Room`; user subclasses extend `User`)
Polymorphism, runtime: done (room subclasses override `getAllowedPurposes()`)
Polymorphism, compile time: done (`service/AvailabilityService.java`: overloaded `search(...)` methods)
Abstraction: done (`Room`, `User`, `Bookable`)
Packages: done (model, dao, service, exception, util, ui, and `Main` created)
Exception handling: in progress (custom exception classes and JDBC wrapping in `DBConnection`/importer)
Database design (PK, FK, relationships): done in `src/main/resources/schema.sql` and applied to local MySQL
JDBC connectivity: done (local MySQL80)
CRUD, Create: in progress (`dao/JdbcRoomDao.create()`)
CRUD, Read: in progress (`dao/JdbcRoomDao.findById()`, `findAll()`)
CRUD, Update: in progress (`dao/JdbcRoomDao.update()`)
CRUD, Delete: in progress (`dao/JdbcRoomDao.delete()`)
Transaction-based conflict prevention: in progress (`dao/JdbcBookingDao.java` locks the room and checks timetable/approved-booking overlap in one transaction; database execution pending)
7. Feature progress
Room availability lookup: done (live console query verified against imported timetable)
Timetable and booking conflict detection: in progress (transactional DAO implemented; live booking attempt pending)
Faculty booking dashboard: in progress (console booking flow complete; Swing deferred)
Student free-period room finder: in progress (console availability search complete; Swing deferred)
Utilization reports: not started
Documentation and demo notes: not started

Use one of: not started, in progress, done, blocked.

8. Open issues and bugs
(none yet)

Format: (date) Issue, where it happens, what was tried, status.

9. Next steps

In priority order. The next agent starts here.

Run the schema, import the real data, and verify console availability/booking against MySQL.
Add remaining entity DAOs and booking CRUD/status operations.
Execute the JUnit suite once Maven is installed or configured.
10. Notes for the evaluation and demo

Anything useful to say out loud when explaining the project, for example how a booking conflict is detected, where each OOP concept lives, and why the transaction matters.

(add as the project grows)
