# Transaction Control Language (TCL) — Notes

**TCL** manages **transactions** — groups of changes treated as a single unit
that either all succeed together or all get undone. It decides when the work
done by [DML](dml-notes.md) commands becomes permanent. The last of the five SQL
command categories.

| Command | Purpose |
|---|---|
| `COMMIT` | Permanently save all changes in the current transaction |
| `ROLLBACK` | Undo all changes in the current transaction |
| `SAVEPOINT` | Mark a point you can partially roll back to |

> A **transaction** is a sequence of operations. Until you `COMMIT`, the changes
> are provisional and can be undone with `ROLLBACK` — this is what lets a
> database recover cleanly from errors mid-way through multi-step work.

## `COMMIT`

Makes every change in the current transaction permanent.

```sql
INSERT INTO students (student_id, name, age, major)
VALUES (1, 'John', 20, 'Computer Science');
COMMIT;   -- the new record is now saved for good
```

## `ROLLBACK`

Undoes every change made since the transaction began (or since the last commit)
— used for **error handling / recovery**.

```sql
INSERT INTO students (student_id, name, age, major)
VALUES (1, 'John', 20, 'Computer Science');
ROLLBACK;   -- the insert is undone; the row is not saved

DELETE FROM students WHERE student_id = 2;
ROLLBACK;   -- the deletion is reversed; student 2 remains
```

## `SAVEPOINT` + `ROLLBACK TO`

A savepoint is a named checkpoint **inside** a transaction. `ROLLBACK TO
savepoint` undoes only the work done *after* that point, keeping everything
before it — fine-grained control for complex, multi-step transactions.

```sql
INSERT INTO student (student_id, name, age, major)
VALUES (1, 'John', 20, 'Computer Science');

SAVEPOINT sp1;                              -- checkpoint after the insert

UPDATE student SET age = 21 WHERE student_id = 1;
DELETE FROM student WHERE student_id = 3;

ROLLBACK TO sp1;    -- undo the UPDATE and DELETE; keep the initial INSERT

COMMIT;             -- save what remains (just the insert)
```

## Rolling back on error

The main reason transactions exist: if one step fails partway through, undo the
whole unit so you're never left in a half-finished state (all-or-nothing — the
**A**tomicity in ACID).

**Manual — decide at the end:**

```sql
START TRANSACTION;                       -- or BEGIN
    UPDATE account SET balance = balance - 100 WHERE id = 1;
    UPDATE account SET balance = balance + 100 WHERE id = 2;
-- both worked → COMMIT; something wrong → ROLLBACK (undoes BOTH updates)
COMMIT;
```

**Automatic — roll back the moment an error is raised** (MySQL stored procedure
with an error handler):

```sql
DELIMITER //
CREATE PROCEDURE transfer_funds()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;                        -- any SQL error → undo everything
    END;

    START TRANSACTION;
        UPDATE account SET balance = balance - 100 WHERE id = 1;
        UPDATE account SET balance = balance + 100 WHERE id = 2;
    COMMIT;
END //
DELIMITER ;
```

**Partial — roll back only the bad part** with a savepoint:

```sql
START TRANSACTION;
    INSERT INTO orders (id, customer_id) VALUES (1, 42);
    SAVEPOINT after_order;

    INSERT INTO order_items (order_id, sku) VALUES (1, 'BAD-SKU');
    ROLLBACK TO after_order;             -- undo just the item, keep the order

    INSERT INTO order_items (order_id, sku) VALUES (1, 'GOOD-SKU');
COMMIT;
```

> **In Spring Boot** you rarely write `ROLLBACK` by hand — annotate a method
> `@Transactional` and Spring rolls back automatically when a `RuntimeException`
> propagates out. Note the default: it rolls back on **unchecked** exceptions
> and `Error` only — for a checked exception use
> `@Transactional(rollbackFor = Exception.class)`. *(Covered when the course
> reaches the Spring data layer.)*

> **Not the same as `REVOKE`.** `ROLLBACK` undoes *data* changes on error.
> `REVOKE` ([DCL](dcl-notes.md)) removes a *permission* — and since `GRANT`/`REVOKE`
> usually auto-commit, a bad grant is fixed with an explicit `REVOKE`, not a
> transaction rollback.

## How they work together

- `SAVEPOINT` marks intermediate checkpoints.
- `ROLLBACK TO sp` rewinds to a checkpoint (partial undo); plain `ROLLBACK`
  discards the whole transaction.
- `COMMIT` finalizes whatever survives — after commit, you can no longer roll
  back.

## Summary

- TCL provides **robust transaction management**: `COMMIT` (save), `ROLLBACK`
  (undo all), `SAVEPOINT` / `ROLLBACK TO` (partial undo).
- Changes stay provisional until `COMMIT`; `ROLLBACK` recovers from errors.
- Keeps data **consistent and integral** across multi-step operations.
