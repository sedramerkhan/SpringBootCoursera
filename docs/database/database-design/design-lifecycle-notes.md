# Database Design Lifecycle — Notes

**Database design** is the process of structuring a database so it stores and
retrieves data efficiently. Good design isn't cosmetic — it directly drives
three outcomes:

- **Performance** — efficient data retrieval and storage (e.g. the right
  [indexes](../indexing/README.md) on frequently-queried columns).
- **Scalability** — the database keeps working as data volume and user count
  grow.
- **Maintainability** — the schema is easy to update and manage over time.

## Why it matters — a poorly-designed example

Consider an e-commerce database with no design discipline:

- **No indexing** — searching for a product means a full table scan, so
  queries slow down as the catalog grows, degrading the user experience. See
  [indexing notes](../indexing/README.md) for the structures that fix this.
- **Data redundancy** — the same customer info (name, email) copied into every
  order row. Updating a customer means finding and fixing every copy, which is
  cumbersome and error-prone. This is exactly what
  [normalization](../advanced-sql/normalization-notes.md) exists to eliminate.

A well-designed database — proper indexing + normalization — gets fast
queries *and* data integrity, and is simpler to maintain going forward.

## The five stages

| Stage | Question it answers | Output |
|---|---|---|
| **1. Requirement analysis** | What data do stakeholders need? | Entities, attributes, relationships |
| **2. Conceptual design** | How do those things relate, independent of any DBMS? | ERD (entity-relationship diagram) |
| **3. Logical design** | What tables/columns/types implement that model? | Logical schema (tables, FKs) |
| **4. Physical design** | How is it stored for performance? | Indexes, partitions, storage params |
| **5. Implementation** | Build it for real | Tables created, populated, tested |

Each stage builds on the previous one's output — skipping straight to
"implementation" is how the poorly-designed example above happens.

### 1. Requirement analysis

Gather requirements from stakeholders to understand the data needs: identify
key **entities**, their **attributes**, and the **relationships** between
them, making sure everything ties back to business objectives and user needs.

**Example — library management system:**

- Entities: `Books`, `Authors`, `Borrowers`
- Attributes: `title`, `author's name`, `borrower's ID`
- Relationship: *borrowers* **borrow** *books*

### 2. Conceptual design

Turn the requirements into an **ERD** (entity-relationship diagram) —
defining entities, their attributes, and primary keys, independent of any
specific database product. This is the stage for validating the model
*accurately reflects* the requirements before committing to a schema.

**Example — library management system:**

- `Books` — attributes include `ISBN`, `title`, `publication year`
- Relationship: an `Author` **writes** `Books`

### 3. Logical design

Convert the conceptual (ERD) model into a **logical schema**: concrete
tables, columns, data types, and the foreign keys that establish
relationships between tables. This is where
[normalization](../advanced-sql/normalization-notes.md) rules (1NF–BCNF) get
applied so the schema avoids redundancy and anomalies.

**Example — library management system:**

```sql
CREATE TABLE Author (
    author_id INT PRIMARY KEY,
    name      VARCHAR(100)
);

CREATE TABLE Book (
    isbn        VARCHAR(20) PRIMARY KEY,
    title       VARCHAR(200),
    pub_year    INT,
    author_id   INT,
    FOREIGN KEY (author_id) REFERENCES Author(author_id)
);

CREATE TABLE Borrower (
    borrower_id INT PRIMARY KEY,
    name        VARCHAR(100)
);
```

A foreign key (`Book.author_id`) links `Book` to `Author`; a similar FK
relationship links borrowing activity to `Borrower`.

### 4. Physical design

Design the physical storage structures: **indexes**, **partitions**, and
storage parameters, chosen so the physical layer actually delivers the
performance the logical model promises.

**Example — library management system:**

- Index `Book.isbn` and `Borrower.borrower_id` — both are frequently queried,
  so an index turns a full scan into a direct lookup (see [B-tree](../indexing/b-tree-notes.md)
  / [B+ tree](../indexing/b-plus-tree-notes.md) notes for how).
- **Partition** large tables (e.g. a `Loan` history table) to spread data
  across storage units and keep queries fast as the table grows.

### 5. Implementation

Build the database for real: create the tables with SQL (see
[DDL](../sql/ddl-notes.md)), **populate** them with initial data
([DML](../sql/dml-notes.md)), then **test** — run queries against the
relationships to confirm both correctness and performance match expectations.

## Summary

- Good design = **performance** (fast retrieval) + **scalability** (handles
  growth) + **maintainability** (easy to change).
- Skipping design shows up as slow queries (missing indexes) and redundant,
  error-prone data (no normalization).
- Lifecycle: **requirement analysis** (entities/attributes/relationships) →
  **conceptual design** (ERD) → **logical design** (schema + FKs, apply
  normalization) → **physical design** (indexes/partitions) →
  **implementation** (build, populate, test).
