# Bulk Uploads — Notes

**Bulk upload** is a method for quickly importing large amounts of data into a
database from an external source, using commands/tools optimized for high
volume rather than row-by-row operations.

## Why efficient data handling matters

- Large datasets can slow query processing and waste computational resources
  if handled inefficiently.
- Inefficient data management strains hardware/infrastructure and drives up
  cost — streamlining the process reduces that cost.
- At scale, **data integrity and consistency** become harder to maintain;
  inefficient uploads risk inconsistency and slow the whole pipeline.

## Techniques to streamline bulk operations

| Technique | What it does |
|---|---|
| **Data partitioning** | Divide data into chunks and update them together instead of row-by-row — fewer individual operations, less load on the system. |
| **Index optimization** | Well-tuned indexes make updates and queries process faster, cutting the time needed to handle large datasets. |
| **Bulk operations** | Use the database's built-in bulk insert/update statements to process many records in a single operation instead of one at a time. |
| **Parallel processing** | Split the workload across multiple processes/servers so data is processed simultaneously, reducing total time. |

## SQL Server: `BULK INSERT`

`BULK INSERT` imports data from a specified file straight into a table in one
operation — e.g. loading a CSV of thousands of customer records at once
instead of inserting row by row. Key options:

- **`FIELDTERMINATOR`** — the character separating fields (typically a comma
  for CSV).
- **`ROWTERMINATOR`** — the character separating rows (typically a newline).

## Performance considerations during the load

- **Disable indexes and constraints before loading, re-enable after.**
  Every insert normally has to update each index and check every constraint
  (`FOREIGN KEY`, `CHECK`, `UNIQUE`) — for a bulk load that overhead is paid
  once per row, thousands of times over. Dropping/disabling them first lets
  the load run as a straight write; rebuild the indexes and re-enable (and
  re-validate) the constraints immediately afterward so the table isn't left
  unprotected.
- **Disable triggers** on the target table for the same reason — row-level
  triggers firing per inserted row add the same kind of per-row overhead.
- **Batch the load** rather than committing one giant transaction — smaller
  batches (e.g. a few thousand rows at a time) limit transaction log growth
  and make a failure partway through cheaper to recover from.
- **Use minimal/bulk logging where available** (e.g. SQL Server's
  bulk-logged recovery model, or `TABLOCK` on `BULK INSERT`) to reduce
  transaction log overhead versus fully-logged row-by-row inserts.
- **Validate and handle bad rows** — bulk load tools typically support an
  error/reject file and a max-error threshold (SQL Server: `ERRORFILE`,
  `MAXERRORS`) so a few malformed rows don't abort the entire load.

## Best practices for the bulk load file

- **Format:** CSV, comma-delimited, plain tabular text (rows/columns) — no
  spreadsheet formatting/formulas.
- **Header row:** the first row should name the table's columns.
- **Size limit:** keep the file under **50 MB** to avoid upload performance
  issues.
- **Creating the file:** build it in a spreadsheet app (Excel, Google Sheets)
  or a plain text editor (Notepad), then save/export as `.csv`. Opening the
  saved CSV in a text editor should show comma-separated values — a quick
  visual check that the format is correct before running the upload.

## Summary

- Bulk upload trades row-by-row operations for high-volume, purpose-built
  import commands (e.g. SQL Server's `BULK INSERT`).
- Partitioning, index optimization, native bulk operations, and parallel
  processing all reduce the cost of large-scale updates.
- Temporarily disabling indexes/constraints/triggers, batching the
  transaction, and using minimal logging further cut per-row overhead — just
  remember to re-enable and rebuild them afterward.
- A correctly formatted, size-limited CSV (comma-delimited, header row, plain
  text) is the precondition for a smooth bulk load.
