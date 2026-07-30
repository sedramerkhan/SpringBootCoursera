# Database Monitoring — Reference Notes

Real-time tracking of database health and performance metrics, so issues get
caught before they affect users. Part of the [database notes](../README.md).

## Notes

- [Performance Monitoring & Optimization](performance-monitoring-notes.md) —
  key performance factors (workload, throughput, resources, optimization,
  contention); critical KPIs (response time, throughput, open connections,
  errors, most frequent queries); monitor types (resource, network,
  application, third-party); best practices; example tool (Nagios XI for MS
  SQL Server — buffer hit ratio, lock waits, page splits, predictive
  analysis).
- [Optimizing Database & API Performance](optimization-techniques-notes.md) —
  caching mechanisms (in-memory, CDN, HTTP, full-page); query optimization
  (query plan, query rewrite, indexing, DB-specific partitioning,
  denormalization); data sharding; monitoring tools & practices (APM,
  logging/tracing, system resource monitoring, real user monitoring); Netflix
  and Airbnb case studies.

> See also: [index fragmentation monitoring](../indexing/index-maintenance-notes.md)
> and [scalability & performance design](../database-design/scalability-performance-notes.md)
> for related, more specific angles on performance.
