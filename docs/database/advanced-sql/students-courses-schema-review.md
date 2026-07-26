# Case Study — Reviewing the Students/Courses/Enrollments Schema

A worked discussion answering a schema-review exercise: given the
`Students`/`Courses`/`Enrollments` schema below, what would you change for
data integrity and query performance? Cross-links back to the reference
notes ([Normalization](normalization-notes.md), [JOIN
Types](join-types-notes.md), [Window Functions](window-functions-notes.md))
rather than repeating their content.

## The schema as given

```sql
CREATE TABLE Students (
    student_id INT,
    name VARCHAR(100),
    age INT,
    PRIMARY KEY (student_id)
);

CREATE TABLE Courses (
    course_id INT,
    course_name VARCHAR(100),
    PRIMARY KEY (course_id)
);

CREATE TABLE Enrollments (
    enrollment_id INT,
    student_id INT,
    course_id INT,
    PRIMARY KEY (enrollment_id),
    FOREIGN KEY (student_id) REFERENCES Students(student_id),
    FOREIGN KEY (course_id) REFERENCES Courses(course_id)
);
```

Sample data: two students (John Doe, Jane Smith), two courses (Database
Systems, Algorithms), three enrollments — John takes both courses, Jane
takes Database Systems only.

```sql
SELECT Students.name, Courses.course_name
FROM Enrollments
INNER JOIN Students ON Enrollments.student_id = Students.student_id
INNER JOIN Courses ON Enrollments.course_id = Courses.course_id;
```

`Enrollments` is the **junction table** resolving the many-to-many
relationship between `Students` and `Courses` — the query already joins
*through* it rather than a direct `CROSS JOIN` between `Students` and
`Courses`, which is the correct shape. The issues are elsewhere: the
constraints `Enrollments` is missing, and the fact that `INNER JOIN`
quietly drops anyone with no matching row on either side.

## Issues and opportunities identified

- **No uniqueness constraint on `Enrollments`.** Nothing stops the exact
  same `(student_id, course_id)` pair from being inserted twice — John
  could be enrolled in Database Systems three times over, and the schema
  would allow it silently.
- **FK columns aren't guaranteed `NOT NULL`.** As written, `student_id` or
  `course_id` on an enrollment row could be left `NULL`, producing an
  enrollment that doesn't actually point at a student or a course.
- **No `ON DELETE`/`ON UPDATE` policy declared on the foreign keys.**
  Leaving this at the database default (typically `NO ACTION`/`RESTRICT`)
  is a decision, but it should be an explicit one — e.g. deleting a
  student while their enrollment rows still reference them will simply
  fail unless the schema says otherwise.
- **No indexes on the FK columns.** Whether this matters depends on the
  engine: MySQL/InnoDB auto-creates an index backing a foreign key,
  but PostgreSQL and SQL Server do **not** — an unindexed FK column means
  every join or `ON DELETE` check falls back to a full scan of
  `Enrollments`. Don't assume; check with `EXPLAIN`.
- **No `CHECK` constraint on `age`.** Nothing stops a negative or
  absurd age from being inserted.
- **`enrollment_id` is never used downstream.** It's still the right PK
  choice (a natural key would be the `(student_id, course_id)` pair, which
  should instead become a `UNIQUE` constraint, not the primary key,
  so a student can be re-enrolled after being dropped without reusing an
  identity) — just noting it doesn't appear in the report query, which is
  expected for a display query.

## Q1 — Constraints and indexing for `Enrollments`

```sql
ALTER TABLE Enrollments
  MODIFY student_id INT NOT NULL,
  MODIFY course_id  INT NOT NULL;

ALTER TABLE Enrollments
  ADD CONSTRAINT uq_student_course UNIQUE (student_id, course_id);

CREATE INDEX idx_enrollments_student ON Enrollments (student_id);
CREATE INDEX idx_enrollments_course  ON Enrollments (course_id);
```

- The `UNIQUE (student_id, course_id)` constraint is the data-integrity
  fix: it makes a duplicate enrollment a constraint violation instead of a
  silent extra row, and as a side effect gives the database a composite
  index it can use for "is this student in this course" lookups.
- `NOT NULL` on both FK columns rules out enrollment rows that don't
  actually reference anyone.
- The two single-column indexes speed up the two join directions the
  sample query uses (`Enrollments.student_id → Students`,
  `Enrollments.course_id → Courses`) and are also what the `FOREIGN KEY`
  constraints themselves will consult on `DELETE`/`UPDATE` to the
  referenced tables.
- Declare the FK delete behavior explicitly rather than leaving the
  default: `ON DELETE CASCADE` if a deleted student's enrollment history
  should disappear with them, `ON DELETE RESTRICT` if it should block the
  delete until enrollments are cleaned up first, or `ON DELETE SET NULL`
  only if `student_id`/`course_id` are allowed to be nullable (which the
  `NOT NULL` change above rules out).

## Q2 — Normalization

