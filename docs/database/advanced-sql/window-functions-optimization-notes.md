# Window Functions — Combining & Optimizing (Notes)

Builds on [Window Functions](window-functions-notes.md): once you're
comfortable with individual window functions, this covers using **several
in one query** and the techniques for keeping those queries fast as data
grows.

Examples below reuse the same `employees` sample data as the main window
functions notes:

| EmployeeID | Name  | Salary | DepartmentID |
|---|---|---|---|
| 1 | Alice | 90000 | 10 |
| 2 | Bob   | 80000 | 10 |
| 3 | Carol | 80000 | 10 |
| 4 | Dave  | 70000 | 10 |
| 5 | Eve   | 95000 | 20 |
| 6 | Frank | 60000 | 20 |

## Combining multiple window functions in one query

Each column in a `SELECT` list can carry its **own** window function with
its **own** `OVER (...)` — they don't need to share the same `PARTITION BY`
or `ORDER BY`, and the database computes all of them in a single pass over
the data rather than requiring one query per metric.

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       SUM(Salary) OVER (PARTITION BY DepartmentID) AS DeptTotalSalary,
       AVG(Salary) OVER (PARTITION BY DepartmentID) AS DeptAvgSalary,
       ROW_NUMBER() OVER (PARTITION BY DepartmentID ORDER BY Salary DESC) AS SalaryRowNum
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | DeptTotalSalary | DeptAvgSalary | SalaryRowNum |
|---|---|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 320000 | 80000 | 1 |
| 2 | Bob   | 80000 | 10 | 320000 | 80000 | 2 |
| 3 | Carol | 80000 | 10 | 320000 | 80000 | 3 |
| 4 | Dave  | 70000 | 10 | 320000 | 80000 | 4 |
| 5 | Eve   | 95000 | 20 | 155000 | 77500 | 1 |
| 6 | Frank | 60000 | 20 | 155000 | 77500 | 2 |

One query, one pass over `employees`, and each row now carries its
department's total, its department's average, **and** its own
salary-ranked position within the department.

## Optimizing window function performance

### Filter rows before windowing

A plain `WHERE` clause is evaluated **before** window functions run
(window functions sit late in SQL's logical processing order, after
`WHERE`/`GROUP BY`/`HAVING`), so filtering early genuinely shrinks the set
of rows every window function in the query has to process.

```sql
WITH FilteredEmployees AS (
  SELECT EmployeeID, Name, Salary, DepartmentID
  FROM employees
  WHERE Salary > 65000
)
SELECT EmployeeID, Name, Salary, DepartmentID,
       SUM(Salary) OVER (PARTITION BY DepartmentID) AS DeptTotalSalary,
       ROW_NUMBER() OVER (PARTITION BY DepartmentID ORDER BY Salary DESC) AS SalaryRowNum
FROM FilteredEmployees;
```

| EmployeeID | Name  | Salary | DepartmentID | DeptTotalSalary | SalaryRowNum |
|---|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 320000 | 1 |
| 2 | Bob   | 80000 | 10 | 320000 | 2 |
| 3 | Carol | 80000 | 10 | 320000 | 3 |
| 4 | Dave  | 70000 | 10 | 320000 | 4 |
| 5 | Eve   | 95000 | 20 | 95000  | 1 |

Frank (60000) is gone entirely — not just hidden from the output. That's
an important distinction: the `WHERE` filters the **rows the partition is
built from**, so department 20's total drops from 155000 to 95000 because
Frank never enters the partition, and department 20's `SalaryRowNum`
column no longer has a row `2` at all. Filtering rows *after* a window
function has run (e.g. keeping only `SalaryRowNum = 1`) is a different
operation with a different result — and it can't be done in the same
`SELECT`'s `WHERE` clause anyway, since a `WHERE` clause can't reference a
window function's output column; that requires wrapping the query in a
CTE or subquery and filtering the outer query instead.

### Other techniques

- **Partition efficiently** — pick a `PARTITION BY` column with a
  reasonable number of distinct values for the data size. Too many tiny
  partitions adds overhead similar to over-granular `GROUP BY`.
- **Minimize sorting** — `ORDER BY` inside `OVER (...)` costs a sort per
  partition. Reuse the same `PARTITION BY`/`ORDER BY` shape across the
  window functions in a query where the logic allows it, so the database
  can share one sort instead of repeating it per function.
- **Avoid nested subqueries** — prefer a CTE (`WITH ... AS (...)`) over
  deeply nested subqueries mainly for **readability**: whether it also
  changes performance depends on the database — some engines (e.g.
  PostgreSQL 12+) can inline a CTE like a subquery, while others may
  materialize it as a separate step. Don't assume switching subquery
  syntax to CTE syntax alone speeds up a query — the real performance win
  in the example above comes from the `WHERE Salary > 65000` filter, not
  from using `WITH`.

### Indexes on `PARTITION BY` / `ORDER BY` columns

An index covering the columns used in `PARTITION BY` and `ORDER BY` lets
the database locate and order the relevant rows faster instead of
scanning and sorting from scratch.

```sql
CREATE INDEX idx_employees_dept_salary ON employees (DepartmentID, Salary);
```

This benefits every example above, since all of them partition by
`DepartmentID` and most order by `Salary`.

### Reading the query plan with `EXPLAIN`

`EXPLAIN <query>` shows how the database intends to execute a query,
which helps spot bottlenecks before they hurt at scale:

```sql
EXPLAIN
SELECT EmployeeID, Name, Salary, DepartmentID,
       SUM(Salary) OVER (PARTITION BY DepartmentID) AS DeptTotalSalary
FROM employees;
```

Key columns to check in the output (naming follows MySQL's `EXPLAIN`;
other engines expose the same ideas under different labels):

| Column | What it tells you |
|---|---|
| `select_type` | Whether this is a simple query, subquery, or part of a union |
| `table` | Which table this plan step reads |
| `type` | Access method — e.g. `ALL` (full table scan) vs. an index-based lookup |
| `key` | Which index (if any) was actually used |
| `rows` | Estimated rows the database expects to examine |
| `Extra` | Extra notes — e.g. `Using filesort`, `Using temporary` |

A `type` of `ALL` (full table scan) or an `Extra` value like
`Using filesort` on a large table are the two most common early signs
that an index on the `PARTITION BY`/`ORDER BY` columns is missing.

## Summary

- Multiple window functions can live in one `SELECT`, each with its own
  `OVER (...)`, computed in a single pass over the data.
- `WHERE` runs before window functions, so filtering early with `WHERE`
  (or a CTE built on one) shrinks what every window function in the query
  processes — and changes partition membership, not just what's displayed.
- Choose selective partition columns, reuse a consistent
  `PARTITION BY`/`ORDER BY` shape to share sorts, and prefer CTEs over
  nested subqueries mainly for readability (performance depends on the
  engine).
- Index the `PARTITION BY`/`ORDER BY` columns, and use `EXPLAIN` to check
  for full table scans (`type = ALL`) or `Using filesort` before assuming
  a query is optimized.
