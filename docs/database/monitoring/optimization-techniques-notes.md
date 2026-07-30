# Optimizing Database (& API) Performance — Notes

Optimizing database performance combines **caching** (avoid re-doing work),
**query-level techniques** (do the work more efficiently), **sharding**
(spread the work out), and **monitoring** (know where the bottleneck is).

## Caching mechanisms

| Mechanism | What it does |
|---|---|
| **In-memory caching** | Stores frequently accessed data in memory, cutting the time needed to fetch it from the database. |
| **CDN (Content Delivery Network)** | Distributes content across servers worldwide, reducing latency for users everywhere. |
| **HTTP caching** | Saves server responses in a local cache so repeat requests for the same resource are served faster. |
| **Full-page cache** | Stores a rendered page in cache and serves it directly instead of regenerating it dynamically on every request. |

## Query optimization techniques

- **Query plan** — analyze and tune the query **execution plan** so the
  database engine runs the query the most efficient way (see also
  [`EXPLAIN`](../advanced-sql/join-types-notes.md#query-planning--explain)).
- **Query rewrite** — simplify a complex query, or break it into smaller,
  more manageable pieces, to make it cheaper to run.
- **Indexing** — create indexes on frequently queried columns; see
  [below](#indexing) and the full [indexing notes](../indexing/README.md).
- **DB-specific partitioning** — use the optimization features of the
  specific engine in use (e.g. Oracle *partitioning*, SQL Server *table
  partitioning*).
- **Denormalization** — deliberately combine tables to introduce redundancy
  and cut down on joins, trading storage/write cost for faster reads (see
  [normalization notes](../advanced-sql/normalization-notes.md) for the
  trade-off in the other direction).

### Indexing

Indexing is the fundamental optimization technique: it builds a data
structure that lets the database jump straight to matching rows by search
criteria — the same idea as a book's index — instead of scanning the whole
table.

- **Benefit:** queries on indexed columns run much faster, which matters
  most on large tables.
- **Knock-on effect:** faster queries mean less time spent per query, which
  lowers overall load on the database server.
- **Clustered index for sorting:** creating a **clustered index** on the
  column a query sorts by physically orders the table's rows on disk to
  match that column — so `ORDER BY` (or a range scan) reads rows already in
  order instead of needing a separate sort step. See [clustering vs.
  secondary index](../indexing/ordered-indices-notes.md#clustering-vs-secondary-index)
  for the underlying structure (only **one** clustered index per table,
  since data can be physically sorted only one way) and [index
  fragmentation](../indexing/index-maintenance-notes.md#index-fragmentation)
  for the maintenance cost of keeping that physical order intact as rows are
  inserted/updated.

## Data sharding

**Sharding** horizontally partitions data across multiple servers/nodes to
scale a database and improve performance under high data volume:

- The database is split into subsets called **shards**.
- Each shard is stored and processed **independently** on its own database
  instance.
- This lets data across different shards be processed **concurrently**,
  spreading the workload instead of funneling it through one instance.

**Benefits:** scalability and improved performance — the same underlying
horizontal-scaling idea as
[sharding in the scalability notes](../database-design/scalability-performance-notes.md#principles-of-scalable-design).

## Monitoring tools & practices

- **APM (Application Performance Monitoring) tools** — track the
  performance of both the application and the database it talks to.
- **Logging and tracing** — record database activity and transactions to
  help diagnose issues and guide optimization.
- **System resource monitoring** — track CPU, memory, disk I/O, and network
  usage to confirm the database has the resources it needs.
- **Real User Monitoring (RUM)** — measures the performance actually
  experienced by real users, showing how database performance affects the
  end-user experience (versus synthetic/internal metrics alone).

These complement the KPIs and monitor types in the
[performance monitoring notes](performance-monitoring-notes.md) — this list
adds the practice of tracing/logging and the user-experience angle (RUM).

## Case studies

- **Netflix** — serves content to millions of users worldwide by caching at
  multiple levels (CDNs **and** in-memory caching), reducing load on the
  database and keeping streaming responsive.
- **Airbnb** — handles a large, growing dataset by **sharding**, horizontally
  partitioning data across multiple databases to scale and keep response
  times low as users and listings grow.

## Summary

- **Caching** (in-memory, CDN, HTTP, full-page) avoids repeating expensive
  work for repeat requests.
- **Query-level techniques** — tuning the execution plan, rewriting queries,
  indexing, engine-specific partitioning, and denormalization — make each
  query itself cheaper.
- **Sharding** scales out by splitting data across independent instances
  that can be processed concurrently.
- **Monitoring** (APM, logging/tracing, system resources, RUM) is what tells
  you which of the above to apply where.
- Real systems combine these: Netflix layers caching (CDN + in-memory);
  Airbnb shards its data to scale horizontally.
