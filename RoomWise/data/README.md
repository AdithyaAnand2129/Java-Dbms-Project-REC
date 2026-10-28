# Timetable source data

The full college timetable exports are included in this folder:

- `theory.json` — 3,030 theory records
- `labs.json` — 1,137 lab records

Run `schema.sql` once, configure `src/main/resources/db.properties`, then run:

```text
java -cp target/classes;path\\to\\mysql-connector-j.jar com.roomwise.util.CollegeTimetableImporter data/theory.json data/labs.json
```

The importer accepts the original names and locations too. It parses all theory
and lab records, normalizes day aliases such as `wed` and `wednesday`, and loads
the recurring weekly schedule into MySQL.
