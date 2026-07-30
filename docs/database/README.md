# Database — Reference Notes

The database half of **Java Database Engineering: Spring Boot MVC & SQL Mastery**,
grouped into eight areas. Each has its own indexed reference:

- **[Database Design](database-design/README.md)** — why design drives
  performance/scalability/maintainability, the five-stage lifecycle
  (requirement analysis, conceptual/ERD, logical schema, physical/indexing,
  implementation), core terminology (entity, attribute, relationship,
  primary/foreign key, schema, normalization vs. denormalization), domain &
  data models (UML relationships, conceptual/logical/physical progression),
  the full ERD reference (basic + advanced notation, Extended ER/EER, MySQL
  Workbench), and scalability & performance (sharding/replication/load
  balancing, partitioning, indexing, query optimization).
- **[SQL](sql/README.md)** — the SQL Primer: foundations, DDL, data types,
  constraints, DML, DQL (incl. aggregates/grouping), DCL, TCL, string functions,
  and best practices. Organized by the five command categories.
- **[Advanced SQL](advanced-sql/README.md)** — relational design and query
  techniques beyond the Primer: normalization & denormalization (functional
  dependency, decomposition, 1NF–BCNF, when to denormalize), JOIN types
  (cross, inner, left/right, advanced/multi-join), and window functions
  (`PARTITION BY`, ranking/distribution functions).
- **[PL/SQL](plsql/README.md)** — Oracle's procedural extension: subprograms
  (functions & procedures) and triggers.
- **[Indexing](indexing/README.md)** — DBMS indexing structures: ordered indices
  (dense/sparse, clustering/secondary, multilevel), B-tree, B+ tree, and
  hashing (static/dynamic, bucket overflow, hashing vs ordered indices); plus
  monitoring & maintenance (choosing columns, fragmentation, reorganize vs.
  rebuild, automation).
- **[Backup & Recovery](backup-recovery/README.md)** — protecting data
  against loss: backup vs. replication, the four planning factors, threats
  and BC/DR, backing up relational vs. distributed vs. SaaS systems, backup
  types (full/incremental/differential/synthetic) with real tools per
  system, building a backup plan (RTO/RPO, online/offline, automation),
  actually performing backups (built-in vs. third-party tools, manual vs.
  automated steps, daily/weekly/monthly scheduling with Task Scheduler),
  and restoring (the SSMS procedure, the 3-2-1 rule, and why/how backups
  fail).
- **[Data Loading](data-loading/README.md)** — importing/exporting and
  loading/unloading data between databases and external files; real-world
  uses (migration, backup, analytics feeds); tools and techniques (WinSQL,
  drag-and-drop export, exporting SQL query results); plus bulk uploads
  (partitioning, index optimization, parallel processing, SQL Server
  `BULK INSERT`, CSV file best practices).
- **[Monitoring](monitoring/README.md)** — real-time database health
  tracking: key performance factors (workload, throughput, resources,
  optimization, contention), critical KPIs (response time, throughput, open
  connections, errors, frequent queries), monitor types (resource, network,
  application, third-party), best practices, and an example tool (Nagios XI
  for MS SQL Server); plus optimization techniques (caching layers, query
  optimization, sharding, APM/logging/RUM) with Netflix/Airbnb case studies.

> These eight are siblings so notes cross-link freely (e.g. PL/SQL triggers →
> SQL constraints, `../sql/...`). Plain **SQL** vs **PL/SQL**: SQL is the
> declarative query language; PL/SQL is Oracle's procedural language built on top
> of it.
