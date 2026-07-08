# SQL Best Practices & Pitfalls — Notes

Module 1 wrap-up: how to write **effective, maintainable** SQL, the common
mistakes to avoid, and a few habits worth building. The command-by-command
detail lives in the [other notes](README.md) — this is the cross-cutting advice.

## Best practices

| Practice | Why |
|---|---|
| **Consistent naming** | Clear, uniform names for tables, columns, and indexes make queries readable and predictable. Pick a convention (e.g. `snake_case`, singular table names) and stick to it. |
| **Comment your code** | Explain complex queries and business logic so the next reader (often future-you) understands the *intent*, not just the syntax. |
| **Modularize** | Break large queries into smaller reusable pieces — **views** for saved queries, **CTEs** (`WITH …`) for readable multi-step logic. |
| **Use transactions** | Wrap multi-step operations in a [transaction](tcl-notes.md) so they're all-or-nothing and the data can't be left half-updated. |
| **Review & refactor** | Revisit SQL periodically to simplify, fix, and keep it reliable as requirements change. |

## Common pitfalls to avoid

- **Ignoring indexes** — an **index** is a lookup structure that lets the engine
  find rows without scanning the whole table. Without one, filters/joins on big
  tables get slow. Add indexes on columns you frequently filter or join on.
  *(Trade-off: indexes speed up reads but slow down writes and use space — index
  deliberately, not everywhere.)*
- **Over-using subqueries** — deeply nested subqueries are hard to read and often
  slower; a **`JOIN`** is usually clearer and lets the optimizer do its job.
- **Neglecting transactions** — any operation with multiple steps should be
  transactional, or a mid-way failure leaves inconsistent data.
- **Hardcoding values** — don't bake literals into queries. Use **parameters /
  bind variables** for flexibility *and* safety — parameterized queries are the
  primary defense against **SQL injection**.
- **`SELECT *`** — fetching every column wastes I/O, breaks when the schema
  changes, and hides intent. **List the columns you actually need.**
- **Lack of comments** — undocumented complex logic becomes unmaintainable.

## Final tips

- **Stay updated** — keep up with current SQL standards and features.
- **Practice regularly** — fluency comes from writing queries, not reading about
  them.
- **Use tools** — a good DB client / query analyzer helps you write and
  **optimize** (read the *query plan* to see where time goes).
- **Learn from real-world scenarios** — study real use cases and apply the
  patterns (see the [DQL worked patterns](dql-notes.md#putting-it-together--worked-patterns)).

## Summary

- Write for the **next reader**: consistent names, comments, small modular
  pieces.
- Protect **integrity** with transactions; protect **performance** with indexes
  and joins over nested subqueries.
- Protect **security** with parameters, not hardcoded/concatenated values.
- Be explicit: list columns instead of `SELECT *`, and refactor as you go.
