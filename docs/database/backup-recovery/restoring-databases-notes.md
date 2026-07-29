# Restoring Databases — Notes

## What is restoring?

**Restoring** is the process of copying data from a backup — and applying
any logged transactions since that backup was taken — to bring a database
back to a specific state. It takes a backup file and turns it back into a
fully operational database.

Restoration is what makes a backup actually useful: without the ability to
restore, a backup is just a file sitting in storage. It's the critical step
in any disaster recovery scenario — everything covered under [database
backup](database-backup-notes.md) exists to make this moment possible.

## Restoring in SQL Server (SSMS) — step by step

**Prerequisites**: SQL Server Management Studio (SSMS) installed, and the
backup file available in the default backup location.

1. Log into the machine where the database will be restored.
2. Open **SSMS**.
3. In the left navigation pane, right-click **Databases** → **Restore
   Database**.
4. Under **Source**, select **Device**, then click the **...** button to
   browse for the backup device.
5. In the popup, click **Add**, browse to the backup file, select it, and
   click **OK**.
6. In the left navigation of the restore dialog, click **Options**.
7. On the Options pane, select **Overwrite the existing database (WITH
   REPLACE)** and **Close existing connections to destination database**.
8. Run the restore — this applies the backup and finalizes the database at
   that saved state.

Steps 6–7 matter because they control exactly *how* the backup gets applied
— without them, an in-use database with existing connections can't safely
be overwritten by the restore.

## Best practices for backup & restoration

- **Reliable data backup** — a restore is only as good as the backup behind
  it, so use a robust backup solution. Follow the **3-2-1 rule**: keep
  **3** copies of the data, on **2** different types of storage/devices,
  with **1** kept **offsite**.
- **Regular restoration testing** — periodically actually restore from a
  backup to confirm it's viable, rather than assuming it works because the
  backup job reported success.
- **Daily monitoring** — generate a daily backup status report and review
  it, so problems are caught and addressed promptly instead of discovered
  during an actual emergency.

## Backup failure

A **backup failure** is when a backup operation doesn't complete
successfully, *or* the resulting backup file turns out to be unusable at
restore time. No backup infrastructure is 100% fail-proof — a meaningful
fraction of backups fail, which is exactly why the practices above (testing,
monitoring) exist.

**Risks of backup failure**:

- **Lost productivity** — without a reliable backup, recovering from data
  loss takes much longer.
- **Reputation cost** — data loss can break customer/client trust.
- **Penalties and legal issues** — regulated industries can face legal
  penalties for failing to maintain proper backups.

**Why backups fail**:

| Cause | Example |
|---|---|
| **Infrastructure issues** | Network problems, especially for WAN or cloud-based backups |
| **Media issues** | Physical media degradation, corruption, or storage failure |
| **Software issues** | Bugs or misconfiguration in the backup software |
| **Human error** | Mistakes in configuring, executing, or maintaining backups |

## Handling backup failure

- **Ensure complete coverage** — confirm the backup job actually includes
  all the files/data it's supposed to.
- **Regular test restores** — routinely restore critical files as a test,
  to verify backup integrity before an emergency forces the question.
- **Automated notifications** — configure the backup software to send a
  status notification after every job, so failures surface immediately.
- **Daily review** — check backup reports/notifications daily to catch and
  fix problems early.

## Summary

- **Restoring** = applying a backup (+ any logged transactions since) to
  bring a database back to a specific working state — the step that makes
  a backup actually worth having.
- Restoring in SSMS: Databases → Restore Database → select the backup
  device → under Options, enable overwrite + close existing connections →
  run.
- Best practices: a reliable backup following the **3-2-1 rule**, **regular
  restoration testing**, and **daily monitoring** of backup status.
- **Backup failure** — an incomplete backup or an unusable backup file —
  carries real risk: lost productivity, reputation damage, legal/regulatory
  penalties. Causes span infrastructure, media, software, and human error.
- Mitigate it with complete-coverage checks, regular test restores,
  automated failure notifications, and daily report review.
