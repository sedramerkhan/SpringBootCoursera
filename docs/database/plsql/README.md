# PL/SQL — Reference Notes

Notes for **Module 2 — PL/SQL**, Oracle's procedural extension to SQL (variables,
control flow, and reusable subprograms in *blocks*). For plain SQL see
[../sql/](../sql/README.md).

> **Dialect:** PL/SQL is **Oracle-specific**. Other databases have their own
> procedural languages (PostgreSQL `PL/pgSQL`, SQL Server `T-SQL`).

## Notes

- [Subprograms — Functions & Procedures](subprograms-notes.md) — the two kinds of
  PL/SQL subprogram in one note: shared scaffolding (local vs stored, header
  `IS`/`AS`, parameter modes `IN`/`OUT`/`IN OUT`), then **functions** (`RETURN`,
  recursion) and **procedures** (`OUT` params, why stored procedures matter).
- [Triggers](triggers-notes.md) — event-driven subprograms that fire
  automatically on **DML** / **DDL** / **logon** events; timing
  (`BEFORE`/`AFTER`/`INSTEAD OF`), `:NEW`/`:OLD`, advantages & disadvantages.
