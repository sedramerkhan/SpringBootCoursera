# Database — Reference Notes

The database half of **Java Database Engineering: Spring Boot MVC & SQL Mastery**,
grouped into four areas. Each has its own indexed reference:

- **[SQL](sql/README.md)** — the SQL Primer: foundations, DDL, data types,
  constraints, DML, DQL (incl. aggregates/grouping), DCL, TCL, string functions,
  and best practices. Organized by the five command categories.
- **[Advanced SQL](advanced-sql/README.md)** — relational design and query
  techniques beyond the Primer: normalization (1NF–BCNF), JOIN types (cross,
  inner, left/right, advanced/multi-join), and window functions
  (`PARTITION BY`, ranking/distribution functions).
- **[PL/SQL](plsql/README.md)** — Oracle's procedural extension: subprograms
  (functions & procedures) and triggers.
- **[Indexing](indexing/README.md)** — DBMS indexing structures: ordered indices
  (dense/sparse, clustering/secondary, multilevel), B-tree, B+ tree, and
  hashing (static/dynamic, bucket overflow, hashing vs ordered indices).

> These four are siblings so notes cross-link freely (e.g. PL/SQL triggers →
> SQL constraints, `../sql/...`). Plain **SQL** vs **PL/SQL**: SQL is the
> declarative query language; PL/SQL is Oracle's procedural language built on top
> of it.
