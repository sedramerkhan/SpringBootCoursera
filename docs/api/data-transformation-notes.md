# Data Transformation Strategies — Notes

Reshaping data so different systems can exchange it smoothly.

## Why it's needed

- **Diverse formats** — APIs work with JSON, XML, CSV; transformation lets them
  exchange data smoothly.
- **System compatibility** — adapts data structure to what the target system expects.
- **Business efficiency** — automating it reduces manual work and speeds up workflows.
- **Consistency** — keeps data accurate and reliable across platforms.

## Normalization vs Denormalization

| | Normalization | Denormalization |
|---|---|---|
| **Definition** | splits data into smaller related tables (each piece stored once) | combines data into fewer, larger tables (more info in one place) |
| **Goal** | reduce redundancy, keep data consistent | improve performance, reduce joins |
| **Pros** | saves storage, ensures consistency | faster queries, easier retrieval |
| **Cons** | complex queries (many joins) | redundant data → harder/inconsistent updates |
| **Use case** | OLTP (banking, order management — accuracy critical) | OLAP (reporting, analytics — speed matters) |

### OLTP vs OLAP

Two workload types the tables above optimize for:

| | **OLTP** (Online Transaction Processing) | **OLAP** (Online Analytical Processing) |
|---|---|---|
| **Purpose** | run the business — day-to-day operations | analyze the business — insights & reporting |
| **Operations** | many small reads/writes (INSERT, UPDATE, DELETE) | few large, complex read queries (aggregations) |
| **Data** | current, detailed, frequently changing | historical, aggregated, mostly read-only |
| **Design** | **normalized** — avoid redundancy, fast writes | **denormalized** (star/snowflake schema) — fast reads |
| **Users** | many concurrent (customers, apps) | fewer (analysts, dashboards) |
| **Examples** | banking transfer, placing an order, login | monthly sales report, trend analysis, BI dashboards |

Rule of thumb: **OLTP feeds OLAP** — transactional systems capture the raw data,
which is then moved (via ETL) into an analytical store for reporting.

### ETL (Extract, Transform, Load)

The pipeline that moves data from source systems into a target (often a data
warehouse for OLAP):

1. **Extract** — pull raw data from sources (databases, APIs, files, logs).
2. **Transform** — clean, validate, normalize, map fields, convert formats,
   and aggregate so it matches the target's schema.
3. **Load** — write the transformed data into the target system.

- **ETL** transforms *before* loading — classic approach for warehouses.
- **ELT** (Extract, Load, Transform) loads raw data first, then transforms
  inside a powerful target (e.g. a modern cloud warehouse) — common with big
  data where the target can handle the heavy lifting.

Tools: Apache NiFi and Talend automate these workflows (see the tools table below).

## Two key strategies

- **Data mapping** — aligns fields between source and target systems for
  consistency during integration (e.g. `user_id` in one system → `customer_id`
  in another). Without it, data becomes mismatched.
- **Format conversion** — changes the format itself (JSON → XML, XML → CSV),
  enabling cross-platform communication when systems require different formats.

## Common tools

| Category | Tools | Role |
|---|---|---|
| ETL / data integration | Apache NiFi, Talend | extract → transform → load; automate workflows |
| Big data frameworks | Apache Spark, Hadoop | process massive volumes at speed (analytics) |
| API / middleware integration | MuleSoft, Apache Camel | bridge apps — route, convert, enrich data in transit |
| Validation / schema | JSON Schema, XML Schema | enforce strict rules, prevent errors, keep consistency |

## Takeaway

Handling diverse formats, ensuring compatibility, and keeping data consistent —
with the right mapping, conversion, and tooling, systems exchange data
efficiently, securely, and reliably.