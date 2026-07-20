# Advanced SQL — Reference Notes

Beyond the [SQL Primer](../sql/README.md): relational design (relationships,
normalization) and query techniques (join types, window functions). Part of
the [database notes](../README.md).

## Notes

- [Database Normalization](normalization-notes.md) — data redundancy &
  anomalies (insert/update/delete); normal forms 1NF–BCNF with worked
  examples; primary/foreign keys as the mechanism that re-links normalized
  tables.
- [JOIN Types](join-types-notes.md) — cross join (Cartesian product, use
  cases, combining with aggregates/`WHERE`, performance caution); inner join
  in depth (use cases, combining with `GROUP BY`/aggregates/`GROUP_CONCAT`/
  `HAVING`); left/right (outer) join (`NULL`-filling the unmatched side,
  finding unmatched rows, inner vs left vs right); advanced join operations
  (chaining 3+ table joins with mixed types, indexing join columns, reading
  an `EXPLAIN` plan).
- [Window Functions](window-functions-notes.md) — `PARTITION BY`;
  `ROW_NUMBER()`, `RANK()`, `NTILE(n)`, `FIRST_VALUE()`/`NTH_VALUE()`,
  `CUME_DIST()`, `PERCENT_RANK()`; aggregate functions (`SUM()`, `AVG()`,
  `COUNT()`, `MIN()`/`MAX()`) run as partitioned window functions; frame
  clauses `RANGE BETWEEN` vs `ROWS BETWEEN`; practical applications (salary
  ranking, running sales totals, moving averages, customer segmentation).
- [Window Functions — Combining &
  Optimizing](window-functions-optimization-notes.md) — using several
  window functions in one query pass; performance techniques (filtering
  rows before windowing, efficient partitioning, minimizing sorts, CTEs vs.
  nested subqueries); indexing `PARTITION BY`/`ORDER BY` columns; reading
  an `EXPLAIN` plan for window-function queries.

> One-to-many relationships (the customer/orders, author/books style FK
> pattern) and the basic inner-`JOIN` mechanics (`ON`, matching FK to PK) are
> covered in the SQL Primer's [Table
> Constraints](../sql/table-constraints-notes.md#one-to-many-relationships)
> and [DQL — `JOIN`](../sql/dql-notes.md#join--combining-tables) notes, since
> that content landed there before this module existed.

## Case studies

- [Students/Courses/Enrollments Schema
  Review](students-courses-schema-review.md) — a worked exercise applying
  the notes above to a concrete schema: constraints/indexing for a
  many-to-many junction table, why it's already close to 3NF/BCNF,
  `INNER JOIN` vs. `LEFT JOIN` for reports that must include unmatched
  rows, and window functions layered over the same join.
