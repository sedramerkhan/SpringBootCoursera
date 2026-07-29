# Backup & Recovery — Reference Notes

Protecting a database against data loss: why backups matter, how they differ
from replication, and how to plan and execute one. Part of the
[database notes](../README.md).

## Notes

- [Database Backup](database-backup-notes.md) — backup vs. replication;
  the four planning factors (frequency, amount, urgency, type); threats
  backups guard against and the business-continuity-vs-disaster-recovery
  distinction (with an e-commerce example); backing up relational vs.
  distributed/NoSQL vs. SaaS/cloud systems; benefits; the four backup types
  (full, incremental, differential, synthetic) with a worked Sun–Wed
  example; choosing a type (data volume, backup window, tooling, recovery
  speed) and the common full→differential/incremental→full hybrid pattern;
  real tools by system (RMAN, `mysqldump`, `mongodump`, AWS Backup, etc.);
  and the five-step process for building a backup plan (critical data →
  RTO/RPO → online/offline → strategy → automation).
- [Performing Backup](performing-backup-notes.md) — built-in tools (SSMS,
  Access) vs. third-party enterprise tools (Veritas, Veeam, Commvault);
  manual backup steps (Access example); automated/scripted backup;
  configuring a third-party tool end to end; the daily/weekly/monthly
  schedule best practice; and automating that schedule with a script +
  Windows Task Scheduler.
- [Restoring Databases](restoring-databases-notes.md) — what restoring
  actually is; the step-by-step SSMS restore procedure (Restore Database →
  select backup device → Options: overwrite + close existing connections →
  run); best practices (the 3-2-1 rule, regular restoration testing, daily
  monitoring); what backup failure is and its risks (lost productivity,
  reputation, legal/regulatory penalties); why backups fail (infrastructure,
  media, software, human error); and how to handle failures (coverage
  checks, test restores, automated notifications, daily review).