The schema is already close to 3NF/BCNF, and that's not an accident: the
`Enrollments` junction table is exactly [how a many-to-many relationship
gets normalized](normalization-notes.md) — instead of one row per student
with a repeating list of courses (which would violate 1NF) or duplicating
`course_name`/`age` onto every enrollment row (a transitive dependency,
violating 3NF), the M:N relationship is resolved into its own table keyed
by a surrogate `enrollment_id`, with `student_id`/`course_id` as FKs
re-linking back to the normalized `Students`/`Courses` tables.

- **1NF** — every column is atomic (no repeating group of courses per
  student row).
- **2NF/3NF** — both `Students` and `Courses` have single-column primary
  keys, so there's no partial-key dependency to violate 2NF over, and
  neither table's non-key columns (`name`/`age`, `course_name`) depend on
  anything but that table's own PK — no transitive dependency, so 3NF
  holds. The same reasoning makes them BCNF as well, since the only
  determinant is the primary key.
- **Beyond 3NF** — there's no evidence here of the kind of independent
  multivalued facts that would call for 4NF; going further than 3NF/BCNF
  isn't warranted by what's shown.

The forward-looking risk: if `Enrollments` later grows columns like
`professor` or `room` that actually depend on `(course_id, semester)`
rather than on the enrollment itself, that reintroduces a transitive
dependency and 3NF breaks again — those belong on a separate
`CourseOfferings`/`Sections` table, not bolted onto `Enrollments`.

## Q3 — `INNER JOIN` vs. `LEFT JOIN`

The provided query's `INNER JOIN` is the right choice for "show me
students together with the courses they're enrolled in" — every row it
returns represents a real enrollment, which is what an enrollment report
should show. But `INNER JOIN` requires a match on **both** sides, so it
silently excludes:

- Students with **zero** enrollments (e.g. a student who just registered)
  — they'd vanish from the report entirely rather than showing up with no
  course.
- Courses with **zero** enrollments, if the query were instead driven from
  `Courses`.

Where a `LEFT JOIN` is more appropriate depends on the question being
asked:

```sql
-- "Every student, including those not enrolled in anything"
SELECT s.name, c.course_name
FROM Students s
LEFT JOIN Enrollments e ON e.student_id = s.student_id
LEFT JOIN Courses c ON c.course_id = e.course_id;
```

```sql
-- "Every course, including those nobody has enrolled in"
-- (e.g. flagging under-enrolled courses for cancellation)
SELECT c.course_name, s.name
FROM Courses c
LEFT JOIN Enrollments e ON e.course_id = c.course_id
LEFT JOIN Students s ON s.student_id = e.student_id;
```

Both produce `NULL` in the unmatched side's columns (`course_name`/`name`
respectively) for the students or courses with no enrollment — that
`NULL` is itself the useful signal ("this student isn't enrolled in
anything yet") that the original `INNER JOIN` query can't surface.

## Q4 — Window functions over this schema

There's no grade or score column here, but enrollment **counts** are
enough to demonstrate a couple of useful window functions layered onto
the same join from the original query — no `GROUP BY` needed, so the
per-enrollment detail rows are preserved alongside the aggregate:

```sql
SELECT s.name, c.course_name,
       COUNT(*) OVER (PARTITION BY e.course_id) AS CourseEnrollmentCount,
       ROW_NUMBER() OVER (PARTITION BY e.student_id ORDER BY e.enrollment_id) AS StudentEnrollmentOrder
FROM Enrollments e
INNER JOIN Students s ON e.student_id = s.student_id
INNER JOIN Courses c ON e.course_id = c.course_id;
```

Against the three sample enrollment rows — `(1, John, DB Systems)`,
`(2, John, Algorithms)`, `(3, Jane, DB Systems)`:

| name | course_name | CourseEnrollmentCount | StudentEnrollmentOrder |
|---|---|---|---|
| John Doe | Database Systems | 2 | 1 |
| John Doe | Algorithms | 1 | 2 |
| Jane Smith | Database Systems | 2 | 1 |

`CourseEnrollmentCount` — `COUNT(*)` [partitioned](window-functions-notes.md#partition-by--resetting-the-calculation-per-group)
by `course_id` — attaches "how many students are in this course" to every
row without collapsing the student-level detail; Database Systems shows
`2` on both of its rows since John and Jane are both enrolled in it.
`StudentEnrollmentOrder` uses [`ROW_NUMBER()`](window-functions-notes.md#row_number--a-unique-sequential-number-per-partition)
to number each student's own enrollments in the order they were created
(by `enrollment_id`) — useful for something like "which course did this
student sign up for first."

A second, reporting-oriented example ranks courses by overall popularity,
combining a `GROUP BY` aggregate with `RANK()` over the grouped result
(see [`RANK()`](window-functions-notes.md#rank--rank-with-gaps-on-ties)):

```sql
SELECT c.course_name, COUNT(*) AS enrollment_count,
       RANK() OVER (ORDER BY COUNT(*) DESC) AS PopularityRank
FROM Enrollments e
INNER JOIN Courses c ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name;
```

Database Systems (2 enrollments) ranks `1`, Algorithms (1 enrollment)
ranks `2` — the kind of query that scales directly to "which courses
should we open more sections of" once the sample data grows past two
courses.
