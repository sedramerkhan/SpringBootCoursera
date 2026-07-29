# Database Design Terminology — Notes

The core vocabulary used throughout database design, worked through a school
database (`Student`, `Course`, `Enrollment`) as the running example.

## Entity & attribute

- **Entity** — an object or concept you want to store information about; a
  *category* of data. E.g. `Student`, `Course`. Entities are the fundamental
  building blocks of a database.
- **Attribute** — a property/characteristic of an entity; the specific pieces
  of data that describe it. E.g. `Student` has `student_id`, `name`, `age`;
  `Course` has `course_id`, `course_name`, `credits`.

## Relationship

Describes how entities interact — the connections that link entities
together meaningfully. E.g. a `Student` **enrolls in** a `Course` — the
`enrolls` relationship connects the two entities.

Relationships come in three cardinalities:

| Cardinality | Meaning |
|---|---|
| **One-to-one** | One row in A relates to exactly one row in B |
| **One-to-many** | One row in A relates to many rows in B |
| **Many-to-many** | Many rows in A relate to many rows in B (needs a junction table, e.g. `Enrollment`) |

## Primary key & foreign key

- **Primary key** — a unique identifier for a record in a table; guarantees no
  two rows collide. E.g. `student_id` on `Student`, `course_id` on `Course`.
  Essential for data integrity and efficient retrieval.
- **Foreign key** — a field in one table that references the primary key of
  another table, creating the link between them. E.g. `Enrollment.student_id`
  is a foreign key referencing `Student.student_id`, so every enrollment
  record traces back to a specific student, keeping data consistent.

```sql
CREATE TABLE Student (
    student_id INT PRIMARY KEY,
    name       VARCHAR(100),
    age        INT
);

CREATE TABLE Course (
    course_id   INT PRIMARY KEY,
    course_name VARCHAR(100),
    credits     INT
);

CREATE TABLE Enrollment (
    student_id INT,
    course_id  INT,
    FOREIGN KEY (student_id) REFERENCES Student(student_id),
    FOREIGN KEY (course_id)  REFERENCES Course(course_id)
);
```

## Schema

The overall structure of a database — how tables, columns, and relationships
are organized. It's the **blueprint** for how data is stored, giving the
designer a way to plan and visualize the structure and flow of data before
building it. E.g. a school database schema defines the `Student`, `Course`,
and `Enrollment` tables and the relationships between them.

## Normalization vs. denormalization

- **[Normalization](../advanced-sql/normalization-notes.md)** — organizing
  data to reduce redundancy and improve integrity, by splitting one wide table
  into smaller related tables (e.g. separating `Student` info from
  `Enrollment` records so a student's details are stored once). If a
  student's info changes, it's updated in exactly one place.
- **[Denormalization](../advanced-sql/normalization-notes.md#denormalization)**
  — the reverse: combining normalized tables back together to improve *read*
  performance. Merging `Student` and `Enrollment` into one table removes the
  joins a query would otherwise need, at the cost of reintroducing
  redundancy. Worth it when read speed matters more than storage efficiency
  or write-side integrity (high-read/low-write, performance-critical
  queries) — the same
  [OLTP-normalized vs. OLAP-denormalized](../../api/data-transformation-notes.md#normalization-vs-denormalization)
  trade-off that shows up in analytics/reporting systems.

Choosing between them is a balance: normalize for data integrity and
efficient writes, denormalize for fast reads.

## Summary

| Term | Definition |
|---|---|
| **Entity** | Object/concept the database stores info about (e.g. `Student`) |
| **Attribute** | A property of an entity (e.g. `name`, `age`) |
| **Relationship** | How entities connect (one-to-one, one-to-many, many-to-many) |
| **Primary key** | Uniquely identifies a row in its own table |
| **Foreign key** | References another table's primary key, linking tables |
| **Schema** | The blueprint: tables + columns + relationships |
| **Normalization** | Split tables to remove redundancy, improve integrity |
| **Denormalization** | Merge tables to cut joins, improve read performance |
