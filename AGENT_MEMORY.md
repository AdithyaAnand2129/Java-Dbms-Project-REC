RoomWise: Agent Memory

This is the shared memory for every agent working on RoomWise. Read it at the start of every session. Update it at the end of every session. Keep entries short, factual and honest.

Read AGENT_INSTRUCTIONS.md first for the project rules.

1. Current status
Last updated: 2026-10-28 by Copilot
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
(2026-10-28) Decision: Consolidate all application Java classes into `src/main/java/com/roomwise/Main.java` at the user's request to make the codebase easier to explain. Trade-off: package-level separation is no longer visible as separate source folders. Made by: user.
(2026-10-28) Decision: Keep the required com.roomwise package layers instead of merging every class into Main.java. Reason: package separation is a documented graded OOP requirement and the tests depend on the package structure. Made by: Copilot.
(2026-10-28) Decision: Flatten all Java classes into `src/main/java/com/roomwise` and the test into `src/test/java/com/roomwise`, using one `com.roomwise` package. Reason: user requested a single simple class folder; behavior and class boundaries remain unchanged. Made by: Copilot.
(2026-10-28) Decision: Combine every production class into `src/main/java/com/roomwise/Main.java` and remove the separate production `.java` files. Reason: user explicitly requested all classes in the main Java file. Made by: Copilot.
(2026-10-28) Decision: Remove the `src/test` folder and its test source. Reason: user requested a simpler project tree and does not want the test folder. Made by: Copilot.
(2026-10-28) Decision: Remove Docker from the project setup. Reason: user discarded the Docker approach and will use local MySQL Server with JDBC and db.properties. Made by: user.
(2026-10-28) Decision: Use `real_data.sql` as the MySQL Workbench load script generated from both supplied JSON exports. Reason: user requested real data instead of sample data. Made by: user.
(2026-10-28) Decision: Allow duplicate source staff codes and distinguish courses by `(course_code, name)`. Reason: the real exports contain six staff-code collisions and eighteen course-code/name collisions. Made by: Copilot.

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

### Session 11 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Run the RoomWise application.
- **What was done:** Compiled `Main.java` with JDK 21 and the local MySQL Connector/J jar, copied runtime resources into a temporary `run-out` classpath, and launched the default Swing application. Startup verified the local MySQL database and loaded the timetable data successfully.
- **Tests run and result:** Application startup passed; console output reached `Database ready.` and the Swing process remained running.
- **What is unfinished or broken:** None for this run request. The temporary `RoomWise/run-out` directory is ignored generated output.
- **Next steps recommended:** Stop the running Java process when the application is no longer needed.

### Session 5 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Simplify the project tree without changing application behavior.
- **What was done:** Removed generated `RoomWise/out` and `RoomWise/target` artifacts and root IntelliJ `.idea` metadata. Added `out/` to `RoomWise/.gitignore`. Preserved all Java source packages, tests, resources, timetable data, and Maven configuration.
- **Tests run and result:** Maven was unavailable on PATH. All main Java sources compiled successfully with JDK 21 and the local MySQL Connector/J jar. The JUnit test sources also compiled successfully using the local JUnit dependencies; compiler emitted dependency-metadata warnings only.
- **What is unfinished or broken:** The Maven test runner was not executed because Maven is not installed or configured on PATH.
- **Next steps recommended:** Install/configure Maven when available and run `mvn test`; continue feature work without restoring generated output to version control.

### Session 6 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Remove Java subfolders and make the class tree simpler.
- **What was done:** Moved all 34 production classes into `RoomWise/src/main/java/com/roomwise` and the test into `RoomWise/src/test/java/com/roomwise`. Changed their package declarations/imports to the single `com.roomwise` package and removed the empty Java subfolders. Class responsibilities and application logic were preserved.
- **Tests run and result:** Main sources compiled successfully with JDK 21 and MySQL Connector/J. Test sources compiled successfully with the local JUnit dependencies; only missing optional API metadata warnings were emitted. Maven remains unavailable on PATH.
- **What is unfinished or broken:** The source tree no longer demonstrates separate Java packages; this is an intentional simplification requested by the user.
- **Next steps recommended:** Run `mvn test` after Maven is installed, then use the flat `com.roomwise` folder as the classroom explanation layout.

### Session 7 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Put all Java classes into the main Java file and remove unwanted class files.
- **What was done:** Combined all 34 production classes into `RoomWise/src/main/java/com/roomwise/Main.java`. Kept `Main` public and changed the other top-level classes, interfaces, enums, and exceptions to package-private so one Java file compiles. Removed the other 33 production `.java` files.
- **Tests run and result:** `Main.java` compiled successfully with JDK 21 and MySQL Connector/J. The JUnit test source compiled successfully against the combined output; only missing optional API metadata warnings were emitted. Maven remains unavailable on PATH.
- **What is unfinished or broken:** The project intentionally uses one large source file and no longer has separate production source files, as requested.
- **Next steps recommended:** Run `mvn test` after Maven is installed.

