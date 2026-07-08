# Table Constraints — Notes

**Constraints** are rules applied to columns to enforce **data integrity** — they
guarantee the data stored obeys defined conditions. Set at `CREATE TABLE` time
(see [Data Definition Language (DDL)](ddl-notes.md)) or added
later with `ALTER TABLE`.

## The common constraints

| Constraint | Guarantees |
|---|---|
| `NOT NULL` | Column cannot be empty (no null values) |
| `UNIQUE` | All values in the column are distinct |
| `CHECK` | Values satisfy a specific condition |
| `PRIMARY KEY` | Uniquely identifies each row — **unique + not null** |
| `FOREIGN KEY` | Referential integrity — links to a primary key in another table |

## `UNIQUE`

No two rows may share the same value. Useful for emails, phone numbers, SSNs.

```sql
CREATE TABLE student (
    student_id INT UNIQUE,
    name       VARCHAR(255)
);
```

## `CHECK`

Enforces a custom condition on inserted values.

```sql
CREATE TABLE student (
    student_id INT,
    age        INT CHECK (age > 18)      -- only 18+ can be inserted
);

CREATE TABLE employee (
    emp_id INT,
    salary DECIMAL(10, 2) CHECK (salary >= 0)   -- no negative salary
);
```

## `PRIMARY KEY`

Uniquely identifies each row: values must be **unique** *and* **not null**.

```sql
CREATE TABLE student (
    student_id INT PRIMARY KEY,
    name       VARCHAR(255)
);
```

### `PRIMARY KEY` vs `UNIQUE`

| | Duplicates? | Nulls allowed? | Per table |
|---|---|---|---|
| `PRIMARY KEY` | No | **No** | One |
| `UNIQUE` | No | Yes (typically one null) | Many |

## `FOREIGN KEY`

Links two tables to form a relationship: a foreign-key column points to the
**primary key** of another table. The referenced value must exist there
(referential integrity). Conventionally the FK column name matches the PK it
references.

```sql
CREATE TABLE enrollment (
    enrollment_id INT PRIMARY KEY,
    student_id    INT,
    course_id     INT,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (course_id)  REFERENCES course(course_id)
);
```

## Adding constraints later — `ALTER TABLE`

Constraints don't have to be defined up front; `ALTER TABLE` adds them
afterward.

```sql
ALTER TABLE student
ADD CONSTRAINT fk_department
FOREIGN KEY (department_id) REFERENCES department(department_id);
```

## Multiple constraints per column vs. multi-column constraints

**Different constraints on different columns** (one table, per-column rules):

```sql
CREATE TABLE student (
    student_id INT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    age        INT CHECK (age > 18)
);
```

A single column can also carry more than one constraint (e.g.
`name VARCHAR(255) NOT NULL UNIQUE`).

**Multi-column (composite) constraint** — one rule spanning *several columns
together*. Here the *combination* of `student_id` + `course_id` must be unique,
so a student can enroll in a given course only once:

```sql
CREATE TABLE enrollments (
    student_id INT,
    course_id  INT,
    CONSTRAINT uc_enrollment UNIQUE (student_id, course_id)
);
```

## Summary

- Constraints protect **data integrity**: `NOT NULL`, `UNIQUE`, `CHECK`,
  `PRIMARY KEY`, `FOREIGN KEY`.
- `PRIMARY KEY` = `UNIQUE` + `NOT NULL`; `FOREIGN KEY` links to another table's
  primary key for referential integrity.
- A column can hold several constraints; a **composite** constraint enforces a
  rule across multiple columns at once.
- Add constraints after creation with `ALTER TABLE ... ADD CONSTRAINT`.
