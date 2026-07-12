# Data Query Language (DQL) — Notes

**DQL** is the category for **retrieving** data — reading, not changing. It
centers on `SELECT`, refined by clauses that **filter** (`WHERE`, `LIKE`),
**dedupe** (`DISTINCT`), **group** (`GROUP BY` / `HAVING`), **sort**
(`ORDER BY`), and **cap** (`LIMIT`) the result.
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

## `JOIN` — combining tables

Combines rows from two (or more) tables based on a related column — typically
a [foreign key](table-constraints-notes.md#foreign-key) in one table pointing
at a primary key in another. Plain `JOIN` (**inner join**) keeps only rows
where the `ON` condition matches in **both** tables.

```sql
CREATE TABLE Customers (
    CustomerID   INT PRIMARY KEY,
    CustomerName VARCHAR(100)
);

CREATE TABLE Orders (
    OrderID    INT PRIMARY KEY,
    OrderDate  DATE,
    CustomerID INT,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)
);

-- every order, with the placing customer's name
SELECT Customers.CustomerName, Orders.OrderID, Orders.OrderDate
FROM Customers
JOIN Orders ON Customers.CustomerID = Orders.CustomerID;
```

`FROM Customers` picks the starting table; `JOIN Orders ON ...` pulls in each
`Orders` row whose `CustomerID` matches a `Customers` row. This is the
standard way to query a [one-to-many relationship](table-constraints-notes.md#foreign-key)
(one customer → many orders) — without the join you'd only see one side of
the relationship at a time.

> Only the plain/inner `JOIN` is covered here; `LEFT`/`RIGHT`/`FULL OUTER JOIN`
> (which also keep non-matching rows from one side) aren't in scope yet.

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

### `LIKE` — pattern matching

Matches a column against a text **pattern** inside `WHERE`. Two wildcards:
`%` (any number of characters, including zero) and `_` (exactly one character).
Most database engines run `LIKE` **case-insensitively** by default.

```sql
-- supplier names starting with "CA" (e.g. "California Corp")
SELECT supplier_id, name, address FROM supplier
WHERE name LIKE 'CA%';
```

## `DISTINCT` — unique rows only

Placed right after `SELECT`; removes duplicate rows from the result so each
combination of the selected column(s) appears once.

```sql
-- each roll number appears once, sorted by age
SELECT DISTINCT roll_number FROM student ORDER BY age;
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

`SUM`/`AVG`/`MIN`/`MAX` need a **numeric** column. All of them (and
`COUNT(column)`, unlike `COUNT(*)`) **ignore `NULL`s** — a `NULL` row isn't
treated as `0`, it's simply excluded from the calculation.

## `GROUP BY` — aggregate per group

Without `GROUP BY`, an aggregate reduces the **whole table** to one row.
`GROUP BY` splits rows into groups and applies the aggregate to **each group**.

```sql
-- total quantity sold, per product
SELECT product_id, SUM(quantity) AS total_sold
FROM orders
GROUP BY product_id;

-- highest / lowest commission rate, per work area
SELECT working_area, MAX(commission) AS max_commission FROM agent GROUP BY working_area;
SELECT working_area, MIN(commission) AS min_commission FROM agent GROUP BY working_area;
```

`GROUP BY` is **optional** for an aggregate, not required — `MIN(commission)`
alone (no `GROUP BY`) returns the single lowest commission across the whole
table; adding `GROUP BY working_area` returns the lowest **per area** instead.

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

## Subqueries — nesting a query inside another

A **subquery** (inner query) is a `SELECT` nested inside another query's
`WHERE`, `FROM`, or `SELECT` clause; the outer query uses its result. A
**correlated** subquery references a column from the **outer** query, so it
re-runs **once per outer row** rather than just once.

```sql
-- authors with more than 5 books — the inner COUNT is recomputed per author
SELECT A.name
FROM Authors AS A
WHERE (
    SELECT COUNT(BA.book_id)
    FROM book_author AS BA
    WHERE BA.author_id = A.id
) > 5;
```

`BA.author_id = A.id` ties the subquery to the current outer row (`A`), making
it correlated; the outer `WHERE` then keeps only authors whose book count
exceeds `5`.

This same result is usually expressible with a `JOIN` + [`GROUP BY` /
`HAVING`](#having--filter-groups) instead, which most engines optimize better:

```sql
SELECT A.name
FROM Authors AS A
JOIN book_author AS BA ON BA.author_id = A.id
GROUP BY A.id, A.name
HAVING COUNT(BA.book_id) > 5;
```

Prefer the `JOIN`/`HAVING` form for cases like this; reach for a subquery when
there's no natural join shape (e.g. comparing a row against an aggregate over
a *different* table). See also: [pitfall — over-using
subqueries](best-practices-notes.md#common-pitfalls-to-avoid).

## `ORDER BY` — sort the result

Sorts by a column or alias, ascending (`ASC`, the default) or descending
(`DESC`).

```sql
SELECT student_id, name, age FROM student ORDER BY age DESC;
```

Sort by **multiple columns** — each can pick its own direction independently;
later columns break ties left by earlier ones.

```sql
-- roll number ascending; within a tie, name descending
SELECT name, roll_number FROM student_detail ORDER BY roll_number ASC, name DESC;
```

## `LIMIT` — cap the rows returned

Returns at most N rows — often paired with `ORDER BY` for "top N". An optional
`OFFSET` skips that many rows first (defaults to `0`).

```sql
SELECT * FROM student LIMIT 3;              -- first 3 rows

... ORDER BY total_sold DESC LIMIT 5;       -- top 5

SELECT * FROM student LIMIT 3 OFFSET 3;     -- skip 3, then take the next 3
```

### Where `LIMIT` can't be used

- **Inside a view definition** — a view represents the *complete* result set,
  not a truncated slice.
- **Inside most nested/subquery expressions** — e.g. a scalar subquery expected
  to return a single value can't cap itself with `LIMIT`. The exception is a
  subquery used as a **table expression in `FROM`**, which can use `LIMIT`
  freely since it's producing its own independent result set.

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
- `JOIN` combines rows from related tables via a foreign-key match (`ON`);
  plain `JOIN` is an **inner join** — only matching rows survive.
- `SELECT` retrieves columns (`*` for all) and computed expressions (`AS` to
  alias); `WHERE` filters rows (`LIKE` for pattern matches); `DISTINCT` dedupes;
  `ORDER BY` sorts (one or more columns, each with its own direction); `LIMIT`
  caps (optionally with `OFFSET` to skip rows first — but not in views or most
  subquery expressions).
- **Aggregates** (`COUNT`/`SUM`/`AVG`/`MIN`/`MAX`) collapse rows and need a
  numeric column (except `COUNT`), ignoring `NULL`s rather than treating them
  as `0`; `GROUP BY` makes them per-group (optional, not required) and
  `HAVING` filters groups (`WHERE` filters rows).
- A **subquery** nests a `SELECT` inside another query; **correlated**
  subqueries reference the outer row and re-run per row — often rewritable as
  a `JOIN` + `GROUP BY`/`HAVING`.
- Written order: `SELECT → FROM → WHERE → GROUP BY → HAVING → ORDER BY → LIMIT`.
