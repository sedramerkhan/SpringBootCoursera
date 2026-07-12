# SQL Data Types — Notes

Every column has a **data type** that dictates what it can store. Choosing the
right one keeps data stored efficiently and accurately. See
[Data Definition Language (DDL)](ddl-notes.md) for the
`CREATE TABLE` syntax these types slot into.

## Numeric types

| Type | Stores | Example | Use when |
|---|---|---|---|
| `INT` | Whole numbers | `age INT` | Counting, indexing, IDs — no fractions needed |
| `FLOAT(size, d)` | Approximate floating-point numbers | `height FLOAT` | Measurements, scientific data |
| `DECIMAL(p, s)` | Exact fixed-precision numbers | `price DECIMAL(10, 2)` | **Money** — precision is critical |

- `FLOAT(size, d)` — `size` = total digits, `d` = digits after the decimal point.
- `DECIMAL(10, 2)` — up to **10 digits total**, **2** after the decimal point.
  Prefer `DECIMAL` over `FLOAT` for financial data (no rounding surprises).

```sql
CREATE TABLE example (
    age    INT,
    height FLOAT,
    price  DECIMAL(10, 2)
);
```

### Integer sizes: `SMALLINT` vs `INT`

`INT` isn't the only whole-number type — smaller variants trade range for
storage. Pick the smallest that comfortably fits your values.

| Type | Storage | Signed range (typical) |
|---|---|---|
| `SMALLINT` | 2 bytes | −32,768 to 32,767 |
| `INT` | 4 bytes | ≈ −2.1 billion to 2.1 billion |

Use `SMALLINT` when values are guaranteed small (e.g. age) to save space; `INT`
for general-purpose whole numbers and IDs. (Changing a column between them later
is an [`ALTER TABLE ... MODIFY COLUMN`](ddl-notes.md#alter-table).)

## Text (string) types

| Type | Stores | Example | Use when |
|---|---|---|---|
| `CHAR(n)` | **Fixed**-length string, exactly `n` chars | `state CHAR(2)` | Known fixed length (codes, abbreviations) |
| `VARCHAR(n)` | **Variable**-length string, up to `n` chars | `name VARCHAR(255)` | Length varies (names, emails) |
| `TEXT` | Large blocks of text | `description TEXT` | Long descriptions, documents |

- `CHAR` is efficient when every value is the same length; `VARCHAR` is more
  flexible and the common default. You pick the length limit (e.g. `VARCHAR(50)`).

```sql
CREATE TABLE employee (
    first_name  VARCHAR(255),
    gender      CHAR(6),        -- fixed: 'male' / 'female'
    description TEXT            -- bio / work-profile notes
);
```

> Note: the transcript uses `CHAR` for gender because the values are short and
> constrained — in practice `VARCHAR` or an `ENUM`/lookup is more common.

## Date & time types

| Type | Stores | Format |
|---|---|---|
| `DATE` | A date only | `YYYY-MM-DD` |
| `TIME` | A time only | `HH:MM[:SS]` |
| `DATETIME` | Date **and** time | `YYYY-MM-DD HH:MM:SS` |
| `TIMESTAMP` | Date and time, tied to a point in time | `YYYY-MM-DD HH:MM:SS` |

- `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` auto-captures the **system time** when a
  row is created — handy for "created at" audit columns.

```sql
CREATE TABLE appointment (
    patient_id       INT,
    doctor_id        INT,
    birth_date       DATE,
    appointment_date DATETIME,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Boolean type

Stores `TRUE` / `FALSE` — ideal for yes/no flags.

```sql
CREATE TABLE user (
    user_id   INT PRIMARY KEY,
    user_name VARCHAR(50),
    is_active BOOLEAN        -- TRUE = active, FALSE = inactive
);
```

## Summary

- **Numeric** — `INT` (whole), `FLOAT` (approximate fractions), `DECIMAL`
  (exact, for money).
- **Text** — `CHAR` (fixed length), `VARCHAR` (variable length), `TEXT` (large).
- **Date/Time** — `DATE`, `TIME`, `DATETIME`, `TIMESTAMP` (auto-capture with
  `DEFAULT CURRENT_TIMESTAMP`).
- **Boolean** — `TRUE`/`FALSE` flags.

Picking the right type makes the database more reliable and performant.
