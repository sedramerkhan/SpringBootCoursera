# Data Loading: Importing & Exporting — Notes

**Data loading** (also called data copying) is transferring data between
different databases, or between a database and an external file. It's central
to database migration, backups, and integrating a database with other
systems.

## Core terms

| Term | Meaning |
|---|---|
| **Export** | Copy database data to an external file, typically in a format meant for importing into another database within the same DBMS. |
| **Import** | The reverse of export — copy data from an external file (usually a prior export) into a database. |
| **Unload** | Like export, but the destination is a **text file** — suited for non-database applications like spreadsheets. |
| **Load** | Copy data into a database **from** an external text file (e.g. CSV, or a format supported by tools like Oracle SQL\*Loader). |

## Real-world uses

- Migrating a database between different systems.
- Creating backups for disaster recovery.
- Feeding data from an operational database into an analytical platform.

## Techniques & tools

### General-purpose tools (e.g. WinSQL)

- Move data between relational databases of **different types**, mapping
  columns between the source and target schema.
- Back up data to a local file, or move it between environments.
- Export a table to a text file, or import a text file into a database.
- Generate `INSERT INTO` statements for existing data, which can be run
  against another database to replicate the data.

### Drag-and-drop export

Drag a table from one database in your management tool and drop it into
another to transfer the data directly — the tool handles the underlying
export/import mechanics.

- **Simple** — no SQL required, accessible to users not comfortable writing
  queries.
- **Flexible** — supports multiple file formats, versatile across different
  import needs.

### Exporting SQL query results

Run a query in a database management tool (e.g. WinSQL), then export the
**result set** rather than a whole table — to another database, or to a file
format like text, MS Excel, or MS Word. (Drag-and-drop on a table works
similarly, saving its full contents to text/Excel/Word.)

- **Multi-format** — easy to share with non-technical stakeholders.
- **Customizable** — export only the data actually needed, cutting down on
  unnecessary data handling.

SQL Server also ships dedicated import/export tools that streamline this
process further with more advanced transfer options.

## Summary

- Data loading = export/import (database ↔ database) plus unload/load
  (database ↔ text file).
- Used for migration, backup, and feeding analytical platforms.
- Tools like WinSQL support cross-database moves, file export/import, and
  generating replayable `INSERT` statements.
- Drag-and-drop and query-result export offer simple, format-flexible options
  for non-SQL-fluent users and ad hoc sharing.
