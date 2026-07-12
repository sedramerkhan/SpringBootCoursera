# JOIN Types — Notes

A catalog of SQL's **JOIN** variants — how each one decides which row
combinations survive. The general inner-join mechanics (`ON`, matching a
foreign key to a primary key) already live in
[DQL — `JOIN`](../sql/dql-notes.md#join--combining-tables) as part of the SQL
Primer's core `SELECT` toolkit; this note covers the **types** in depth,
starting with the one outlier that needs no relationship at all.

## Cross Join

Returns the **Cartesian product** of two tables: every row of the first
table paired with **every** row of the second — no `ON` condition, no
relationship between the tables required. Result size is always
`rows(table1) × rows(table2)` (e.g. 3 rows × 2 rows → 6 combinations).

```sql
SELECT * FROM table1 CROSS JOIN table2;
```

### Example

```sql
-- employee(employee_id, name, salary), department(department_id, department_name)
SELECT name, department_name
FROM employee
CROSS JOIN department;
```

With 3 employees (Alice, Bob, John) and 3 departments (HR, Engineering,
Marketing), this returns **9** rows — Bob paired with HR, Bob with
Engineering, Bob with Marketing, and the same for Alice and John.

### With an aggregate — measuring the product's size

```sql
SELECT COUNT(*) FROM employee CROSS JOIN department;   -- 3 x 3 = 9
```

### With a filter — narrowing the product down

A cross join can carry a `WHERE` just like any other query — it filters the
Cartesian product **after** it's built:

```sql
SELECT * FROM employee
CROSS JOIN department
WHERE department.department_name = 'HR';   -- only the HR-paired rows survive
```

### When it's useful

- **Generating test data** — e.g. every product × every customer, to exercise
  scale/performance.
- **All possible combinations** — schedules, pairings, or any case needing
  every combination between two sets.
- **Comparing unrelated tables** — merging two tables that have no natural
  relationship, purely to compute or compare across every pair.

### Performance caution

Because the result grows **multiplicatively**, a cross join on two large
tables can blow up in size and memory. Use it only when every-row-with-every-row
is genuinely what's needed, and add a `WHERE` to cut the result down whenever
possible.

## Inner Join

Merges rows from two (or more) tables based on a **related column** — only
rows with a **match in both tables** make it into the result; unmatched rows
on either side are dropped entirely. (This is the `JOIN` already covered in
[DQL — `JOIN`](../sql/dql-notes.md#join--combining-tables); this section goes
deeper on combining it with grouping/aggregates.)

```sql
SELECT columns
FROM table1
INNER JOIN table2 ON table1.common_column = table2.common_column;
```

- `table1` — the primary table `SELECT`s from.
- `INNER JOIN table2` — the second table to pull matching rows from.
- `ON ...` — the shared column(s) that decide which rows match, present in
  both tables.

### When it's useful

- **Fetching related data across tables** — building a comprehensive dataset
  out of pieces stored separately (e.g. customers + their orders).
- **Reports** — combining data from multiple tables into one view (e.g. a
  sales report joining customer data with order details) to analyze purchase
  behavior, order history, demographics, etc.

### Basic example

```sql
-- employee(employee_id, name, salary, department_id)
-- department(department_id, department_name)
SELECT *
FROM employees
INNER JOIN departments ON employees.DepartmentID = departments.DepartmentID;
```

Only employees who have a **matching** department row come back — an
employee with no valid `DepartmentID` would be silently excluded.

### With `GROUP BY` and aggregates

Inner join first, then group/aggregate over the joined result — same
[`GROUP BY`](../sql/dql-notes.md#group-by--aggregate-per-group) rules apply,
just spanning both tables now:

```sql
-- employees + total salary per department
SELECT departments.DepartmentName,
       COUNT(employees.EmployeeID) AS NumberOfEmployees,
       SUM(employees.Salary)       AS TotalSalary
FROM employees
INNER JOIN departments ON employees.DepartmentID = departments.DepartmentID
GROUP BY departments.DepartmentName;
```

1. `INNER JOIN` matches each employee to their department.
2. `GROUP BY DepartmentName` buckets the joined rows per department.
3. `COUNT`/`SUM` aggregate **within** each bucket — headcount and total payroll
   per department.

Swap in `AVG(employees.Salary)` for the average salary per department, or
`MIN`/`MAX(employees.Salary)` for the salary range per department — same
join, same `GROUP BY`, different aggregate.

### `GROUP_CONCAT` — collapsing a group into one string

```sql
SELECT departments.DepartmentName,
       GROUP_CONCAT(employees.Name SEPARATOR ', ') AS Employees
FROM employees
INNER JOIN departments ON employees.DepartmentID = departments.DepartmentID
GROUP BY departments.DepartmentName;
```

`GROUP_CONCAT` folds every `Name` in a group into a single
comma-separated string (e.g. `"Alice, Bob, John"`) — handy for a compact,
human-readable roster per department instead of one row per employee.

### Putting it together

All of the above can combine into one query — join, then compute several
aggregates and a `GROUP_CONCAT` per group in a single pass:

```sql
SELECT departments.DepartmentName,
       COUNT(employees.EmployeeID)                    AS NumberOfEmployees,
       SUM(employees.Salary)                          AS TotalSalary,
       AVG(employees.Salary)                          AS AvgSalary,
       MIN(employees.Salary)                           AS MinSalary,
       MAX(employees.Salary)                           AS MaxSalary,
       GROUP_CONCAT(employees.Name SEPARATOR ', ')     AS Employees
FROM employees
INNER JOIN departments ON employees.DepartmentID = departments.DepartmentID
GROUP BY departments.DepartmentName;
```

### Filtering groups — `HAVING` after the join

[`HAVING`](../sql/dql-notes.md#having--filter-groups) works the same way
after an inner join — it filters the **grouped/aggregated** result, not the
raw joined rows:

```sql
-- only departments with more than one employee
SELECT departments.DepartmentName, COUNT(employees.EmployeeID) AS NumberOfEmployees
FROM employees
INNER JOIN departments ON employees.DepartmentID = departments.DepartmentID
GROUP BY departments.DepartmentName
HAVING COUNT(employees.EmployeeID) > 1;
```

## Left Join & Right Join

Both are **outer joins**: unlike `INNER JOIN`, they keep rows from **one**
side even when there's no match on the other — filling the unmatched side's
columns with `NULL` instead of dropping the row.

- **`LEFT JOIN`** — every row from the **left** (first-listed) table, plus
  matching rows from the right table. No match → `NULL`s for the right
  table's columns.
- **`RIGHT JOIN`** — every row from the **right** (second-listed) table, plus
  matching rows from the left table. No match → `NULL`s for the left table's
  columns.

```sql
SELECT columns FROM table1 LEFT JOIN  table2 ON table1.column = table2.column;
SELECT columns FROM table1 RIGHT JOIN table2 ON table1.column = table2.column;
```

`table1` is always "the left table," `table2` is always "the right table" —
which one is guaranteed complete depends only on `LEFT` vs `RIGHT`, not on
which table happens to be the primary/parent one.

### When they're useful

- **Data completeness / reporting** — when a report must show **every** row
  from one table regardless of whether it has related rows in the other, so
  nothing gets silently dropped by a missing match.
- **`LEFT JOIN`** — list every **customer**, including ones who've placed no
  **orders** (an inner join would drop them).
- **`RIGHT JOIN`** — list every **department**, including ones with no
  **employees** yet (an inner join would drop them).

### `LEFT JOIN` example

```sql
-- every employee, with their department name (or NULL if unassigned)
SELECT employees.EmployeeID, employees.Name, employees.Salary, departments.DepartmentName
FROM employees
LEFT JOIN departments ON employees.DepartmentID = departments.DepartmentID;
```

Every employee row survives. An employee like `Eve`, who isn't assigned to
any department, still appears — with `DepartmentName` shown as `NULL` instead
of being dropped.

### `RIGHT JOIN` example

```sql
-- every department, with its employees (or NULL if it has none)
SELECT employees.EmployeeID, employees.Name, employees.Salary, departments.DepartmentName
FROM employees
RIGHT JOIN departments ON employees.DepartmentID = departments.DepartmentID;
```

Every department row survives, even one like `Sales` with **no** employees —
`EmployeeID`, `Name`, and `Salary` come back `NULL` for that row.

### Finding the unmatched rows — `WHERE ... IS NULL`

Add a `WHERE` on the "always-`NULL`-when-unmatched" side's key column to
isolate **only** the rows that had no match — e.g. employees with no
department:

```sql
SELECT employees.EmployeeID, employees.Name, employees.Salary
FROM employees
LEFT JOIN departments ON employees.DepartmentID = departments.DepartmentID
WHERE departments.DepartmentID IS NULL;
```

Without the `WHERE`, the plain `LEFT JOIN` returns **all** employees
(matched + unmatched) with `NULL` filled in for the unmatched ones; adding
`WHERE departments.DepartmentID IS NULL` narrows that down to **just** the
unmatched employees. The same pattern (swap `LEFT`→`RIGHT` and which side's
key you check) finds departments with no employees.

### Combining with `GROUP BY`

Outer joins combine with `GROUP BY`/aggregates the same way `INNER JOIN`
does (see [above](#with-group-by-and-aggregates)) — the only difference is
that groups for unmatched rows will aggregate over `NULL`s (which `SUM`/
`COUNT`/etc. simply ignore, per [DQL's aggregate
notes](../sql/dql-notes.md#aggregate-functions)).

### `INNER` vs `LEFT` vs `RIGHT`

| | Keeps unmatched rows from | Fills with `NULL` on |
|---|---|---|
| `INNER JOIN` | Neither side | — (unmatched rows dropped entirely) |
| `LEFT JOIN` | **Left** table | Right table's columns |
| `RIGHT JOIN` | **Right** table | Left table's columns |

Pick `INNER` when you only want rows that exist on both sides; pick `LEFT`/
`RIGHT` when one table must be **complete** in the result regardless of
matches.

## Advanced Join Operations

### Combining multiple joins

A single query can chain **more than one** join to pull data spread across
more than two tables — each join just adds another `JOIN ... ON ...` clause,
and different joins in the same query can be **different types**.

```sql
SELECT table1.columns, table2.columns, table3.columns
FROM table1
LEFT JOIN  table2 ON table1.column = table2.column
INNER JOIN table3 ON table2.column = table3.column;
```

Using a 3-table schema (`employees` → `departments`, `employees` →
`locations`), the join **type you pick per pair** changes which rows survive:

**`LEFT` + `INNER`** — every employee (even without a department), but only
if their location is valid:

```sql
SELECT employees.EmployeeID, employees.Name, employees.Salary,
       departments.DepartmentName, locations.LocationName
FROM employees
LEFT JOIN  departments ON employees.DepartmentID = departments.DepartmentID
INNER JOIN locations   ON employees.LocationID   = locations.LocationID;
```

The `LEFT JOIN` on `departments` keeps every employee row (`NULL`
`DepartmentName` if unassigned); the `INNER JOIN` on `locations` then drops
any employee whose `LocationID` doesn't match a real location.

**Multiple `INNER JOIN`s** — only employees with **both** a valid department
**and** a valid location:

```sql
SELECT employees.EmployeeID, employees.Name, employees.Salary,
       departments.DepartmentName, locations.LocationName
FROM employees
INNER JOIN departments ON employees.DepartmentID = departments.DepartmentID
INNER JOIN locations   ON employees.LocationID   = locations.LocationID;
```

Every join in the chain requires a match, so a missing department **or** a
missing location excludes the employee entirely.

**`LEFT` + `RIGHT`** — every employee **and** every location, unmatched
either way filled with `NULL`:

```sql
SELECT employees.EmployeeID, employees.Name, employees.Salary,
       departments.DepartmentName, locations.LocationName
FROM employees
LEFT JOIN  departments ON employees.DepartmentID = departments.DepartmentID
RIGHT JOIN locations   ON employees.LocationID   = locations.LocationID;
```

The `LEFT JOIN` keeps every employee (department `NULL` if unassigned); the
`RIGHT JOIN` then keeps every location too (employee columns `NULL` for a
location with no employees). Both sides can end up with `NULL`s at once.

| Combination | Guarantees complete | `NULL`s appear when |
|---|---|---|
| `LEFT` then `INNER` | Employees (until the `INNER` trims unmatched) | An employee has no department, but still needs a valid location to survive at all |
| `INNER` + `INNER` | Neither — every table must match | Never (unmatched rows are dropped, not nulled) |
| `LEFT` + `RIGHT` | Both employees and locations | An employee has no department; **or** a location has no employees |

Since each join's type changes what the *next* join even sees, **join order
and type both matter** — worth checking the result shape against intent,
especially on large joined tables where an unintended `NULL`-heavy or
row-multiplying result is easy to miss.

### Optimizing join performance — indexing

Since a join has to look up matching rows by the `ON` columns, an
[index](../indexing/README.md) on those columns lets the database find
matches without scanning the whole table:

```sql
CREATE INDEX idx_department_id ON employees (DepartmentID);
CREATE INDEX idx_location_id   ON employees (LocationID);
```

Indexing the columns used in join conditions reduces how much data the
engine has to scan per lookup, which matters most on **large tables** where
an unindexed join would otherwise be slow.

### Query planning — `EXPLAIN`

`EXPLAIN` (prefixed on a query) shows the **execution plan** — the steps the
engine actually takes to run it — so you can see whether your indexes are
being used and where the cost is going:

```sql
EXPLAIN
SELECT employees.EmployeeID, employees.Name, employees.Salary,
       departments.DepartmentName, locations.LocationName
FROM employees
INNER JOIN departments ON employees.DepartmentID = departments.DepartmentID
LEFT JOIN  locations   ON employees.LocationID   = locations.LocationID;
```

Key columns in the output (names vary slightly by database):

| Column | Meaning |
|---|---|
| `id` | Identifies which part of the query a row describes — subqueries each get their own `id` |
| `select_type` | `SIMPLE` (no subquery/union) or `PRIMARY` (the outer query of one that has a subquery) |
| `table` | Which table this row of the plan refers to |
| `type` | How that table gets accessed — e.g. `ALL` (full table scan), `index` (scans an index), `NULL` (no table access needed — result comes from elsewhere) |
| `possible_keys` | Indexes that *could* be used to find matching rows |
| `key_len` | Length of the index key actually used — hints at how much of a composite index is engaged |
| `ref` | What's compared against the chosen key to select rows |
| `rows` | Estimated rows the engine expects to examine — a rough cost/efficiency signal |

A full-table-scan `type` (`ALL`) on a large joined table, with no
`possible_keys`, is the signal to add an index on that join's `ON` column.

## Summary

- `CROSS JOIN` = Cartesian product, no `ON`/relationship needed —
  `rows(table1) × rows(table2)` result rows. Combine with aggregates
  (`COUNT`) to size the product, or `WHERE` to filter it to the pairs that
  matter. Grows fast, so use deliberately on large tables.
- `INNER JOIN` = only rows with a match in **both** tables via `ON`; unmatched
  rows on either side are dropped.
- `INNER JOIN` + `GROUP BY` + aggregates (`COUNT`/`SUM`/`AVG`/`MIN`/`MAX`) is
  the standard shape for cross-table reports (e.g. headcount/payroll per
  department); `GROUP_CONCAT` collapses a group's values into one string;
  `HAVING` filters the grouped result afterward.
- `LEFT JOIN` keeps every row from the left table (`NULL`s for unmatched right
  columns); `RIGHT JOIN` keeps every row from the right table (`NULL`s for
  unmatched left columns) — use either for "must show every row from one
  table" reporting. `WHERE <other-side-key> IS NULL` isolates just the
  unmatched rows.
- Chaining joins across 3+ tables is normal — but each join's **type** changes
  what the next join sees, so mixing `LEFT`/`RIGHT`/`INNER` in one query needs
  care about which side stays complete and where `NULL`s can appear.
- Index the columns used in `ON` conditions to speed up joins on large
  tables; use `EXPLAIN` to see the actual execution plan (access `type`,
  `possible_keys`, estimated `rows`) and confirm indexes are being used.
