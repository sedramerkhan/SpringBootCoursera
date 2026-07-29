# Performing Database Backups — Notes

The practical side of backup: what tools actually run a backup, the manual
vs. automated steps to do it, and the scheduling practices that keep it
happening reliably. Complements the concepts in [database
backup](database-backup-notes.md) — the *why*/*what type* — with the
*how*.

## Tools and software

| Category | Examples | Fit |
|---|---|---|
| **Built-in / native tools** | SQL Server Management Studio (SSMS) for Microsoft SQL Server; Microsoft Access's own backup option | Streamlined backups directly from the database's own management interface — no extra software needed for regular backups |
| **Third-party enterprise tools** | Veritas Backup Exec, Veeam Backup & Replication, Commvault | Add deduplication, encryption, cloud storage integration, and centralized management — suited to complex, large-scale environments the built-in tools aren't built for |

## Manual backup process (example: Microsoft Access)

1. Navigate to the **File** tab.
2. Select **Save As**.
3. Choose **Save Database As**.
4. Under advanced settings, click **Back Up Database**, then **Save As** to
   write the backup file.

Straightforward, and guarantees a recent backup exists before doing
something risky — but it's manual, so it only happens when someone
remembers to run it.

## Automated backup process (scripted)

1. Write a script (e.g. VBScript for Microsoft Access) that copies the
   current database file to a backup filename.
2. Add a brief delay (e.g. ~10 seconds) so the copy operation completes
   cleanly before anything else touches the file.
3. Execute the backup by shelling out the copy process.

Automation removes the human-error/forgetfulness risk a manual process
carries — the backup runs whether or not anyone remembers to trigger it.

## Setting up a third-party backup tool

1. **Install** the chosen tool and follow its setup wizard.
2. **Configure core settings** — backup frequency and storage location.
3. **Set backup preferences** — which [backup type](database-backup-notes.md#types-of-backup)
   to use (full, incremental, differential, synthetic), tailored to the
   organization's actual data-protection needs.
4. **Set up encryption** for the backups at rest.
5. **Run and monitor** — start the backup from the tool's interface, and
   watch for alerts/failures so problems are caught immediately rather than
   at restore time.

## Best practices — backup schedule

A layered schedule, not a single cadence:

- **Daily** — captures the most recent changes; minimizes how much could be
  lost between backups.
- **Weekly** — a checkpoint on top of daily backups, giving multiple
  restore points across the week.
- **Monthly** — long-term archival; establishes a historical record of the
  database's state over time, so a specific past version can be retrieved
  later.

## Automating the schedule: script + Windows Task Scheduler

1. Write the backup script (e.g. VBScript copying the database file to a
   designated backup location).
2. Create a new task in **Windows Task Scheduler** dedicated to running that
   script.
3. Configure the task: execution frequency, start time, and any parameters
   the script needs.
4. Fine-tune the task's settings for performance and reliability as it runs
   over time.

Task Scheduler turns the script into a self-sustaining process — backups
keep running on schedule without manual intervention, and the task
definition itself is the structured record of when/how they run.

## Summary

- Backups run either through a database's **built-in tools** (SSMS, Access)
  for straightforward cases, or **third-party enterprise tools** (Veritas,
  Veeam, Commvault) when dedup/encryption/cloud integration/scale are
  needed.
- **Manual** backup (e.g. File → Save As → Back Up Database in Access) is
  simple but depends on someone remembering to run it; **automated**
  backup (a script copying the file, delay, then execute) removes that
  dependency.
- Setting up a third-party tool means: install → configure frequency/
  location → pick a backup type → enable encryption → run and monitor.
- A solid schedule layers **daily** (recent changes), **weekly**
  (checkpoints), and **monthly** (long-term archival) backups.
- **Windows Task Scheduler** running a backup script is the standard way to
  turn "someone has to remember to back up" into "it just happens" —
  configure frequency, start time, and parameters once.
