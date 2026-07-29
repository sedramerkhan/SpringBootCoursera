# Normalization & Denormalization — Notes

**Normalization** is the process of organizing tables to minimize **data
redundancy** and prevent **data anomalies**, by structuring data so each fact
is stored once and non-key attributes depend only on the table's key. It
builds on [primary/foreign keys](../sql/table-constraints-notes.md) and the
[one-to-many relationships](../sql/table-constraints-notes.md#one-to-many-relationships)
they enforce.

## Features of good relational design

- **Minimal redundancy** — reduces storage and the risk of inconsistent
  copies of the same fact.
- **Easy update/deletion** — data can be modified without anomalies; changes
  and removals don't create integrity issues elsewhere.
- **Support for data integrity** — enforced rules keep stored data accurate
  and consistent with real-world constraints and relationships.
- **Optimal data retrieval** — effective querying/access, which matters most
  as a database grows.

## Why normalize

- **Minimizes redundancy** — each piece of information stored **once**, saving
  space and avoiding the risk of copies drifting out of sync.
- **Prevents anomalies** during insert/update/delete (see below).
- **Preserves integrity** — enforcing dependency rules keeps relationships and
  constraints accurate, making retrieval and manipulation more reliable.

## Functional dependency & decomposition

**Functional dependency (FD)** is the theory underpinning normalization.
Attribute `B` is **functionally dependent** on attribute `A` (written
`A → B`, "`A` determines `B`") if every valid value of `A` corresponds to
exactly one value of `B`. E.g. in a customer table, `customer_id →
customer_address` — a given customer ID always maps to the same address.
FDs are what let you formalize *which* attributes belong together in the
same table, and are the basis normal forms are defined against.

Normalization proceeds by **decomposing** a table into smaller tables along
its functional dependencies, to remove redundancy and update anomalies:

| Decomposition | Effect |
|---|---|
| **Lossless** | The original relation `R` can be **exactly reconstructed** by joining the decomposed tables back together — no data is lost or fabricated |
| **Lossy** | Joining the decomposed tables back together loses information or introduces spurious rows not in the original data |

**Example** — decomposing `R(A, B, C)` into `R1` and `R2` on `B` is lossless
only if joining `R1 ⋈ R2` on `B` reproduces `R` exactly. A decomposition that
can't guarantee that reconstruction isn't safe to use — every normal form
below assumes its decomposition step is lossless.

## Data redundancy & anomalies

**Data redundancy** — the same fact stored in multiple rows (e.g. a
customer's name and email repeated on every one of their orders). Beyond
wasted space, it risks **inconsistency** if not every copy gets updated.

Redundancy causes three **data anomalies**:

| Anomaly | Happens when | Example |
|---|---|---|
| **Update** | The same fact is duplicated across rows | A customer's email is stored on every order row; missing even one row during an update leaves conflicting data |
| **Insertion** | A row can't be added without unrelated data being present | A new course can't be recorded until a student enrolls in it, if course info only exists inside the enrollment table |
| **Deletion** | Removing one fact accidentally removes another | Deleting a student's only enrollment record also deletes the only record of that course's existence |

### Example — eliminating redundancy with a FK

Before: an `Orders` table repeats `CustomerName`/`CustomerEmail` on every row
for the same customer. After normalizing:

```sql
CREATE TABLE Customers (
    CustomerID    INT PRIMARY KEY,
    CustomerName  VARCHAR(100),
    CustomerEmail VARCHAR(100)
);

CREATE TABLE Orders (
    OrderID    INT PRIMARY KEY,
    OrderDate  DATE,
    CustomerID INT,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);
```

Each customer's details live in **exactly one row** of `Customers`; `Orders`
just references it by ID. This is the same [one-to-many
pattern](../sql/table-constraints-notes.md#one-to-many-relationships) covered
separately — normalization is *why* that pattern is the right shape.

## Normal forms

Progressive rules, each building on the one before:

| Form | Eliminates | Rule |
|---|---|---|
| **1NF** | Repeating groups | Every field holds a single **atomic** value (no comma-packed lists) |
| **2NF** | Partial dependency | 1NF, **and** every non-key attribute depends on the **whole** primary key (matters when the key is composite) |
| **3NF** | Transitive dependency | 2NF, **and** every non-key attribute depends **only** on the primary key — not on another non-key attribute |
| **BCNF** | Remaining anomalies 3NF misses | For every dependency `A → B`, `A` must be a **super key** |

4NF and 5NF exist (multi-valued and join dependencies) but aren't covered here.

### 1NF — atomic values

**Before** — `courses` packs multiple values into one field (a **repeating
group**); `student` alone can't even be a key since it's not unique:

| student | courses |
|---|---|
| John | Maths, Science |
| Priya | Maths, History, Art |

**After** — one course per row, so every field holds a single atomic value:

```sql
CREATE TABLE student_course (student VARCHAR(100), course VARCHAR(100));
```

| student | course |
|---|---|
| John | Maths |
| John | Science |
| Priya | Maths |
| Priya | History |
| Priya | Art |

### 2NF — remove partial dependency

**Before** — the key is the **composite** `(student_id, course)`, but
`instructor` only depends on `course` (part of the key) — proof: whenever
`course = Maths`, `instructor` is always `Mr. Smith`, **regardless of which
student**:

| student_id | course | instructor |
|---|---|---|
| 1 | Maths | Mr. Smith |
| 1 | Science | Ms. Jones |
| 2 | Maths | Mr. Smith |

That repetition of `Mr. Smith` across students 1 and 2 is the redundancy 2NF
removes — and it's also an **update anomaly** waiting to happen: rename him
and you must catch every row.

**After** — split so every non-key attribute depends on its *whole* key:

```sql
CREATE TABLE enrollment (student_id INT, course_id VARCHAR(20), PRIMARY KEY (student_id, course_id));
CREATE TABLE course     (course_id VARCHAR(20) PRIMARY KEY, instructor VARCHAR(100));
```

`enrollment` (just the many-to-many link, no redundancy possible):

| student_id | course_id |
|---|---|
| 1 | Maths |
| 1 | Science |
| 2 | Maths |

`course` (`Mr. Smith` now stored **once**, not once per enrolled student):

| course_id | instructor |
|---|---|
| Maths | Mr. Smith |
| Science | Ms. Jones |

### 3NF — remove transitive dependency

**Before** — key is `(student_id, course_id)`, but `instructor_email` doesn't
depend on that key at all — it depends on `instructor`, itself a **non-key**
attribute. That's the transitive chain: `(student_id, course_id) →
instructor → instructor_email`:

| student_id | course_id | instructor | instructor_email |
|---|---|---|---|
| 1 | Maths | Mr. Smith | smith@school.edu |
| 2 | Maths | Mr. Smith | smith@school.edu |
| 1 | Science | Ms. Jones | jones@school.edu |

`smith@school.edu` is repeated because two students share the Maths course —
the same update-anomaly risk as 2NF, one level further down the chain.

**After** — pull the instructor's own attributes into their own table, keyed
by `instructor_id`, so `course` only references *which* instructor, not their
details:

```sql
CREATE TABLE enrollment (student_id INT, course_id VARCHAR(20), PRIMARY KEY (student_id, course_id));
CREATE TABLE course     (course_id VARCHAR(20) PRIMARY KEY, instructor_id INT);
CREATE TABLE instructor (instructor_id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(100));
```

`enrollment` (unchanged from the 2NF fix):

| student_id | course_id |
|---|---|
| 1 | Maths |
| 2 | Maths |
| 1 | Science |

`course` (references the instructor by ID, not by name):

| course_id | instructor_id |
|---|---|
| Maths | 101 |
| Science | 102 |

`instructor` (name **and** email now stored **once** per instructor):

| instructor_id | name | email |
|---|---|---|
| 101 | Mr. Smith | smith@school.edu |
| 102 | Ms. Jones | jones@school.edu |

Now `instructor_email` depends only on `instructor_id` — its own primary key
— not transitively through the `instructor` name. The same idea applies
whenever two facts (e.g. a department's name and its head) both hang off one
non-primary attribute: pull them into their own table keyed by that
attribute.

### BCNF — beyond 3NF

3NF can still allow anomalies when a table has **multiple overlapping
candidate keys**. the Boyce-Codd normal form tightens the rule: for every dependency `A → B`, `A`
alone must already be a super key. It's a stricter version of 3NF used when
those more complex, overlapping-key dependencies show up.

**Example — a table not in BCNF:**

| student_id | course_id | instructor |
|---|---|---|
| 1 | Maths | Mr. Smith |
| 2 | Maths | Mr. Smith |
| 1 | Science | Ms. Jones |

Here `(student_id, course_id) → instructor` holds, but so does
`course_id → instructor` — and `course_id` alone is **not** a super key (it
doesn't uniquely identify rows on its own; `Maths` appears twice). That
determinant-that-isn't-a-super-key is exactly what BCNF forbids. Fixing it
means the same [course/instructor split shown under
3NF](#3nf--remove-transitive-dependency) above: pull `course_id →
instructor` into its own `course` table, keyed by `course_id`.

### BCNF vs. 3NF

| | 3NF | BCNF |
|---|---|---|
| **Redundancy** | Can still allow some redundancy when candidate keys overlap | Removes all redundancy of this kind |
| **Guarantee** | Preserves functional dependencies | Every determinant is a super key — stricter |
| **Practicality** | More practical for most applications — balances normalization with performance | Best when data integrity is paramount and redundancy must be eliminated |

Pick **3NF** for most applications, where some redundancy is tolerable but
dependency preservation matters. Pick **BCNF** when redundancy itself is the
risk to eliminate.

### Goals of normalization

Overall, normalization aims to:

- **Achieve BCNF** where practical — structure the database so no
  determinant fails the super-key test.
- **Preserve lossless-join decomposition** — every split must allow exact
  reconstruction of the original data via `JOIN`.
- **Preserve functional dependencies** — the relationships and constraints
  between attributes stay enforceable after decomposition.
- **Balance normalization with performance** — full normalization isn't
  free; see [denormalization](#denormalization) below for when to trade some
  of it back for read speed.

## Primary & foreign keys recap

- **Primary key** — uniquely identifies each row, guaranteeing no duplicates.
- **Foreign key** — links a row to the primary key of another table,
  enforcing referential integrity across the normalized tables (e.g.
  `enrollment.student_id` must reference an existing `student`).

Normalization splits data into multiple tables; primary/foreign keys are what
keep those split tables **correctly linked** back together (see
[`JOIN`](../sql/dql-notes.md#join--combining-tables) to query across them).

## Denormalization

**Denormalization** is the reverse of normalization: deliberately combining
normalized tables to improve **read** performance, at the cost of
reintroducing redundancy. It's a trade-off, not a mistake — normalization
optimizes for write-side integrity; denormalization optimizes for read-side
speed, and which one wins depends on the workload.

**Example**: separate `Order` and `Customer` tables (the normalized shape)
get denormalized by folding `Customer` fields directly onto `Order`, so a
query no longer needs a `JOIN` to read them together. **Pre-computing**
values (e.g. storing an order's total instead of summing line items on every
read) is the same idea — trade storage/redundancy for query speed.

### When to consider it

- **High-read, low-write workloads** — read operations vastly outnumber
  writes, so the write-side cost of redundancy matters less than read speed.
- **Performance-critical querying** — normalized joins are measurably too
  slow for the required query performance.
- **Complex queries** — normalization has pushed a query across enough
  joined tables that the query itself has become complex and slow.

This is the same normalized-OLTP vs. denormalized-OLAP split covered in
[terminology](../database-design/terminology-notes.md#normalization-vs-denormalization)
and in [data transformation](../../api/data-transformation-notes.md#normalization-vs-denormalization).

### Trade-offs

- **Extra storage** — redundant copies of the same data cost space.
- **Update-anomaly risk returns** — redundant copies must be kept in sync;
  miss one and data drifts inconsistent.
- **More complex application code** — the code writing to a denormalized
  structure has to manage that redundancy itself, since the schema no longer
  enforces it.

Denormalize only when the read-performance win is worth these costs — i.e.
when it measurably improves query efficiency without compromising the data
integrity the application actually depends on.

## Summary

- **Good relational design** has minimal redundancy, easy update/deletion,
  enforced data integrity, and optimal retrieval.
- **Functional dependency** (`A → B`: `A` uniquely determines `B`) is the
  theory normal forms are built on; normalizing means **decomposing** a
  table along its FDs, and a decomposition must be **lossless** (the
  original table is reconstructable via `JOIN`) to be valid.
- Normalization reduces **redundancy** and prevents **insertion/update/deletion
  anomalies**.
- **1NF**: atomic fields, no repeating groups. **2NF**: 1NF + no partial
  dependency (whole composite key, not part of it). **3NF**: 2NF + no
  transitive dependency (only the key, not another non-key attribute).
  **BCNF**: every determinant is a super key — stricter than 3NF, at the
  cost of some practicality.
- Normalization's goals: achieve BCNF where practical, keep decomposition
  lossless, preserve functional dependencies, and balance normalization
  against performance.
- Primary keys enforce uniqueness; foreign keys re-link normalized tables and
  enforce referential integrity.
- **Denormalization** trades some of that back — merging tables /
  pre-computing values to cut joins and speed up reads — worthwhile in
  high-read/low-write or performance-critical scenarios, at the cost of
  extra storage, anomaly risk, and more complex application code.
