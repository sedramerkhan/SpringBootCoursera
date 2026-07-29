# Designing for Scalability & Performance — Notes

**Scalability** is a database's ability to handle increased load — growing
data volume and user traffic — without compromising performance. It matters
most for applications expected to grow over time: designing for it up front
avoids performance bottlenecks and keeps the user experience smooth as load
increases.

## Principles of scalable design

| Principle | What it does | Trade-off |
|---|---|---|
| **Horizontal scaling (sharding)** | Distributes data across multiple servers, each ("shard") holding a subset of the data; add servers to add capacity | Scales further, but adds distributed-system complexity |
| **Vertical scaling** | Adds more resources (CPU, RAM) to a single server | Simple, fine for smaller databases with predictable growth, but hits a hard ceiling |
| **Replication** | Keeps multiple copies of the database | Distributes read load and improves availability/disaster recovery |
| **Load balancing** | Distributes incoming requests across multiple servers | Prevents any single server becoming the bottleneck; removes a single point of failure |

Horizontal and vertical scaling both have valid use cases — the choice
depends on the application's specific growth requirements. Replication and
load balancing then work alongside whichever scaling approach is chosen to
keep the system highly available.

## Techniques for handling growth

- **Partitioning** — divide a large table into smaller, more manageable
  pieces, so a query only has to scan the partition(s) relevant to it
  instead of the whole table (see [partitioning](#partitioning) below for
  the types and a worked example).
- **Data archiving** — move rarely-accessed data to cheaper storage,
  keeping the main (frequently-queried) database lean and fast.
- **Choosing the right storage engine** — pick based on the workload, e.g.
  InnoDB for transactional workloads, MyISAM for read-heavy workloads.
  Different engines make different trade-offs, so this is itself a
  performance decision, not just a default setting.

## Performance optimization

**Performance optimization** enhances a database's speed and efficiency —
reducing query response time and increasing overall throughput. It matters
because it keeps applications responsive, improves user experience, and
reduces server load/resource consumption as traffic grows.

Three core techniques:

- **[Indexing](../indexing/README.md)** — speeds up retrieval by letting the
  database locate rows without a full scan. B-tree indexes suit range
  queries; hash indexes suit exact-match lookups (see the
  [indexing notes](../indexing/README.md) for the underlying structures).
- **Query optimization** — analyze and rewrite SQL so it runs efficiently:
  use `EXPLAIN` to inspect the [execution
  plan](../advanced-sql/join-types-notes.md#query-planning--explain), and
  avoid `SELECT *` so only the columns actually needed are read/transferred.
- **Partitioning** — covered above and below; also a performance technique,
  not just a growth-handling one, since it shrinks the amount of data any
  one query has to scan.

## Practical examples

### Indexing

```sql
CREATE INDEX idx_customerid ON Orders(CustomerID);
```

In a retail application, `Orders` stores every order with a `CustomerID`
column linking it to a customer. Without an index, looking up a customer's
orders means scanning the **entire** table. `idx_customerid` lets the
database jump straight to the matching rows instead, which matters
increasingly as the table grows.

### Query optimization

```sql
SELECT CustomerID, OrderDate
FROM Orders
WHERE OrderID = 123;
```

Naming only the columns actually needed (`CustomerID`, `OrderDate`) instead
of `SELECT *` limits how much data the database has to process and transfer
for the same query. The `WHERE` clause further limits the result to the one
matching row.

### Partitioning

```sql
CREATE TABLE sales (
    SaleID   INT,
    SaleDate DATE,
    Amount   DECIMAL(10, 2)
)
PARTITION BY RANGE (YEAR(SaleDate)) (
    PARTITION p0 VALUES LESS THAN (2020),
    PARTITION p1 VALUES LESS THAN (2021),
    PARTITION p2 VALUES LESS THAN (2022)
);
```

**Range partitioning** splits `sales` by the year of `SaleDate`. Each
boundary is an **exclusive upper bound**, and a partition catches everything
from the previous boundary up to (but not including) its own:

| Partition | Covers |
|---|---|
| `p0` | `YEAR(SaleDate) < 2020` |
| `p1` | `2020 <= YEAR(SaleDate) < 2021` (i.e. exactly 2020) |
| `p2` | `2021 <= YEAR(SaleDate) < 2022` (i.e. exactly 2021) |

A query filtering on a specific year only has to scan the one partition
that could contain it, instead of the whole `sales` table — the same
locality win as an index, applied at the table-storage level. Beyond
**range** partitioning, MySQL also supports **list** partitioning (explicit
sets of values, e.g. by region) and **hash** partitioning (an even spread
across N partitions via a hash function) — pick whichever matches the
table's actual query pattern.

## Summary

- **Scalability** = handling more data/traffic without losing performance.
  Principles: **horizontal scaling** (sharding, add servers) vs. **vertical
  scaling** (add resources to one server), backed by **replication** (copies
  for availability) and **load balancing** (spread requests, avoid a single
  point of failure).
- **Growth-handling techniques**: partitioning, archiving cold data to
  cheaper storage, and picking the storage engine that matches the
  workload.
- **Performance optimization** techniques: indexing (B-tree for ranges, hash
  for exact match), query optimization (`EXPLAIN`, avoid `SELECT *`), and
  partitioning (range/list/hash) — each shrinks the amount of data a query
  actually has to touch.
- Practical patterns: index a foreign key column used in lookups
  (`idx_customerid`), select only needed columns, and range-partition a
  large time-series table by year so queries scan one partition instead of
  the whole table.
