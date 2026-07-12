# Data Control Language (DCL) — Notes

**DCL** is the category that **controls access** to data — who is allowed to do
what to which database objects. Two commands: `GRANT` (give privileges) and
`REVOKE` (take them away). One of the five SQL command categories alongside
[DDL](ddl-notes.md), [DML](dml-notes.md), and [DQL](dql-notes.md).

| Command | Purpose |
|---|---|
| `GRANT` | Give a user specific privileges on an object |
| `REVOKE` | Remove privileges previously granted to a user |

## `GRANT`

```sql
GRANT privilege ON object TO user;

-- let user1 read the student table
GRANT SELECT ON student TO user1;
```

This restricts what a user can do: `user1` can now run `SELECT` on `student`,
but nothing else unless separately granted.

**Multiple privileges** — list them, or use `ALL PRIVILEGES`:

```sql
GRANT SELECT, INSERT, UPDATE ON student TO user2;   -- three privileges
GRANT ALL PRIVILEGES ON student TO admin;           -- everything
```

## `REVOKE`

```sql
REVOKE privilege ON object FROM user;

REVOKE SELECT ON student FROM user1;                 -- user1 loses read access
REVOKE SELECT, INSERT, UPDATE ON student FROM user2; -- loses all three
REVOKE ALL PRIVILEGES ON student FROM admin;         -- loses everything
```

### Why revoke?

- **Least privilege / security** — ensure only authorized users retain access.
- **Role changes** — remove privileges a user no longer needs.
- **Compliance** — enforce security policies.

## Summary

- DCL manages **permissions**, not data or structure: `GRANT` gives, `REVOKE`
  removes.
- Syntax mirror each other — `GRANT ... TO user` vs `REVOKE ... FROM user`.
- Grant/revoke one privilege, a list, or `ALL PRIVILEGES` at once.
- Keeps data secure by controlling who can access it.
