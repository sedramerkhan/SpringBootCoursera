# Data Integrity — Notes

**Data integrity** is the accuracy and consistency of data throughout its
lifecycle — ensuring data stays reliable, accurate, and intact as it's
stored, modified, transferred, or deleted.

## Physical vs. logical data integrity

| Type | Focus |
|---|---|
| **Physical data integrity** | How data is stored and accessed — the physical storage itself is secure and reliable. |
| **Logical data integrity** | Preventing human error and maintaining consistency through **rules and constraints** that preserve the logical structure and relationships within the data. |

## Why it matters

Compromised data integrity can have disastrous consequences — e.g. a company
tracking dry-ice temperature data: if that data is corrupted, shipments spoil
and the resulting losses (and in some contexts, safety risk) can be severe.

Maintaining data integrity:

- Prevents costly errors.
- Ensures regulatory compliance.
- Supports informed decision-making.

Data integrity is a **business necessity**, not just a technical concern.

## Common causes of integrity loss

| Cause | How it compromises data |
|---|---|
| **Human error** | Mistakes during entry, update, or deletion cause corruption or loss. |
| **Transfer error** | Incomplete transfers or corruption in transit. |
| **Cyber threats** | Hacking, malware, ransomware altering, deleting, or corrupting data. |
| **Security issues** | Weak access control lets unauthorized users manipulate data. |
| **Hardware/infrastructure issues** | Storage device failure, power outages, network issues causing corruption or loss. |

## Strategies to maintain data integrity

- **Data entry training** — staff enter data correctly and consistently.
- **Input validation** — enforce rules so only correct, expected data is
  accepted (see [constraints](../sql/table-constraints-notes.md) below).
- **Remove duplicate data** — clean the database regularly to eliminate
  duplicates that cause inconsistency.
- **Back up data** regularly, so corrupted/lost data can be restored (see
  the full [Backup & Recovery notes](../backup-recovery/README.md)).
- **Access control** — prevent unauthorized changes.
- **Audit trail** — log all data changes to monitor and review for
  integrity issues.
- **Penetration testing / security audits** — regularly test for
  vulnerabilities and keep security measures current.
- **Load and stress testing** — verify the database holds up under peak
  conditions without compromising integrity.
- **SSL encryption** — protect data in transit between client and server.
- **Process maps** for data handling — consistent, repeatable practices.
- **Culture** — make the whole team aware that data integrity matters.
- **Cybersecurity** — robust measures to protect against threats generally.

## Enforcing integrity with constraints

SQL constraints are the primary mechanical tool for enforcing data
integrity — see the full [Table Constraints notes](../sql/table-constraints-notes.md)
for syntax and examples. Quick mapping to the integrity types below:

| Constraint | Integrity type it enforces |
|---|---|
| `CHECK` | Domain integrity — value satisfies a condition (e.g. `age > 0`) |
| `UNIQUE` | Prevents duplicates (e.g. one row per email address) |
| `PRIMARY KEY` | Entity integrity — every record has a unique identifier |
| `FOREIGN KEY` | Referential integrity — a value must exist in the referenced table |

## Triggers — beyond what constraints can express

When a rule is too complex for a `CHECK` constraint, a
[trigger](../plsql/triggers-notes.md) can enforce it in code that runs
automatically on `INSERT`/`UPDATE`/`DELETE` (`AFTER` triggers) or replace the
operation entirely (`INSTEAD OF` triggers) — see the triggers notes for
timing options and examples.

## Types of data integrity

| Type | Ensures | Example |
|---|---|---|
| **Physical integrity** | Data survives hardware failure/environmental factors | RAID for data redundancy |
| **Entity integrity** | Every record has a unique identifier — no duplicates | A unique SSN per employee in an HR database |
| **Referential integrity** | Foreign keys match a primary key in the referenced table | An `Orders` row can't reference a nonexistent `CustomerID` |
| **Domain integrity** | Values in a column stay within a valid range/format | A patient's date of birth must be a valid, in-range date |
| **User-defined integrity** | Custom business rules not covered by the above | An employee's salary must fall within their job title's approved pay scale |

## Data integrity by sector

- **Healthcare** — accurate electronic health records are essential for
  correct diagnosis/treatment; discrepancies can lead to incorrect,
  potentially life-threatening treatment.
- **Finance** — institutions depend on precise transaction data for risk and
  fraud decisions; e.g. banks use **KYC (Know Your Customer)** protocols to
  authenticate identities, stay compliant, and prevent money laundering.
- **Education** — accurate student records support enrollment management,
  academic progress tracking, and institutional reporting that informs
  resource allocation and policy.

## Summary

- Data integrity = data staying accurate and consistent through its whole
  lifecycle; split into **physical** (secure storage) and **logical**
  (rules/constraints preserving structure).
- Loss comes from human error, transfer errors, cyber threats, weak
  security, and hardware/infrastructure failure.
- Maintaining it takes a mix of training, validation, deduplication,
  backups, access control, audit trails, security testing, encryption, and
  a integrity-first culture.
- Mechanically, **constraints** (`CHECK`, `UNIQUE`, `PRIMARY KEY`,
  `FOREIGN KEY`) and **triggers** (for logic constraints can't express) are
  the tools; conceptually, integrity breaks down into **physical, entity,
  referential, domain,** and **user-defined**.
- It's a cross-sector concern: healthcare (patient safety), finance
  (fraud/compliance via KYC), and education (accurate records) all depend on
  it.
