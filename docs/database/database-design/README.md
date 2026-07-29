# Database Design — Reference Notes

Why database design matters and the lifecycle for doing it well. Part of the
[database notes](../README.md).

## Notes

- [Design Lifecycle](design-lifecycle-notes.md) — why design drives
  performance/scalability/maintainability; the five stages (requirement
  analysis → conceptual design/ERD → logical design/schema →
  physical design/indexing → implementation), each illustrated with a library
  management system example.
- [Terminology](terminology-notes.md) — the core vocabulary: entity,
  attribute, relationship (1:1/1:N/N:N), primary key, foreign key, schema,
  and normalization vs. denormalization, worked through a school database
  example.
- [Domain & Data Models](modeling-notes.md) — **domain model**: UML-flavored,
  DBMS-independent (association/aggregation/composition relationships),
  e-commerce/healthcare/education/finance examples. **Data model**: the
  conceptual/logical/physical progression, logical (ERD) vs. physical
  (tables/types/constraints) in detail, e-commerce/healthcare/education
  examples.
- [Entity-Relationship Diagrams (ERDs)](erd-notes.md) — the full ERD
  reference: basic notation (rectangle/oval/diamond, entity sets, attributes
  on relationships), advanced notation (attribute types, cardinality &
  participation constraints, weak entity sets), Extended ER/EER
  (specialization — overlapping/disjoint, total/partial; generalization —
  total/partial; aggregation; triangle & double-diamond notation; a
  Person/Student/Instructor + Project worked example), and building ERDs in
  MySQL Workbench (reverse-engineer or build from scratch).
- [Scalability & Performance](scalability-performance-notes.md) — scaling
  principles (horizontal/sharding vs. vertical, replication, load
  balancing); growth techniques (partitioning, archiving, storage engine
  choice); performance techniques (indexing, query optimization via
  `EXPLAIN`, partitioning); worked SQL examples for an index, a
  column-limited `SELECT`, and range-partitioning a `sales` table by year.
