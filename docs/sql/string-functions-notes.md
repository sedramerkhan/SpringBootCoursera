# String Functions in SQL — Notes

String functions manipulate **text data** inside a query — combining, extracting,
or reformatting column values on the fly. They're used in the `SELECT` list of
a [DQL](dql-notes.md) query, often with `AS` to name the computed column (an
**alias**).

| Function | Does |
|---|---|
| `CONCAT(a, b, ...)` | Joins two or more strings into one |
| `SUBSTRING(str, start, length)` | Extracts part of a string |
| `UPPER(str)` | Converts to uppercase |
| `LOWER(str)` | Converts to lowercase |
| `LENGTH(str)` | Returns the number of characters |

## `CONCAT` — combine strings

```sql
SELECT CONCAT(first_name, ' ', last_name) AS full_name
FROM students;
```

Joins `first_name` + a space + `last_name` into one `full_name` column. Useful
for building full names, addresses, or any combined text. You can concatenate
more than two pieces:

```sql
SELECT CONCAT(first_name, ' ', last_name, ' - ', age) AS student_info
FROM students;
```

## `SUBSTRING` — extract part of a string

`SUBSTRING(string, start, length)` — `start` is **1-based** (the first character
is position 1), `length` is how many characters to take.

```sql
SELECT SUBSTRING(first_name, 1, 3) AS first_three   -- first 3 letters
FROM students;

SELECT SUBSTRING(email, 1, 5) AS email_start          -- first 5 chars of email
FROM students;
```

## Nesting functions

Functions can wrap each other — the inner result feeds the outer function:

```sql
-- first initial + last name, e.g. "J Smith"
SELECT CONCAT(SUBSTRING(first_name, 1, 1), ' ', last_name) AS initial_name
FROM students;
```

## Case & length

```sql
SELECT UPPER(first_name)  AS uppercase_name  FROM students;   -- JOHN
SELECT LOWER(first_name)  AS lowercase_name  FROM students;   -- john
SELECT LENGTH(first_name) AS name_length     FROM students;   -- 4
```

## Summary

- `CONCAT` combines strings; `SUBSTRING` extracts a piece (1-based `start`,
  then `length`).
- `UPPER` / `LOWER` change case; `LENGTH` counts characters.
- Use them in the `SELECT` list with `AS` to label the result; nest them to
  build more complex output.

> **Dialect note:** exact names vary by database — e.g. Oracle uses `SUBSTR`,
> SQL Server uses `LEN` instead of `LENGTH` and `+`/`CONCAT` for joining. The
> concepts are the same; check your engine's function names.
