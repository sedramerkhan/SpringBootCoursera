# Databases & SQL — Intro Notes

Foundational context for the **Java Database Engineering: Spring Boot MVC & SQL
Mastery** course, before the SQL commands themselves.

## What is a database?

A **database** is an organized collection of data stored so it can be easily
accessed, managed, and updated. Rather than scattering data across files, a
database keeps it structured, queryable, and consistent.

A **DBMS** (Database Management System) is the software that stores and manages
that data and mediates every read/write — e.g. MySQL, PostgreSQL, Oracle,
SQL Server, SQLite. An **RDBMS** is a *relational* DBMS: it organizes data into
**tables** (relations) of rows and columns.

## Kinds of databases

| Type | Model | Examples | Good for |
|---|---|---|---|
| **Relational (SQL)** | Tables with rows/columns, fixed schema, relationships via keys | MySQL, PostgreSQL, Oracle, SQL Server | Structured data, transactions, joins |
| **Document (NoSQL)** | JSON-like documents, flexible schema | MongoDB, CouchDB | Semi-structured, evolving data |
| **Key–value** | Simple key → value pairs | Redis, DynamoDB | Caching, sessions, fast lookups |
| **Column-family** | Rows grouped by column families | Cassandra, HBase | Massive write-heavy, wide data |
| **Graph** | Nodes + edges (relationships as first-class) | Neo4j | Highly connected data (social, recommendations) |

This course focuses on **relational databases** and **SQL**.

### Relational vs. non-relational (at a glance)

- **Relational** — rigid **schema** defined up front, strong consistency (ACID),
  data split across related tables joined by **keys**. Query with **SQL**.
- **Non-relational (NoSQL)** — flexible schema, scales horizontally, trades some
  consistency for speed/availability. Query APIs vary per store.

## What is SQL?

**SQL** = *Structured Query Language* — the standard language for talking to a
relational database: defining structure, and reading/writing data.

SQL statements fall into categories (covered later in the course):

| Category | Stands for | Purpose | Examples |
|---|---|---|---|
| **DDL** | Data **Definition** Language | Define/alter structure | `CREATE`, `ALTER`, `DROP`, `TRUNCATE` |
| **DML** | Data **Manipulation** Language | Change the data | `INSERT`, `UPDATE`, `DELETE` |
| **DQL** | Data **Query** Language | Read the data | `SELECT` |
| **DCL** | Data **Control** Language | Permissions | `GRANT`, `REVOKE` |
| **TCL** | **Transaction** Control Language | Manage transactions | `COMMIT`, `ROLLBACK`, `SAVEPOINT` |

## Core building blocks

- **Table** — a collection of related data, made of **rows** and **columns**.
- **Column** — a named field with a fixed **data type** (`INT`, `VARCHAR`, …).
- **Row** (record) — one entry in the table.
- **Primary key** — a column whose value **uniquely identifies each row** and
  cannot be `NULL`.
- **Schema** — the overall structure: the tables, columns, and constraints; also
  a *named logical container* that groups related objects.

## Creating a database

Before creating tables, you create the database that holds them:

```sql
CREATE DATABASE school;      -- create a new database
SHOW DATABASES;              -- list databases (MySQL)
USE school;                  -- select it as the active database
DROP DATABASE school;        -- permanently delete it (use with caution)
```

Inside a database you then create tables — see
[Data Definition Language (DDL)](ddl-notes.md).

> **Schema vs. database:** loosely used as synonyms, but a *schema* is a named
> namespace inside a database that groups tables/views/procedures. In some
> engines (e.g. Postgres) they're distinct; in MySQL `SCHEMA` is an alias for
> `DATABASE`.
