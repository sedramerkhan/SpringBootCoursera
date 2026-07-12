# Data Query Language (DQL) — Notes

**DQL** is the category for **retrieving** data — reading, not changing. It
centers on `SELECT`, refined by clauses that **filter** (`WHERE`), **group**
(`GROUP BY` / `HAVING`), **sort** (`ORDER BY`), and **cap** (`LIMIT`) the result.
One of the [five SQL command categories](databases-and-sql-intro-notes.md#what-is-sql).

## `SELECT` — retrieve columns

List the columns you want, then the table they come from. Columns not listed
aren't returned, even if the table has them.

```sql
SELECT column1, column2 FROM table_name;

SELECT student_id, name, age FROM student;
```

**All columns** — use `*`:

```sql
SELECT * FROM course;      -- every column (and its data)
```

`SELECT` can also return **computed expressions**, not just raw columns. Use
`AS` to name (alias) the result:

```sql
SELECT call_duration * rate_per_minute AS total_bill FROM call_record;
```

## `WHERE` — filter rows

Returns only rows meeting a condition. Combine conditions with `AND` / `OR`, and
match ranges with `BETWEEN`.

```sql
SELECT student_id, name, age FROM student WHERE age > 20;

-- multiple conditions + a date range
SELECT * FROM call_record
WHERE caller_id = 505
  AND call_date BETWEEN '2026-07-01' AND '2026-07-31';
```

## Aggregate functions

Aggregates collapse **many rows into a single value**. The common five:

| Function | Returns |
|---|---|
| `COUNT(...)` | Number of rows |
| `SUM(...)` | Total of a numeric column |
| `AVG(...)` | Average |
| `MIN(...)` / `MAX(...)` | Smallest / largest |

```sql
SELECT COUNT(*)              AS student_count FROM student;
SELECT SUM(total_amount)     AS revenue       FROM orders;
SELECT AVG(age)              AS avg_age        FROM student;
```

## `GROUP BY` — aggregate per group

Without `GROUP BY`, an aggregate reduces the **whole table** to one row.
`GROUP BY` splits rows into groups and applies the aggregate to **each group**.

```sql
-- total quantity sold, per product
SELECT product_id, SUM(quantity) AS total_sold
FROM orders
GROUP BY product_id;
```

> **Rule:** every column in the `SELECT` list that is *not* inside an aggregate
> must appear in `GROUP BY`. (Here `product_id` is grouped; `SUM(quantity)` is
> aggregated — nothing else is selected.)

## `HAVING` — filter groups

`WHERE` filters **rows before** grouping; `HAVING` filters **groups after**
aggregation. An aggregate (`SUM`, `COUNT`, …) **cannot** go in `WHERE` — that's
what `HAVING` is for.

```sql
-- only products that sold more than 100 units
SELECT product_id, SUM(quantity) AS total_sold
FROM orders
GROUP BY product_id
HAVING SUM(quantity) > 100;
```

### `WHERE` vs `HAVING`

| | Filters | Runs | Can use aggregates? |
|---|---|---|---|
| `WHERE` | Individual rows | **Before** grouping | No |
| `HAVING` | Whole groups | **After** aggregation | Yes |

You can use both together: `WHERE` trims the rows that feed the groups, then
`HAVING` trims the resulting groups.

## `ORDER BY` — sort the result

Sorts by a column or alias, ascending (`ASC`, the default) or descending
(`DESC`).

```sql
SELECT student_id, name, age FROM student ORDER BY age DESC;
```

## `LIMIT` — cap the rows returned

Returns at most N rows — often paired with `ORDER BY` for "top N".

```sql
... ORDER BY total_sold DESC LIMIT 5;
```

## Clause order

Clauses must be **written** in this order:

```
SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT
```

But the database **evaluates** them roughly in this order:

```
FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT
```

That evaluation order explains a common gotcha: a column **alias** defined in
`SELECT` can be used in `ORDER BY` (evaluated later) but **not** in `WHERE`
(evaluated earlier).

## Putting it together — worked patterns

These recur across industries (e-commerce, banking, healthcare, telecom,
manufacturing…) — all the same `SELECT` / `WHERE` / `GROUP BY` / aggregate
toolkit.

```sql
-- Top 5 best-selling products (e-commerce)
SELECT product_id, SUM(quantity) AS total_sold
FROM orders
GROUP BY product_id
ORDER BY total_sold DESC
LIMIT 5;

-- Monthly phone bill: expression + date range (telecom)
SELECT SUM(call_duration * rate_per_minute) AS total_bill
FROM call_record
WHERE caller_id = 505
  AND call_date BETWEEN '2026-07-01' AND '2026-07-31';
```

> The industry "scenarios" (retrieve a customer's orders, record a transaction,
> look up a patient, enrol a student) are just applications of `SELECT` +
> [`INSERT`/`UPDATE`](dml-notes.md) with a `WHERE` — no new syntax, plus the
> aggregates/grouping above for reports.

## Summary

- DQL **reads** data without modifying it.
- `SELECT` retrieves columns (`*` for all) and computed expressions (`AS` to
  alias); `WHERE` filters rows; `ORDER BY` sorts; `LIMIT` caps.
- **Aggregates** (`COUNT`/`SUM`/`AVG`/`MIN`/`MAX`) collapse rows; `GROUP BY`
  makes them per-group; `HAVING` filters groups (`WHERE` filters rows).
- Written order: `SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT`.
