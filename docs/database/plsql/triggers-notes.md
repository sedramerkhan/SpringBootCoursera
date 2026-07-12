# Triggers — Notes

A **trigger** is a special kind of [stored procedure](subprograms-notes.md) that
runs **automatically** in response to a database **event** — you never call it
directly; the event fires it.

> **⚠️ Dialect mix in this lecture:** the concepts are cross-database, but the
> video's *examples* are **SQL Server (T-SQL)** — `ON DATABASE`, `PRINT`, `FOR
> CREATE_TABLE`, "SQL Server error log". Oracle **PL/SQL** uses different syntax
> (`BEFORE`/`AFTER ... ON table`, `:NEW`/`:OLD`, `RAISE_APPLICATION_ERROR`). Both
> are shown below where it matters.

## Trigger events (what fires them)

| Event type | Fires on |
|---|---|
| **DML trigger** | `INSERT`, `UPDATE`, `DELETE` on a table |
| **DDL trigger** | `CREATE`, `ALTER`, `DROP` |
| **Logon trigger** | A user session being established (logon) |

## Trigger timing (when it fires)

| Timing | Meaning |
|---|---|
| `BEFORE` / `FOR` | Fires *as* the statement runs — used to validate/adjust before the change |
| `AFTER` | Fires *after* the statement completes successfully (can't be used on a view) |
| `INSTEAD OF` | Replaces the `INSERT`/`UPDATE`/`DELETE` — mainly for making views updatable |
| `LOGON` | Fires on a logon event; defined at server level |

## Why use triggers

- **Auditing / logging** — record changes to a table.
- **Enforce business rules** that are too complex for a `CHECK`
  [constraint](../sql/table-constraints-notes.md).
- **Derived values** — auto-populate a column from others.
- **Data integrity / consistency** — guarantee an action happens whenever a
  given event occurs.

## Advantages vs disadvantages

| Advantages | Disadvantages |
|---|---|
| Enforce **data integrity** (conditions met before change) | **Invisible execution** — fire automatically, hard to troubleshoot |
| **Validate** data before insert/update | Add **server overhead** (extra processing) |
| **Logging/audit** trail | **Complexity** — many triggers on one event is hard to reason about |
| **Performance** — less client-side code | **Scoped** — created in the current DB only (can reference objects outside it) |
| **Maintainable** — logic centralized, not scattered in app code | |

## Example — DDL trigger (block schema changes)

*As shown in the video (SQL Server T-SQL):*

```sql
CREATE TRIGGER safety
ON DATABASE
FOR CREATE_TABLE, ALTER_TABLE, DROP_TABLE
AS
    PRINT 'You cannot create, alter, or drop a table in this database.';
    ROLLBACK;
```

Fires at the database level whenever anyone attempts to create/alter/drop a
table, and blocks it.

## Example — DML trigger (reject a negative salary)

*The video's scenario, written in Oracle PL/SQL:*

```plsql
CREATE OR REPLACE TRIGGER check_salary
BEFORE INSERT ON employee
FOR EACH ROW
BEGIN
    IF :NEW.salary < 0 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Salary cannot be negative');
    END IF;
END;
/
```

- `BEFORE INSERT ... FOR EACH ROW` — runs once per inserted row, before it lands.
- `:NEW.salary` — the incoming value for the row being inserted (`:OLD` holds the
  previous value on `UPDATE`/`DELETE`).
- Raising an error aborts the statement and rolls back the change, protecting
  data integrity.

## `INSTEAD OF` triggers — making views updatable

**The problem it solves:** a **view** built from a join of several tables is
normally **not directly updatable** — if you run `INSERT INTO a_view ...`, the
database doesn't know how to split that row across the underlying base tables,
so it rejects it.

An `INSTEAD OF` trigger intercepts the `INSERT`/`UPDATE`/`DELETE` on the view and
runs **your code instead of** the (impossible) operation. In the trigger body you
explicitly say what should happen to the real base tables.

```plsql
-- a view joining two base tables
CREATE VIEW employee_dept AS
SELECT e.emp_id, e.name, d.dept_name
FROM employee e JOIN department d ON e.dept_id = d.dept_id;

-- INSERT on the view isn't possible directly, so redirect it
CREATE OR REPLACE TRIGGER trg_emp_dept_insert
INSTEAD OF INSERT ON employee_dept
FOR EACH ROW
BEGIN
    INSERT INTO employee (emp_id, name)          -- write to the real table(s)
    VALUES (:NEW.emp_id, :NEW.name);
END;
/
```

Now `INSERT INTO employee_dept (...)` runs the trigger body **instead of** trying
(and failing) to insert into the view. The name says it literally: run *this*
**instead of** the requested operation.

- Contrast with `BEFORE`/`AFTER`, which run *around* an operation that still
  happens. `INSTEAD OF` **replaces** it entirely.
- Used almost exclusively on **views**, not tables.

## Logon trigger

Fires when a user session is established — after authentication but before the
session is fully open — useful for auditing logins or enforcing access policies.
(An authentication *failure* prevents the logon trigger from running at all.)

## Summary

- A trigger is an **event-driven** stored procedure that runs automatically on
  **DML**, **DDL**, or **logon** events.
- Timing: `BEFORE`/`FOR`, `AFTER`, `INSTEAD OF`, `LOGON`.
- Great for integrity, validation, auditing, and business rules — at the cost of
  being invisible, adding overhead, and increasing complexity.
- Oracle uses `BEFORE`/`AFTER ... ON table FOR EACH ROW` with `:NEW`/`:OLD`;
  the lecture's `ON DATABASE` / `PRINT` / `FOR` examples are SQL Server syntax.
