# Index Monitoring & Maintenance — Notes

Indexes speed up reads, but they aren't "set and forget" — data changes cause
**fragmentation** over time, and maintenance (reorganize/rebuild) is needed to
keep query performance from degrading.

## Choosing the right columns to index

Indexing is like a book's table of contents: index the columns readers
actually look up, not every column.

- **Analyze query patterns first** — index columns frequently used in `WHERE`,
  `JOIN`, and `ORDER BY` clauses.
- **Selectivity matters** — columns with high selectivity (many distinct
  values, e.g. an ID) benefit more from indexing than low-selectivity columns
  (e.g. a boolean flag).
- **Join columns on large tables** are prime candidates — unindexed joins
  between large tables are resource-intensive and slow.
- **Avoid over-indexing** — each index costs storage space and slows down
  every `INSERT`/`UPDATE`/`DELETE`, since the index must be updated alongside
  the data. Index only what queries actually need.

The payoff: proper indexing cuts **disk I/O**, one of the most expensive
operations in computing, by letting the DBMS avoid full table scans.

## Index fragmentation

**Fragmentation** is the physical disorganization of an index's data pages on
disk or in memory. A fresh index stores its pages in order; as data is
inserted, updated, and deleted, pages split and get scattered across disk,
forcing the DBMS to do extra work to locate and retrieve data.

**Cause:** data modification (insert/update/delete). When a page fills up and
new data must be inserted, the DBMS **splits** the page, moving part of its
data to a new page elsewhere on disk — the root of fragmentation.

**Impact of high fragmentation:**

| Effect | Why |
|---|---|
| Slower data retrieval | DBMS must read from multiple scattered pages on disk |
| Higher memory usage | more bookkeeping to track scattered pages |
| Larger database size | fragmented pages waste space |
| Longer backup/recovery time | more scattered data to walk through |

## Reorganize vs. rebuild

| | Reorganize | Rebuild |
|---|---|---|
| Weight | Lightweight | Comprehensive |
| What it does | Adjusts physical order of **leaf-level** pages in place | **Drops** the existing index and creates a new one from scratch |
| Effect on fragmentation | Reduces it | Eliminates it, and recompacts pages per the **fill factor** to reclaim space |
| When to use | Fragmentation below ~40% | Fragmentation **at or above ~40%**, or the index structure is too disorganized for reorg to help |

Rule of thumb: **reorganize** for moderate fragmentation, **rebuild** once
fragmentation is severe.

## Reorganizing an index in SSMS

1. Expand the database → the table → its **Indexes** folder.
2. Right-click the index → **Reorganize**.
3. Confirm the correct index is selected in the dialog.
4. Optionally check **Compact large object column data** to compact pages
   containing LOB data.
5. Click **OK**.

## Automating index maintenance

Manual maintenance doesn't scale — automate it to keep it consistent and take
the load off the DBA:

- Use a maintenance script (e.g. the community **Ola Hallengren** SQL Server
  scripts) or write custom scripts that reorganize/rebuild based on measured
  fragmentation level.
- Schedule automation via **SQL Server Integration Services (SSIS)**, **SQL
  Server Maintenance Plans**, or **SQL Server Agent jobs** — set up one or more
  schedules so maintenance runs at low-traffic times without disrupting other
  operations.
- For large indexes: use **parallel processing** to rebuild multiple indexes
  simultaneously, and establish protocols for safely stopping/restarting the
  job to minimize disruption.
- Review and adjust the schedule periodically based on usage patterns and
  monitoring results.

## Minimizing fragmentation — checklist

- Regularly **monitor** index fragmentation levels.
- Set an appropriate **fill factor** to balance storage efficiency vs.
  performance.
- Perform rebuilds when needed to avoid wasted disk space.
- Review and optimize query plans to catch fragmentation-related regressions.

## Summary

- Index the columns that queries actually filter/join/sort on; watch
  selectivity and avoid over-indexing.
- Fragmentation is caused by inserts/updates/deletes forcing page splits, and
  it degrades I/O, memory use, database size, and backup times.
- **Reorganize** for fragmentation < ~40%; **rebuild** at/above ~40% or when
  the structure is too disorganized.
- Automate reorg/rebuild on a schedule (SSIS, Maintenance Plans, or Agent
  jobs) rather than relying on manual intervention.