### Session 8 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Remove the unwanted test folder.
- **What was done:** Removed `RoomWise/src/test`, which contained the standalone availability-service test. The production source remains consolidated in `Main.java`.
- **Tests run and result:** Not run because the test source was intentionally removed at the user's request.
- **What is unfinished or broken:** Automated tests are no longer present in the simplified project tree.
- **Next steps recommended:** Use the application compile check when source changes are made.

### Session 9 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Remove the discarded Docker setup and references.
- **What was done:** Removed Docker wording from JDBC error messages, setup documentation, SQL comments, and `db.properties.example`. Removed the obsolete `.env` ignore entry. The project now documents only local MySQL Server setup.
- **Tests run and result:** Main.java compiled successfully with JDK 21 and MySQL Connector/J after the changes.
- **What is unfinished or broken:** Maven remains unavailable on PATH; no Docker setup remains.
- **Next steps recommended:** Configure local MySQL credentials in `src/main/resources/db.properties` before running the application.

### Session 10 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Replace the optional sample SQL data with the supplied real timetable in a MySQL Workbench-ready format.
- **What was done:** Generated `RoomWise/src/main/resources/real_data.sql` from `data/theory.json` and `data/labs.json`, containing 4,167 timetable slots plus normalized buildings, rooms, faculty, courses, and offerings. Removed `sample_data.sql` and documented the Workbench import flow in both README files. Updated the schema and importer to preserve source collisions rather than merging distinct real records.
- **Tests run and result:** Generated SQL counts verified: 9 buildings, 157 rooms, 491 faculty, 300 courses, 1,239 offerings, and 4,167 slots. Java compilation passed with JDK 21 and MySQL Connector/J; SQL batch counts and transaction wrapper validation passed.
- **What is unfinished or broken:** MySQL command-line client is unavailable on PATH, so the generated script has not been executed from the command line.
- **Next steps recommended:** Run the real SQL through MySQL Workbench against a fresh schema; the MySQL command-line client is not installed on PATH for automated execution here.

### Session 4 — 2026-10-28
- **Agent/Person:** Copilot
- **Goal:** Include the remaining current project files in the Phase 1 checkpoint.
- **What was done:** Added the IntelliJ project metadata, supplied timetable JSON files, and generated `RoomWise/out` artifacts that were present locally. Local database properties were intentionally excluded because they contain credentials.
- **Files created or changed:** `.idea/**`, `RoomWise/data/labs.json`, `RoomWise/data/theory.json`, and non-secret files under `RoomWise/out/**`.
- **Tests run and result:** Not run; this checkpoint update does not change source behavior.
- **What is unfinished or broken:** Generated Maven output under `RoomWise/target/**` remains ignored, and local credential files remain excluded.
- **Next steps recommended:** Continue implementation from the updated Phase 1 checkpoint.

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
- **What was done:** Audited the existing unrecorded Maven scaffold and model hierarchy. Updated the schema to support imported college room IDs and normalized course offerings. Added timetable import, room availability, conflict-safe booking, and a console UI. The user later consolidated all Java code into a single `Main.java` to simplify the explanation, and the project guide now maps its sections to the relevant concepts.
- **Files created or changed:** `RoomWise/src/main/resources/schema.sql`, `sample_data.sql`, `.gitignore`; added importer, parser, room/booking DAOs, availability/booking services, console UI, entry point, test, and `RoomWise/data/README.md`.
- **Tests run and result:** `javac` compilation of all main source: passed. Parser validation against full supplied files: passed (3,030 theory and 1,137 lab records parsed). Local MySQL schema creation and import: passed (4,167 records). Live console availability search: passed. JUnit test was added but cannot be run until Maven is available.
- **What is unfinished or broken:** Maven and MySQL command-line client are unavailable on PATH. End-to-end JUnit run and remaining entity DAOs remain.
- **Next steps recommended:** Verify a live booking attempt, add remaining entity DAOs and booking CRUD/status operations, then install/configure Maven to execute JUnit tests.

5. Database schema (current)

Keep this in sync with schema.sql. List tables, keys and relationships as they actually exist.

Tables defined in `schema.sql`: buildings, rooms, equipment, room_equipment, users, courses, course_offerings, timetable_slots, bookings, conflict_log. Database created locally and populated with the supplied timetable exports.
Schema changes history:
2026-10-28: Added `college_room_id` and normalized `course_offerings`; timetable slots now point to offerings rather than a single-faculty course, matching the real college data.
2026-10-28: Removed uniqueness from `users.staff_code` and changed courses to a unique `(course_code, name)` pair so all supplied real records can be represented.
6. Concept-to-code table (for the evaluation)

Faculty will ask the team to show every concept in the code. Fill in the real class and file names as they are written. Leave "not yet" until it exists.

Encapsulation: done (`Room.java`, `User.java`, `TimetableSlot.java`)
Inheritance: done (`Classroom`, `Lab`, `SeminarRoom` extend `Room`; user subclasses extend `User`)
Polymorphism, runtime: done (room subclasses override `getAllowedPurposes()`)
Polymorphism, compile time: done (`AvailabilityService.java`: overloaded `search(...)` methods)
Abstraction: done (`Room`, `User`, `Bookable`)
Packages: simplified to one `com.roomwise` package at the user's request
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
