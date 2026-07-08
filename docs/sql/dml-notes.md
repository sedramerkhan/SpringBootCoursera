# Data Manipulation Language (DML) — Notes

**DML** is the category of SQL commands that **manipulate the data** inside an
existing structure — as opposed to [DDL](ddl-notes.md), which defines the
structure itself. One of the
[five SQL command categories](databases-and-sql-intro-notes.md#what-is-sql).
Three commands, all operating at the **row level**:

| Command | Purpose |
|---|---|
| [`INSERT INTO`](#insert-into) | Add new rows |
| [`UPDATE`](#update) | Modify existing rows |
| [`DELETE`](#delete) | Remove rows |

## `INSERT INTO`

Adds new rows — one at a time or many at once. (A table must exist first — see
[DDL](ddl-notes.md).) Missing data can be handled with `NULL` or a column
`DEFAULT`.

### Single-row insert

```sql
INSERT INTO table_name (column1, column2, column3)
VALUES (v1, v2, v3);
```

- List the **columns** you're filling, then the matching **values in the same
  order** — order and count must line up or data integrity breaks.
- **Quote text/date values** (`VARCHAR`, `CHAR`, dates) in single quotes;
  numbers are unquoted. Quoting lets the engine validate the value's type.

```sql
INSERT INTO student (student_id, name, age, major)
VALUES (1, 'John', 20, 'Computer Science');
```

### Multiple-row insert

Same command — just add more value tuples separated by commas, ending with a
semicolon. More efficient than many single inserts (one operation).

```sql
INSERT INTO student (student_id, name, age, major)
VALUES
    (2, 'Smith', 22, 'Maths'),
    (3, 'Brown', 21, 'Engineering'),
    (4, 'White', 19, 'Physics');
```

### Handling `NULL` values

Use the `NULL` keyword to store "no value" — the data is **missing or not
applicable** — instead of leaving it blank.

```sql
INSERT INTO student (student_id, name, age, major)
VALUES (5, 'Tom', NULL, 'History');   -- age unknown / not provided
```

### Default values

Define a `DEFAULT` on a column so that inserts which omit it get the default
automatically.

```sql
CREATE TABLE student (
    student_id INT,
    name       VARCHAR(255),
    age        INT DEFAULT 18,
    major      VARCHAR(255) DEFAULT 'Undeclared'
);

-- age and major omitted → filled with 18 and 'Undeclared'
INSERT INTO student (student_id, name)
VALUES (1, 'Alice');
```

### `NULL` vs `DEFAULT`

| | Meaning | When it applies |
|---|---|---|
| `NULL` | Value is unknown / not applicable | You explicitly insert `NULL` (column must allow it) |
| `DEFAULT` | Fall back to a predefined value | You **omit** the column from the `INSERT` |

## `UPDATE`

Modifies existing data. `SET` assigns new column values; `WHERE` limits which
rows change.

```sql
UPDATE table_name
SET column1 = value1, column2 = value2
WHERE condition;

-- set student 1's age to 21
UPDATE student SET age = 21 WHERE student_id = 1;

-- bump course 102's credits to 5
UPDATE course SET credits = 5 WHERE course_id = 102;
```

> ⚠️ **Always include `WHERE`.** An `UPDATE` with no `WHERE` changes **every
> row** in the table. Filtering on a primary key (e.g. `student_id`) targets
> exactly one row.

## `DELETE`

Removes rows matching a condition. Combine conditions with `AND` / `OR`.

```sql
DELETE FROM table_name WHERE condition;

DELETE FROM student WHERE student_id = 1;
DELETE FROM student WHERE student_id = 1 AND age = 20;   -- both must match
DELETE FROM course  WHERE course_id = 10;
```

> ⚠️ **`DELETE` with no `WHERE` empties the whole table**, row by row. (Contrast
> with `TRUNCATE`, a DDL command that empties faster but can't filter — see
> [DDL notes](ddl-notes.md#truncate--empty-the-table).)

## `DELETE` vs `DROP` vs `TRUNCATE`

| | `DELETE` | `TRUNCATE` | `DROP` |
|---|---|---|---|
| Category | DML | DDL | DDL |
| Unit of work | Each row | Table's storage pages | The table object |
| Removes | Chosen rows | All rows | The whole table |
| Keeps structure? | Yes | Yes | **No** |
| `WHERE` filter? | Yes | No | No |
| Logs each row? | Yes (slow on big tables) | No (minimal, fast) | No |
| Resets auto-increment? | No | **Yes** | n/a |
| Fires row triggers? | Yes | No | No |
| Rollback? | Yes | Often no / limited | Often no / limited |

> **Why is `TRUNCATE` DDL if it keeps the structure?** The category is decided by
> *how* the command works, not what it appears to affect. `DELETE` edits **rows**
> (DML); `TRUNCATE` deallocates the table's **storage pages** to reset the object
> to empty — an object-level action, so it's DDL alongside `CREATE`/`ALTER`/`DROP`.
> The surviving schema is a side effect. More in [DDL notes](ddl-notes.md#why-is-truncate-ddl-if-it-keeps-the-structure).

## Summary

- DML changes **data**, not structure: `INSERT` (add), `UPDATE` (modify),
  `DELETE` (remove) — all row-level.
- `UPDATE` and `DELETE` **must** carry a `WHERE` clause, or they hit every row.
- Keeps data accurate and up to date within the existing schema.
