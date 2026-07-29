# Database Backup — Notes

A **database backup** is an exact, complete copy of a database, stored in a
**separate location**, used to restore lost data after an emergency (outage,
system failure, corruption, attack). Its purpose is restoring data to a
**specific point in time** — which is why it matters for compliance and
long-term archival, not just uptime.

## Backup vs. replication

These are easy to conflate but solve different problems:

| | Backup | Replication |
|---|---|---|
| **Goal** | Long-term data preservation and recovery | Minimize recovery time, keep business running through a disaster |
| **Time horizon** | Point-in-time snapshot, kept long-term | Near-real-time copy |

Replication keeps the business running *during* a disaster; backup is what
lets you recover data *after* one, including from mistakes replication would
faithfully copy too (e.g. an accidental mass delete propagates to replicas
immediately, but a backup taken before it still has the data).

## Four factors in a backup strategy

| Factor | Consideration |
|---|---|
| **Frequency** | Frequently-updated data needs more frequent backups, so less is lost between backup intervals |
| **Amount** | Larger datasets need more storage and time, and often push toward incremental/differential backups instead of repeated full ones |
| **Urgency** | How fast the most recent backup must be accessible if something fails — critical, rapidly-changing data needs more frequent backups |
| **Type** | Different data needs different handling — transactional data backed up more often than static data; sensitive data may need encryption; large multimedia files may need dedicated storage |

## Why backups are crucial

**Threats**: data corruption/loss, natural disasters, hardware failures,
power outages, human error, cyberattacks (e.g. ransomware).

**Business continuity (BC) vs. disaster recovery (DR)** — another pair
worth not conflating:

- **Disaster recovery** — restoring the *technology* (systems, data) after
  an incident.
- **Business continuity** — ensuring the entire *business* keeps
  functioning, of which DR is one part.

**Example — "XYZ" e-commerce platform**, storing orders, payments,
inventory, and shipping data:

- **Hardware failure** → IT restores the most recent backup from cloud
  storage, recovering the lost data and bringing the platform back online
  (disaster recovery in action).
- **Ransomware attack** → instead of paying, IT restores from an unaffected
  backup, keeping operations running without compromising security or
  customer trust (business continuity in action — the backup is what makes
  *not paying* a viable option).

## Backing up different database architectures

Backup strategy depends heavily on what's being backed up. A business often
runs several databases, each suited to a function, each backed up
differently:

| System type | Example | Backup approach |
|---|---|---|
| **Relational** (structured tables) | Oracle (accounting), MySQL, SQL Server | Benefit from **incremental/differential** backups (only changes since last backup); sensitive data (e.g. financial) may need more frequent backups plus encryption |
| **Distributed / NoSQL** | MongoDB, Cassandra, Hadoop | Data spread across nodes — backup is more complex, needs coordination across nodes to keep a consistent snapshot; may prioritize near-real-time replication to avoid stock/data discrepancies (e.g. an inventory system like SAP) |
| **SaaS / cloud-managed** | Microsoft 365, AWS-hosted services | The provider manages the underlying infrastructure, but you still need your **own** backup strategy — e.g. regular exports to on-premise storage or a different cloud, to protect against provider-side outages |

## Benefits of database backup

- **Fast data recovery and replication** — quicker recovery after a
  disaster, failure, or corruption.
- **Stronger data security** — protection against cyberattacks, accidental
  deletion, and corruption.
- **Easier data management** — backups can be scheduled/organized to align
  with how the organization actually operates.
- **Improved performance** — a deliberate backup strategy (vs. ad hoc) keeps
  systems running optimally.
- **Controlled cost** — choosing the right mix of full/differential/
  incremental backups optimizes storage spend instead of over-storing.
- **Better compliance** — many industries have strict data-retention/
  protection regulations; regular backups make compliance demonstrable
  during audits.

## Types of backup

| Type | What it copies | Trade-off |
|---|---|---|
| **Full** | The entire database, every time | Simplest to restore from (one copy), but slowest and most storage-heavy to create repeatedly — best fit for a small business with a small amount of data |
| **Incremental** | Only data changed since the **last backup of any type** (full, differential, or another incremental) | Fastest/smallest to create; restore requires the last full backup **plus every incremental since** |
| **Differential** | All changes since the **last full backup** (not since the last backup of any type) | Restore only needs the last full backup **plus the latest differential**; grows larger over time until the next full backup — slower to create and more storage than incremental, but simpler/faster to restore |
| **Synthetic (full)** | Nothing new from the source — it's **assembled** from an existing full backup plus its subsequent incrementals, without re-reading the live database | Gets a full backup's completeness at incremental-level cost: less time, less storage, and far less network/production-system load, since only already-changed data ever moves. The one catch: assembling it overwrites/consolidates the incrementals it was built from |

**Worked example** — full backup on Sunday, then something changes daily:

| Day | Incremental backs up... | Differential backs up... |
|---|---|---|
| Sun | *(full backup — the baseline)* | *(full backup — the baseline)* |
| Mon | changes since **Sunday's full** | changes since **Sunday's full** |
| Tue | changes since **Monday's incremental** | changes since **Sunday's full** (Mon + Tue) |
| Wed | changes since **Tuesday's incremental** | changes since **Sunday's full** (Mon + Tue + Wed) |

