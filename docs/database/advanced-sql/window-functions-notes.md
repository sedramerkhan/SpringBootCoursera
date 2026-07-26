# Window Functions — Notes

A **window function** performs a calculation across a set of rows related to
the current row (its "window") — **without collapsing the result set**. Unlike
a plain [aggregate](../sql/dql-notes.md#aggregate-functions) with `GROUP BY`
(one row per group), a window function keeps every individual row while still
attaching a per-row calculation like a rank, running total, or moving
average.

> **Transcript note:** two of the source examples below (`RANK()`, `NTILE()`)
> narrate the result as computed "over the entire dataset, without
> considering department," but the SQL shown in the same breath includes
> `PARTITION BY DepartmentID`. That's a narration slip, not a SQL error — the
> queries as written **do** reset per department. The notes below follow the
> SQL, not the mismatched narration.

## Why window functions

- **Enhanced analysis** — ranking, running totals, moving averages: the kind
  of in-depth analysis plain aggregates can't express in one query.
- **Non-aggregating** — the result keeps every source row; you see the
  per-row detail *and* the computed value side by side, instead of losing
  detail the way `GROUP BY` does.
- **Versatile** — same toolkit works for ranking, reporting, and data
  transformation, especially once combined with partitioning.

## Common use cases

| Use case | Example |
|---|---|
| Ranking | Leaderboards, performance ranking |
| Partitioned metrics | A calculation scoped to each department/region independently |
| Running totals | A running account balance |
| Moving averages | Trend analysis, e.g. a rolling average of sales over time |

## `PARTITION BY` — resetting the calculation per group

`PARTITION BY` splits the result set into independent groups; the window
function is applied **separately within each partition**, resetting as if
each partition were its own mini result set — similar in spirit to
`GROUP BY`, but without collapsing rows into one per group.

```sql
SELECT ...
FROM table_name
some_window_function() OVER (PARTITION BY grouping_column ORDER BY sort_column);
```

Every window function below is a plain function call followed by
`OVER (...)`, where `PARTITION BY` (optional) scopes it and `ORDER BY`
(needed by most of them) determines row order within that scope.

### Sample data used in the examples below

Every result table below is computed from this same `employees` data, so you
can compare how each function treats the same rows and the `DepartmentID
10` tie between Bob and Carol.

| EmployeeID | Name  | Salary | DepartmentID |
|---|---|---|---|
| 1 | Alice | 90000 | 10 |
| 2 | Bob   | 80000 | 10 |
| 3 | Carol | 80000 | 10 |
| 4 | Dave  | 70000 | 10 |
| 5 | Eve   | 95000 | 20 |
| 6 | Frank | 60000 | 20 |

## `ROW_NUMBER()` — a unique sequential number per partition

Assigns `1, 2, 3, ...` to rows within each partition, in the order given by
`ORDER BY` — no ties, always strictly sequential.

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       ROW_NUMBER() OVER (PARTITION BY DepartmentID ORDER BY Salary DESC) AS RowNum
FROM employees;
```

Numbering **restarts at 1** for each department; ordering by `Salary DESC`
means the highest earner in each department gets `RowNum = 1`. Useful for
"find the top earner per department" or "the latest transaction per
customer" style queries.

| EmployeeID | Name  | Salary | DepartmentID | RowNum |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 1 |
| 2 | Bob   | 80000 | 10 | 2 |
| 3 | Carol | 80000 | 10 | 3 |
| 4 | Dave  | 70000 | 10 | 4 |
| 5 | Eve   | 95000 | 20 | 1 |
| 6 | Frank | 60000 | 20 | 2 |

Note Bob and Carol are tied on salary but still get distinct numbers `2`
and `3` — `ROW_NUMBER()` never ties, unlike `RANK()` below.

## `RANK()` — rank with gaps on ties

Assigns a rank per the `ORDER BY`, but rows with an **equal** value get the
**same** rank, and the rank **after** a tie skips ahead by the number of tied
rows (e.g. two rows tied for rank 2 → the next rank is 4, not 3).

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       RANK() OVER (PARTITION BY DepartmentID ORDER BY Salary DESC) AS SalaryRank
FROM employees;
```

Resets per department (per the `PARTITION BY`); within each department the
highest salary gets rank `1`.

| EmployeeID | Name  | Salary | DepartmentID | SalaryRank |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 1 |
| 2 | Bob   | 80000 | 10 | 2 |
| 3 | Carol | 80000 | 10 | 2 |
| 4 | Dave  | 70000 | 10 | 4 |
| 5 | Eve   | 95000 | 20 | 1 |
| 6 | Frank | 60000 | 20 | 2 |

Bob and Carol tie at rank `2`; the next row, Dave, jumps straight to rank
`4` — rank `3` is skipped because two rows occupied rank `2`.

## `NTILE(n)` — divide into n groups

Distributes the rows of each partition into `n` roughly equal-sized,
numbered groups (`1` through `n`), based on the `ORDER BY`.

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       NTILE(2) OVER (PARTITION BY DepartmentID ORDER BY Salary DESC) AS SalaryTile
FROM employees;
```

Within **each department**, this splits employees into 2 groups by salary —
group `1` gets the higher earners in that department, group `2` the lower
earners (since ordering is `DESC`).

| EmployeeID | Name  | Salary | DepartmentID | SalaryTile |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 1 |
| 2 | Bob   | 80000 | 10 | 1 |
| 3 | Carol | 80000 | 10 | 2 |
| 4 | Dave  | 70000 | 10 | 2 |
| 5 | Eve   | 95000 | 20 | 1 |
| 6 | Frank | 60000 | 20 | 2 |

Department 10 has 4 rows, so `NTILE(2)` splits it evenly: the top 2 earners
(Alice, Bob) land in tile `1`, the bottom 2 (Carol, Dave) in tile `2` — the
split falls between Bob and Carol even though they're tied on salary,
since `NTILE` only cares about row order, not equal values.

## `FIRST_VALUE()` / `NTH_VALUE()` — pull a value from elsewhere in the window

`FIRST_VALUE(column)` returns the **first** value in the ordered partition,
attached to every row in that partition:

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       FIRST_VALUE(Salary) OVER (PARTITION BY DepartmentID ORDER BY Salary DESC) AS TopSalaryInDept
FROM employees;
```

Every row in a department shows that department's **highest** salary
alongside its own. `NTH_VALUE(column, n)` works the same way but returns the
`n`th value in the ordered partition instead of always the first.

| EmployeeID | Name  | Salary | DepartmentID | TopSalaryInDept |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 90000 |
| 2 | Bob   | 80000 | 10 | 90000 |
| 3 | Carol | 80000 | 10 | 90000 |
| 4 | Dave  | 70000 | 10 | 90000 |
| 5 | Eve   | 95000 | 20 | 95000 |
| 6 | Frank | 60000 | 20 | 95000 |

## `CUME_DIST()` — cumulative distribution

Returns the proportion of rows in the partition whose value is **≤** the
current row's value (a number between 0 and 1) — how far up the distribution
this row sits.

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       CUME_DIST() OVER (PARTITION BY DepartmentID ORDER BY Salary DESC) AS SalaryCumeDist
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | SalaryCumeDist |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 0.25 |
| 2 | Bob   | 80000 | 10 | 0.75 |
| 3 | Carol | 80000 | 10 | 0.75 |
| 4 | Dave  | 70000 | 10 | 1.0 |
| 5 | Eve   | 95000 | 20 | 0.5 |
| 6 | Frank | 60000 | 20 | 1.0 |

Bob and Carol tie, so they share the same distribution value `0.75`
(3 of department 10's 4 rows have a salary at or above 80000); the last
row in each partition always reaches `1.0`.

## `PERCENT_RANK()` — relative rank as a percentage

Returns each row's rank as a **percentage** of the partition (0 for the
first row in the ordering, 1 for the last), so ranks are comparable across
partitions of different sizes. Like any window function, it can run **with
or without** `PARTITION BY` — omitting it (as below) scores every row against
the whole result set:

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       PERCENT_RANK() OVER (ORDER BY Salary DESC) AS SalaryPercentRank
FROM employees;
```

No `PARTITION BY` here, so this ranks every employee against the **entire**
table — the top salary overall gets `0.0`, the lowest gets `1.0`.

| EmployeeID | Name  | Salary | DepartmentID | SalaryPercentRank |
|---|---|---|---|---|
| 5 | Eve   | 95000 | 20 | 0.0 |
| 1 | Alice | 90000 | 10 | 0.2 |
| 2 | Bob   | 80000 | 10 | 0.4 |
| 3 | Carol | 80000 | 10 | 0.4 |
| 4 | Dave  | 70000 | 10 | 0.8 |
| 6 | Frank | 60000 | 20 | 1.0 |

Rows are now compared across **both** departments at once — Eve (dept 20)
outranks everyone since she has the single highest salary, and the
`PARTITION BY` from earlier examples plays no part here.

## Aggregate functions with `PARTITION BY`

`PARTITION BY` isn't limited to the ranking-style functions above — the
familiar aggregates (`SUM()`, `AVG()`, `COUNT()`, `MIN()`, `MAX()`) can run
as window functions too. Combined with `PARTITION BY`, each aggregate is
computed **per partition** instead of across the whole table, but — same
as every window function — without collapsing rows the way a `GROUP BY`
aggregate would. `MIN()`/`MAX()` require a numeric column, same as
elsewhere.

### `SUM()` — partitioned total

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       SUM(Salary) OVER (PARTITION BY DepartmentID) AS DeptTotalSalary
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | DeptTotalSalary |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 320000 |
| 2 | Bob   | 80000 | 10 | 320000 |
| 3 | Carol | 80000 | 10 | 320000 |
| 4 | Dave  | 70000 | 10 | 320000 |
| 5 | Eve   | 95000 | 20 | 155000 |
| 6 | Frank | 60000 | 20 | 155000 |

Every row in department 10 shows that department's total salary
(320000 = 90000+80000+80000+70000); department 20's rows show its own
total (155000) instead.

### `AVG()` — partitioned average

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       AVG(Salary) OVER (PARTITION BY DepartmentID) AS DeptAvgSalary
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | DeptAvgSalary |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 80000 |
| 2 | Bob   | 80000 | 10 | 80000 |
| 3 | Carol | 80000 | 10 | 80000 |
| 4 | Dave  | 70000 | 10 | 80000 |
| 5 | Eve   | 95000 | 20 | 77500 |
| 6 | Frank | 60000 | 20 | 77500 |

### `COUNT()` — partitioned row count

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       COUNT(*) OVER (PARTITION BY DepartmentID) AS DeptHeadcount
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | DeptHeadcount |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 4 |
| 2 | Bob   | 80000 | 10 | 4 |
| 3 | Carol | 80000 | 10 | 4 |
| 4 | Dave  | 70000 | 10 | 4 |
| 5 | Eve   | 95000 | 20 | 2 |
| 6 | Frank | 60000 | 20 | 2 |

### `MIN()` / `MAX()` — partitioned extremes

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       MIN(Salary) OVER (PARTITION BY DepartmentID) AS DeptMinSalary,
       MAX(Salary) OVER (PARTITION BY DepartmentID) AS DeptMaxSalary
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | DeptMinSalary | DeptMaxSalary |
|---|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 70000 | 90000 |
| 2 | Bob   | 80000 | 10 | 70000 | 90000 |
| 3 | Carol | 80000 | 10 | 70000 | 90000 |
| 4 | Dave  | 70000 | 10 | 70000 | 90000 |
| 5 | Eve   | 95000 | 20 | 60000 | 95000 |
| 6 | Frank | 60000 | 20 | 60000 | 95000 |

## Window frames — `RANGE BETWEEN` and `ROWS BETWEEN`

Both clauses narrow a partition down to a **frame** — the subset of rows
the window function actually looks at for the current row — but they
define that subset differently:

- **`RANGE BETWEEN`** — bounds the frame by **value**. Rows that tie on
  the `ORDER BY` column are peers and share the same frame (and so the
  same result), regardless of how many physical rows that covers.
- **`ROWS BETWEEN`** — bounds the frame by **physical row position**,
  ignoring ties entirely.

### `RANGE BETWEEN` — running total that groups ties together

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       SUM(Salary) OVER (
         PARTITION BY DepartmentID
         ORDER BY Salary DESC
         RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
       ) AS RunningTotal
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | RunningTotal |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 90000 |
| 2 | Bob   | 80000 | 10 | 250000 |
| 3 | Carol | 80000 | 10 | 250000 |
| 4 | Dave  | 70000 | 10 | 320000 |
| 5 | Eve   | 95000 | 20 | 95000 |
| 6 | Frank | 60000 | 20 | 155000 |

Bob and Carol are peers (both 80000), so `RANGE` treats them as a single
step in the frame — **both** get the running total through the whole tied
group (90000+80000+80000 = 250000), rather than Bob getting a partial sum
partway through the tie.

### `ROWS BETWEEN` — moving average over a fixed row count

```sql
SELECT EmployeeID, Name, Salary, DepartmentID,
       AVG(Salary) OVER (
         PARTITION BY DepartmentID
         ORDER BY Salary DESC
         ROWS BETWEEN 1 PRECEDING AND CURRENT ROW
       ) AS MovingAvg
FROM employees;
```

| EmployeeID | Name  | Salary | DepartmentID | MovingAvg |
|---|---|---|---|---|
| 1 | Alice | 90000 | 10 | 90000 |
| 2 | Bob   | 80000 | 10 | 85000 |
| 3 | Carol | 80000 | 10 | 80000 |
| 4 | Dave  | 70000 | 10 | 75000 |
| 5 | Eve   | 95000 | 20 | 95000 |
| 6 | Frank | 60000 | 20 | 77500 |

Unlike `RANGE`, `ROWS` doesn't care that Bob and Carol tie — each row's
average is just itself and the **one physical row before it**: Alice has
no preceding row (average = itself); Bob averages with Alice (85000);
Carol averages with Bob (80000), even though Carol and Bob are tied.

## Practical applications

- **Ranking employees by salary** — `ROW_NUMBER()`/`RANK()` partitioned by
  department surfaces each department's top earner and salary distribution.
- **Sales performance** — a running/cumulative total of sales per
  representative shows performance trends over time.
- **Moving averages** — smoothing stock prices (or any time series) over a
  rolling window makes trends easier to read for investment decisions.
- **Data segmentation** — `NTILE()` splits customers into quantiles (e.g. by
  spend) for targeted marketing to each segment.
- **Departmental reporting** — `SUM()`/`AVG()`/`COUNT()`/`MIN()`/`MAX()`
  partitioned by department attach each department's total, average,
  headcount, or salary range to every one of its rows, alongside the
  per-employee detail.

## Summary

- A window function computes **per row** across a related set of rows
  without collapsing them — unlike `GROUP BY` aggregates.
- `PARTITION BY` scopes the calculation to reset per group; `ORDER BY` (inside
  the same `OVER (...)`) controls the ordering the function uses.
- `ROW_NUMBER()` — unique sequential number, no ties. `RANK()` — same rank on
  ties, then skips ahead. `NTILE(n)` — splits into `n` groups.
- `FIRST_VALUE()`/`NTH_VALUE()` pull a specific ordered value into every row
  of the partition; `CUME_DIST()` and `PERCENT_RANK()` express a row's
  position as a fraction of its partition (or the whole set, if
  `PARTITION BY` is omitted).
- `SUM()`, `AVG()`, `COUNT()`, `MIN()`, `MAX()` can all run as window
  functions too — `PARTITION BY` scopes them per group instead of
  collapsing rows the way plain `GROUP BY` aggregation would.
- `RANGE BETWEEN` bounds a frame by **value** (tied rows share a frame and a
  result); `ROWS BETWEEN` bounds it by **physical row position** and ignores
  ties.
