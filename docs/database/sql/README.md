# SQL — Reference Notes

Concise SQL reference, organized by topic (not by course video). Start with the
foundations, then dip into whichever command category you need.

## Learning path

1. **Foundations**
   - [Databases & SQL Intro](databases-and-sql-intro-notes.md) — what a database
     / DBMS / RDBMS is, kinds of databases, what SQL is, the five command
     categories, core building blocks, and creating a database.

2. **Defining structure — DDL**
   - [DDL — Create / Alter / Drop Tables](ddl-notes.md) — `CREATE TABLE`,
     `ALTER TABLE` (add/drop/rename/modify columns), `AUTO_INCREMENT`, `DROP`,
     `TRUNCATE`, `DESCRIBE`, schemas, and why `TRUNCATE` counts as DDL.
   - [Data Types](data-types-notes.md) — numeric (incl. `SMALLINT` vs `INT`),
     text, date/time, boolean.
   - [Table Constraints](table-constraints-notes.md) — `NOT NULL`, `UNIQUE`,
     `CHECK`, `PRIMARY KEY`, `FOREIGN KEY` (incl. one-to-many relationships),
     composite constraints.

3. **Manipulating data — DML**
   - [DML](dml-notes.md) — `INSERT` (single/multiple rows, `NULL`, `DEFAULT`s),
     `UPDATE`, `DELETE`, and `DELETE` vs `TRUNCATE` vs `DROP`.

4. **Querying — DQL**
   - [DQL](dql-notes.md) — `SELECT`, `JOIN` (combining related tables),
     `WHERE` (incl. `LIKE` pattern matching), `DISTINCT`, `ORDER BY`
     (multi-column), `LIMIT`/`OFFSET`, aggregate functions
     (`COUNT`/`SUM`/`AVG`/`MIN`/`MAX`), `GROUP BY`, `HAVING` (vs `WHERE`), and
     subqueries (incl. correlated).

5. **Access control — DCL**
   - [DCL](dcl-notes.md) — `GRANT`, `REVOKE`.

6. **Transactions — TCL**
   - [TCL](tcl-notes.md) — `COMMIT`, `ROLLBACK`, `SAVEPOINT`, rolling back on
     error.

7. **Functions**
   - [String Functions](string-functions-notes.md) — `CONCAT`, `SUBSTRING`,
     `UPPER`/`LOWER`, `LENGTH`, `REPLACE`.

8. **Best practices** (module wrap-up)
   - [Best Practices & Pitfalls](best-practices-notes.md) — naming, comments,
     modularizing, transactions; pitfalls (indexes, subqueries vs joins,
     `SELECT *`, hardcoding / SQL injection).

## Command → note quick lookup

| Command / keyword | Category | Note |
|---|---|---|
| `CREATE DATABASE`, `USE` | DDL | [Intro](databases-and-sql-intro-notes.md) |
| `CREATE TABLE`, `DROP TABLE`, `TRUNCATE`, `DESCRIBE`, schemas | DDL | [DDL](ddl-notes.md) |
| `INT`, `VARCHAR`, `DECIMAL`, `DATE`, `BOOLEAN`, … | DDL | [Data Types](data-types-notes.md) |
| `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE`, `CHECK`, `NOT NULL` | DDL | [Table Constraints](table-constraints-notes.md) |
| `JOIN` (combining related tables) | DQL | [DQL](dql-notes.md#join--combining-tables) |
| `ALTER TABLE … ADD/DROP/RENAME/MODIFY` | DDL | [DDL](ddl-notes.md#alter-table) |
| `AUTO_INCREMENT` | DDL | [DDL](ddl-notes.md#auto-increment) |
| `INSERT INTO`, `UPDATE`, `DELETE` | DML | [DML](dml-notes.md) |
| `SELECT`, `WHERE`, `LIKE`, `DISTINCT`, `ORDER BY`, `LIMIT`/`OFFSET`, `GROUP BY`, `HAVING`, aggregates, subqueries | DQL | [DQL](dql-notes.md) |
| `GRANT`, `REVOKE` | DCL | [DCL](dcl-notes.md) |
| `COMMIT`, `ROLLBACK`, `SAVEPOINT` | TCL | [TCL](tcl-notes.md) |
| `CONCAT`, `SUBSTRING`, `UPPER`, `LOWER`, `LENGTH`, `REPLACE` | — | [String Functions](string-functions-notes.md) |

## The five command categories

DDL · DML · DQL · DCL · TCL — the full table with purposes and commands lives in
[Databases & SQL Intro](databases-and-sql-intro-notes.md#what-is-sql).
