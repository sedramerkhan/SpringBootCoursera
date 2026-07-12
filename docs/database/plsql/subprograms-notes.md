# PL/SQL Subprograms — Functions & Procedures — Notes

A **subprogram** is a named, reusable PL/SQL block that encapsulates logic so
it's modular and maintainable. PL/SQL has two kinds:

- **Function** — computes and **returns a value** (mandatory `RETURN` type).
- **Procedure** — performs an **action**; returns data (if any) through `OUT`
  parameters, not a `RETURN` value.

> **Dialect note:** this is **Oracle** PL/SQL. Other engines have equivalents
> (PostgreSQL `PL/pgSQL`, SQL Server `T-SQL`) with different syntax.

## Function vs procedure

| | Function | Procedure |
|---|---|---|
| Purpose | Compute and **return a value** | Perform an **action** |
| `RETURN` *type* in header | **Yes** (mandatory) | No |
| Returns data via | the `RETURN` statement | `OUT` / `IN OUT` parameters |
| Usable inside a SQL expression | Yes | No |

Everything below the next two sections (local vs stored, header shape, parameter
modes) applies to **both**; the Functions and Procedures sections then cover
what's unique to each.

## Local vs stored

Both functions and procedures come in two forms:

- **Local** — declared in the **declarative section** (`DECLARE`) of a PL/SQL
  block; callable anywhere in that block's execution section.
- **Stored** — created with `CREATE OR REPLACE …`, saved **permanently in the
  Oracle database**, and reusable across many blocks and applications.
  `OR REPLACE` recreates it in place without a separate `DROP`.

## Header anatomy

The lines before `BEGIN` are the **header**. For a subprogram named `add_nums`:

```plsql
CREATE OR REPLACE FUNCTION add_nums(a IN NUMBER, b IN NUMBER)  -- name + parameters
RETURN NUMBER      -- (functions only) declares the TYPE returned
IS                 -- separates header from body; locals go between IS and BEGIN
    result NUMBER; -- optional local declarations
BEGIN
    result := a + b;
    RETURN result; -- (functions) the STATEMENT that hands a value back
END;
```

- **`RETURN NUMBER` in the header** (functions only) names the **type** returned
  — no value, just the type. It's what makes something a *function*.
- **`RETURN result;` in the body** is the *statement* that computes the value and
  exits. Its type must match the header.
- **`IS`** separates the header from the body; local declarations go **between
  `IS` and `BEGIN`** (empty if there are none).
- **`IS` and `AS` are interchangeable.** By convention `AS` is common for
  standalone/stored subprograms; both are valid.

## Parameter modes

Each parameter declares a **mode**:

| Mode | Direction | Notes |
|---|---|---|
| `IN` | Caller → subprogram | **Read-only** inside; the default |
| `OUT` | Subprogram → caller | Write a result back; the argument must be a variable |
| `IN OUT` | Both | Pass an initial value in, return an updated value out |

Functions typically take `IN` params and give their answer through `RETURN`;
procedures often use `OUT` / `IN OUT` to return results.

---

## Functions

A function **must return a value**. See the header anatomy above for `RETURN` /
`IS`.

### Local function

```plsql
DECLARE
    a NUMBER := 10;
    b NUMBER := 20;
    c NUMBER;

    FUNCTION add_nums(x IN NUMBER, y IN NUMBER) RETURN NUMBER IS
    BEGIN
        RETURN x + y;
    END;
BEGIN
    c := add_nums(a, b);
    DBMS_OUTPUT.PUT_LINE('Sum = ' || c);   -- enable with SET SERVEROUTPUT ON
END;
/
```

### Stored function

```plsql
CREATE OR REPLACE FUNCTION add_nums(a IN NUMBER, b IN NUMBER)
RETURN NUMBER
IS
BEGIN
    RETURN a + b;
END;
/
```

Then any block just **calls** it: `c := add_nums(10, 20);`.

### Recursive function

A function that **calls itself** — for problems that break into smaller versions
of themselves. **Must have a base case** or it loops forever.

```plsql
CREATE OR REPLACE FUNCTION fact(x IN NUMBER)
RETURN NUMBER
IS
BEGIN
    IF x = 0 THEN
        RETURN 1;               -- base case: stops the recursion
    ELSE
        RETURN x * fact(x - 1); -- recursive step
    END IF;
END;
/
-- fact(6) → 720
```

---

## Procedures

A procedure performs an action and has **no `RETURN` value** — it hands results
back through `OUT` parameters.

### Local procedure

```plsql
DECLARE
    a     NUMBER := 10;
    b     NUMBER := 20;
    total NUMBER;

    PROCEDURE add_nums(x IN NUMBER, y IN NUMBER, result OUT NUMBER) IS
    BEGIN
        result := x + y;        -- "returns" via the OUT parameter
    END;
BEGIN
    add_nums(a, b, total);
    DBMS_OUTPUT.PUT_LINE('Sum = ' || total);
END;
/
```

### Stored procedure

```plsql
CREATE OR REPLACE PROCEDURE add_nums(a IN NUMBER, b IN NUMBER, result OUT NUMBER)
AS
BEGIN
    result := a + b;
END;
/
```

### Why stored procedures matter

The lecture's seven points condense into these benefits:

| Benefit | Why |
|---|---|
| **Efficient** | Compiled and stored in the DB → fast to run repeatedly |
| **Reusable** | Written once, called from many blocks / apps |
| **Secure** | Grant access to the procedure without exposing the tables |
| **Less network traffic** | One call runs many statements *on the server* |
| **Better error handling** | A dedicated `EXCEPTION` section makes failures robust |
| **Modular / maintainable** | Repeated logic lives in one central place |
| **Transactional** | Can wrap data changes in a [transaction](../sql/tcl-notes.md) |

## Summary

- **Function** returns a value (`RETURN` type, mandatory); **procedure** performs
  an action and returns via `OUT` params.
- Both have **local** (in a block's `DECLARE`) and **stored**
  (`CREATE OR REPLACE …`, saved in the DB) forms.
- Header: `IS`/`AS` separates header from body; locals go between `IS` and
  `BEGIN`; functions add a `RETURN` type.
- Parameter modes: `IN` (read-only, default), `OUT` (returns), `IN OUT` (both).
- Functions can also be **recursive** (need a base case); stored subprograms are
  efficient, reusable, secure, modular, and reduce network traffic.
