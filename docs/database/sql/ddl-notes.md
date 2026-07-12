# Data Definition Language (DDL) — Notes

**DDL** is the category of SQL commands that **define and manage database
structure** — the tables and other objects themselves, not the data inside them.
One of the [five SQL command categories](databases-and-sql-intro-notes.md#what-is-sql).

DDL shapes the container; [DML](dml-notes.md) and [DQL](dql-notes.md) work with
what's inside it.

## The DDL commands

| Command | Purpose |
|---|---|
| [`CREATE`](#create-table) | Create an object (table, schema, database) |
| [`ALTER`](#alter-table) | Modify an existing object |
| [`DROP`](#drop-table--remove-the-table) | Delete an object entirely |
| [`TRUNCATE`](#truncate--empty-the-table) | Empty a table but keep its structure |

Column data types and constraints used inside `CREATE`/`ALTER` have their own
notes: [Data Types](data-types-notes.md) and [Table Constraints](table-constraints-notes.md).

## `CREATE TABLE`

Defines a new table by listing each **column** with its **data type**. Tables are
made of rows and columns; every column must have a defined type (`INT`,
`VARCHAR`, …).

```sql
CREATE TABLE student (
    student_id INT,
    name       VARCHAR(255),
    age        INT,
    major      VARCHAR(255)
);
```

- `student` — the table name.
- `student_id INT` — integer column, a natural unique identifier per student.
- `name VARCHAR(255)` — **var**iable-length text, up to 255 characters.
- `age INT` — integer.
- `major VARCHAR(255)` — text up to 255 characters.
- The parentheses enclose the column list; this creates a structured table of
  four columns, each storing a specific type of data.

### Primary key

A **primary key** uniquely identifies each row — its value must be **unique** and
**not null**. (Full constraint coverage in [Table Constraints](table-constraints-notes.md).)

```sql
CREATE TABLE course (
    course_id   INT PRIMARY KEY,
    course_name VARCHAR(255),
    instructor  VARCHAR(255)
);
```

Here `course_id` is the primary key, so each course is uniquely identifiable.

### Auto-increment

`AUTO_INCREMENT` (MySQL) makes a column **auto-generate a unique, sequential
value** for every new row, removing the need to assign IDs manually. It's most
often paired with `PRIMARY KEY`, but isn't limited to it — any column that
needs guaranteed-unique values (e.g. one with a `UNIQUE` constraint) can use
it. By default it **starts at 1** and **increases by 1** per row.

```sql
CREATE TABLE student (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(25) NOT NULL,
    last_name  VARCHAR(25),
    age        INT
);

-- id is omitted — the database fills it in (1, 2, 3, ...)
INSERT INTO student (first_name, last_name, age) VALUES ('Rahul', 'Sharma', 21);
INSERT INTO student (first_name, last_name, age) VALUES ('Aakash', 'Gupta', 22);
```

**Changing the starting value** — `ALTER TABLE ... AUTO_INCREMENT = n`. This
only affects **rows inserted after** the change; existing rows keep their IDs.

```sql
ALTER TABLE student AUTO_INCREMENT = 100;

-- next insert (omitting id) gets 100, not 4
INSERT INTO student (first_name, last_name, age) VALUES ('David', 'Lee', 20);
```

See also: [`TRUNCATE`](#truncate--empty-the-table) resets an auto-increment
counter back to its start value, unlike `DELETE` (see
[DML — `DELETE` vs `DROP` vs `TRUNCATE`](dml-notes.md#delete-vs-drop-vs-truncate)).

## `ALTER TABLE`

Changes an **existing** table's structure, so the schema can evolve without
rebuilding the table. Four operations:

| Operation | Keyword | What it does |
|---|---|---|
| Add a column | `ADD` | Adds a new column (name + data type) |
| Remove a column | `DROP COLUMN` | Deletes the column **and its data** |
| Rename a column | `RENAME COLUMN ... TO` | Changes a column's name only |
| Change a type | `MODIFY COLUMN` | Changes a column's data type |

```sql
-- add a column
ALTER TABLE student ADD email VARCHAR(255);
-- drop a column (also drops its data — irreversible)
ALTER TABLE student DROP COLUMN age;
-- rename a column (data unaffected)
ALTER TABLE student RENAME COLUMN name TO full_name;
-- modify a column's type (safest before rows exist)
ALTER TABLE student MODIFY COLUMN age SMALLINT;
```

- `DROP COLUMN` destroys the column's data; `RENAME` leaves data intact;
  `MODIFY` is safest **before** rows exist (a type change can fail or lose data
  if existing values don't fit the new type). On types, see
  [Data Types](data-types-notes.md).
- `ALTER TABLE` is also how you add [constraints](table-constraints-notes.md)
  after the fact.

> **Worked example — phone numbers:** the course adds `phone_number VARCHAR(15)`,
> then suggests `INT` "since it stores numbers." In practice **`VARCHAR` is the
> right call** — phone numbers can have leading zeros, `+`, and separators a
> numeric type would strip.

## `DROP TABLE` — remove the table

Removes the **entire table** — structure and all its data — permanently. Use with
caution; it cannot be undone.

```sql
DROP TABLE student;
```

Contrast with [`ALTER TABLE ... DROP COLUMN`](#alter-table), which removes only
one column.

## `TRUNCATE` — empty the table

Deletes **all rows** but **keeps the table structure**, resetting it to an empty
state ready for reuse.

```sql
TRUNCATE TABLE student;
```

### Why is `TRUNCATE` DDL if it keeps the structure?

Because the classification is about **how the command works**, not what it
appears to affect. The precise rule isn't "DDL defines structure" — it's
**"DDL acts on database objects; DML acts on the data within them."**

- `DELETE` (DML) is a **row-level, logged** operation: it walks the table and
  removes rows one at a time, logging each so it can be rolled back, honoring
  `WHERE` and firing row triggers.
- `TRUNCATE` (DDL) never touches rows individually — it **deallocates the
  table's storage pages**, resetting the object to its freshly-created empty
  state. It operates on the object's storage, so it's grouped with `CREATE` /
  `ALTER` / `DROP`.

That one implementation difference explains everything else: `TRUNCATE` is fast
(minimal logging), can't take a `WHERE`, resets auto-increment counters, skips
row triggers, and is often not rollback-able. The surviving schema (columns,
types, constraints) is a *side effect*, not the reason for the category. Full
comparison: [`DELETE` vs `TRUNCATE` vs `DROP`](dml-notes.md#delete-vs-drop-vs-truncate).

## Viewing structure — `DESCRIBE`

Shows a table's structure (columns, data types, attributes) — **not** its data.

```sql
DESCRIBE student;   -- or: DESC student;
```

## Schemas

A **schema** defines the structure of your database — its tables, columns, and
constraints. It's also a **logical container** that groups related objects
(tables, views, procedures) so they're easier to organize and manage.

```sql
CREATE SCHEMA school;

CREATE TABLE school.student (
    student_id INT PRIMARY KEY,
    name       VARCHAR(255),
    major      VARCHAR(255),
    age        INT
);
```

- `CREATE SCHEMA school` — create a logical group named `school`.
- `school.student` — prefixing the table name with the schema places the table
  **inside** that schema.

## Quick reference

```sql
CREATE TABLE table_name ( col type, ... );   -- define a table
ALTER TABLE table_name ADD col type;          -- add/drop/rename/modify a column
DESCRIBE table_name;                          -- view structure
TRUNCATE TABLE table_name;                     -- delete all rows, keep structure
DROP TABLE table_name;                         -- delete table + data
CREATE SCHEMA schema_name;                      -- create a logical container
```

## Summary

- DDL = the **structure-defining** category: `CREATE`, `ALTER`, `DROP`,
  `TRUNCATE`.
- `CREATE TABLE` defines columns + types (+ optional constraints, incl.
  `AUTO_INCREMENT` for auto-generated sequential values); `ALTER TABLE`
  adds/drops/renames/retypes columns (and can reset the auto-increment start
  value); `DROP` removes the table; `TRUNCATE` empties it; `DESCRIBE` inspects
  structure.
- Schemas group related objects into a named container.
- For column types and constraints see [Data Types](data-types-notes.md) and
  [Table Constraints](table-constraints-notes.md).
