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

> One-to-many relationships (the customer/orders, author/books style FK
> pattern) and the basic inner-`JOIN` mechanics (`ON`, matching FK to PK) are
> covered in the SQL Primer's [Table
> Constraints](../sql/table-constraints-notes.md#one-to-many-relationships)
> and [DQL — `JOIN`](../sql/dql-notes.md#join--combining-tables) notes, since
> that content landed there before this module existed.

*Coming in this module:* window functions (`PARTITION BY` and practical
applications).