To restore Wednesday's state:

- **Incremental** → Sunday's full **+ Mon's + Tue's + Wed's** incrementals,
  applied in order. Each one only points back to the *previous backup*, so
  the whole chain is required — lose one link and the restore breaks.
- **Differential** → Sunday's full **+ only Wednesday's** differential,
  since each differential already contains everything since the full.

That's the actual trade-off: incremental backups stay small individually but
demand the full chain to restore; differential backups grow larger each day
(until the next full) but only ever need the last full plus the single
latest one.

The full-vs-incremental-vs-differential choice is exactly the storage/
recovery-time trade-off referenced under "controlled cost" above: fewer full
backups saves storage, but makes restores depend on a longer chain of
backups being intact.

### Choosing which type(s) to use

Where the [four factors](#four-factors-in-a-backup-strategy) above shape a
backup strategy in general, picking the specific **type** comes down to:

- **Amount of data** — larger volumes push toward incremental/synthetic over
  repeated fulls.
- **Time available** for the backup window — how long backup operations can
  run without impacting production.
- **Software/OS** in use — the tooling available constrains which types are
  practical (see [tools by system](#backup-tools-by-database-system) below).
- **Recovery speed required** — how fast data must come back after an
  emergency; simpler restore chains (full, differential) win here over
  incremental's multi-file chain.

**Common pattern**: most organizations run a hybrid strategy — an initial
**full** backup, then **differential or incremental** backups in between,
with another **full** (or a **synthetic full**, to avoid re-reading the live
database) taken occasionally to reset the chain.

### Backup tools by database system

| System | Typical tool |
|---|---|
| PostgreSQL | Built-in backup functionality (e.g. `pg_dump`/`pg_basebackup`) |
| Microsoft SQL Server | Built-in backup functionality |
| Oracle | RMAN (Recovery Manager) |
| MySQL | `mysqldump` |
| MongoDB | `mongodump` |
| Cassandra | Snapshot-based backup |
| Amazon DynamoDB | AWS Backup |
| Large files / multimedia (long-term) | Amazon S3 Glacier |
| Large files / multimedia (cost-effective) | Google Cloud Storage Nearline |
| Large-scale enterprise backup/recovery | IBM Spectrum Protect |

Matches the [relational vs. distributed/NoSQL vs.
SaaS](#backing-up-different-database-architectures) split above, just down
at the level of the actual tool you'd run for each.

## Creating a backup plan — five steps

1. **Identify the critical data** that must be protected — not everything
   necessarily needs the same treatment.
2. **Define recovery objectives**:
   - **RTO (Recovery Time Objective)** — the maximum acceptable time to
     restore data after a disaster.
   - **RPO (Recovery Point Objective)** — the maximum acceptable amount of
     data loss (i.e. how old the restored data is allowed to be).
3. **Choose online vs. offline backup**, based on those objectives:
   - **Online (cloud)** — accessible from anywhere, often automated.
   - **Offline (on-premise, e.g. tape/external drive)** — more control and
     security, less automatic.
4. **Select a backup strategy** — full, incremental, differential, or a
   combination — based on backup frequency, data volume, and how fast
   restoration needs to happen.
5. **Implement automation** — tools that run backups, monitor success,
   alert on failure, and replicate across multiple locations so a backup is
   always available.

## Summary

- A **backup** is a full, point-in-time copy of a database kept separately,
  for long-term recovery — distinct from **replication**, which targets
  continuity *during* an incident, not recovery *after* one.
- A backup strategy is shaped by four factors: **frequency**, **amount**,
  **urgency**, **type**.
- Backups guard against corruption/loss, disasters, hardware failure, human
  error, and cyberattacks — serving both **disaster recovery** (restore the
  tech) and **business continuity** (keep the business running).
- Backup approach depends on the database architecture: relational systems
  suit incremental/differential backups; distributed/NoSQL systems need
  cross-node coordination; SaaS/cloud data still needs its own
  independent backup, not just trust in the provider.
- **Full** backups copy everything every time; **incremental** copies
  changes since the last backup of any kind; **differential** copies
  changes since the last full backup; **synthetic full** assembles a new
  full backup from an existing full + its incrementals without re-reading
  the live database — a storage-vs-restore-complexity trade-off across all
  four.
- Picking a type comes down to data volume, backup-window time, the
  tooling available, and required recovery speed — most organizations land
  on a **hybrid**: full → differential/incremental → occasional full/
  synthetic-full.
- Real-world tooling differs by system: `pg_dump`/built-in for PostgreSQL
  and SQL Server, RMAN for Oracle, `mysqldump` for MySQL, `mongodump` for
  MongoDB, snapshots for Cassandra, AWS Backup for DynamoDB, and
  Glacier/Nearline/Spectrum Protect for large-scale file storage.
- Build a backup plan by: identify critical data → define RTO/RPO → choose
  online/offline → pick a backup-type strategy → automate it.
