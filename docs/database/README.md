# Database — Reference Notes

The database half of **Java Database Engineering: Spring Boot MVC & SQL Mastery**,
grouped into five areas. Each has its own indexed reference:

- **[Database Design](database-design/README.md)** — why design drives
  performance/scalability/maintainability, the five-stage lifecycle
  (requirement analysis, conceptual/ERD, logical schema, physical/indexing,
  implementation), core terminology (entity, attribute, relationship,
  primary/foreign key, schema, normalization vs. denormalization), domain &
  data models (UML relationships, conceptual/logical/physical progression),
  the full ERD reference (basic + advanced notation, Extended ER/EER, MySQL
  Workbench), and scalability & performance (sharding/replication/load
  balancing, partitioning, indexing, query optimization).
- **[SQL](sql/README.md)** — the SQL Primer: foundations, DDL, data types,
  constraints, DML, DQL (incl. aggregates/grouping), DCL, TCL, string functions,
  and best practices. Organized by the five command categories.
- **[Advanced SQL](advanced-sql/README.md)** — relational design and query
  techniques beyond the Primer: normalization & denormalization (functional
  dependency, decomposition, 1NF–BCNF, when to denormalize), JOIN types
  (cross, inner, left/right, advanced/multi-join), and window functions
  (`PARTITION BY`, ranking/distribution functions).
- **[PL/SQL](plsql/README.md)** — Oracle's procedural extension: subprograms
  (functions & procedures) and triggers.
- **[Indexing](indexing/README.md)** — DBMS indexing structures: ordered indices
  (dense/sparse, clustering/secondary, multilevel), B-tree, B+ tree, and
  hashing (static/dynamic, bucket overflow, hashing vs ordered indices).

> These five are siblings so notes cross-link freely (e.g. PL/SQL triggers →
> SQL constraints, `../sql/...`). Plain **SQL** vs **PL/SQL**: SQL is the
> declarative query language; PL/SQL is Oracle's procedural language built on top
> of it.
