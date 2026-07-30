# Data Loading — Reference Notes

Transferring data between databases, or between a database and external files
— for migration, backup, and system integration. Part of the
[database notes](../README.md).

## Notes

- [Importing & Exporting Data](import-export-notes.md) — core terms (export,
  import, unload, load); real-world uses (migration, backup, analytics
  feeds); tools and techniques (WinSQL cross-database transfer & `INSERT`
  generation, drag-and-drop export, exporting SQL query results to
  text/Excel/Word).
- [Bulk Uploads](bulk-upload-notes.md) — why efficient data handling matters
  at scale; streamlining techniques (partitioning, index optimization, bulk
  operations, parallel processing); SQL Server's `BULK INSERT` (field/row
  terminators); best practices for the load file (CSV, delimiter, header row,
  50 MB size limit).
