# Hashing — Notes

**Hashing** is an alternative to [ordered indices](ordered-indices-notes.md) for
locating records: a hash function maps a search key straight to a bucket
address, giving very fast **equality** lookups without keeping anything sorted.

> **Transcript note:** "grows to 15 times its size" is a mis-transcription of
> **1.5 times** its size, and "extensible hashing" is the standard term
> **extendible hashing** — both corrected below.

## Static hashing

A **hash function** `h` maps every search-key value `K` to a **bucket address**
`B`. A **bucket** is a storage unit (usually a disk block) that can hold one or
more entries. Different keys can **collide** on the same bucket, in which case
the entries there need a **sequential search** to distinguish them.

| | Hash **index** | Hash **file organization** |
|---|---|---|
| What buckets store | Pointers to the actual records | The records **themselves** |
| Data lives | Elsewhere (index points to it) | In the bucket |

## Bucket overflow

**Causes:** too few buckets for the data volume, or a **skewed distribution**
— many records sharing a search-key value, or a hash function that doesn't
spread keys evenly.

**Handling overflow** — two techniques:

- **Closed hashing** (**overflow chaining**): when a bucket overflows, an
  additional **overflow bucket** is allocated and linked to it, forming a
  chain. Every record stays reachable even past the original bucket's
  capacity.
- **Open hashing**: does **not** add overflow buckets. Instead it **probes**
  other slots within the existing fixed set of buckets to place the
  overflowing entry.

> **Naming gotcha:** this is flipped from the general-CS usage you may already
> know. Outside databases, "open hashing" usually means **chaining** and
> "closed hashing" (a.k.a. open addressing) means **probing within a fixed
> table**. Here it's the reverse — **closed** hashing chains overflow buckets,
> **open** hashing probes the fixed set. Go by the definition, not the label.

## Example: hashing on department name

10 buckets; the hash function sums the (simplified) integer/ASCII value of
each character and takes it **mod 10**, so results always land in `0–9`.

`h(Music) = 1`, `h(History) = 2`, `h(Physics) = 3`, `h(Electrical Engineering)
= 3` — Physics and Electrical Engineering **collide** on bucket 3.

| Bucket | Entries (name — department — salary) |
|---|---|
| 0 | *(empty)* |
| 1 | Mozart — Music — 40,000 |
| 2 | El Said — History — 80,000; Califieri — History — 60,000 |
| 3 | Einstein — Physics — 95,000; Gold — Physics — 87,000; Kim — Electrical Eng. — 80,000 |
| 4 | Wu — Finance — 90,000; Singh — Finance — 80,000 |
| 5 | Crick — Biology — 72,000 |
| 6 | Srinivasan — Comp. Sci. — 65,000; Katz — Comp. Sci. — 75,000; Brandt — Comp. Sci. — 92,000 |
| 7 | *(empty)* |

Buckets 8–9 don't appear in this walkthrough. Entries from the **same**
department land in the same bucket (e.g. the two History rows in bucket 2);
Physics and Electrical Engineering share bucket 3 purely from a hash collision.

## Static hashing's limitations

- **Fixed bucket set** — the number of buckets is chosen once, which is
  awkward as the data **grows or shrinks**.
- **Performance degradation** — too few buckets up front → frequent overflow.
- **Space waste** — over-provisioning for anticipated growth leaves buckets
  unused.
- **Fix requires periodic reorganization** — rehashing the whole file with a
  new hash function (and bucket count) is disruptive and expensive.

## Dynamic hashing

Lets the bucket count adjust as the database grows, instead of committing to
one fixed size:

- **Periodic rehashing** — once the table grows past a threshold (e.g. **1.5×**
  its original size), allocate a new, larger table and rehash every entry into
  it all at once.
- **Linear hashing** — rehashes **incrementally** rather than all at once,
  spreading the reorganization cost over time.
- **Extendible hashing** — tailored to disk-based hashing; multiple hash
  values can share a bucket, which lets the structure **double its entry
  capacity without doubling the number of buckets** — efficient for
  disk-based systems.

## Hashing vs ordered indices

Weighing the cost of reorganizing an index comes down to two things:

- **Insert/delete frequency** — the more often data changes, the more often
  reorganization may be needed, and each reorganization costs I/O and
  potential downtime.
- **Average vs. worst-case access time** — optimizing for the average case
  helps typical usage but may worsen the worst case; whether that trade-off is
  acceptable depends on the workload.

Pick based on the **query shape** the system needs to serve:

- **Hashing** — excellent for **equality** lookups (find the record for one
  specific key value).
- **Ordered indices** (e.g. B+ trees) — better for **range queries** (a span of
  dates, a price range, etc.), since hashing has no notion of key order.

## Real-world database support

| Database | Hash indices | Note |
|---|---|---|
| PostgreSQL | Supported, but **discouraged** | Generally underperforms B-tree indices |
| Oracle | **Static hashing** (hash clusters) supported | Does **not** support hash indices |
| SQL Server | **Not supported** | Exclusively uses B+-trees for indices |

B+-trees dominate in practice because they handle **both** exact-match and
range queries well — see [B-Tree](b-tree-notes.md) / [B+ Tree](b-plus-tree-notes.md).

## Summary

- Hashing maps a key straight to a bucket via a hash function — fast equality
  lookups, but no ordering.
- A **hash index** stores pointers in its buckets; a **hash file organization**
  stores the records themselves.
- Bucket overflow is handled by **closed hashing** (chain overflow buckets) or
  **open hashing** (probe the fixed set) — note the DB-textbook naming is the
  reverse of general-CS usage.
- **Static hashing** has a fixed bucket count, so it degrades as data grows or
  wastes space if over-provisioned; fixing it means an expensive full rehash.
- **Dynamic hashing** (periodic rehashing, linear hashing, extendible hashing)
  adjusts the bucket count as data grows, avoiding a single disruptive
  reorganization (except periodic rehashing, which still redoes everything at
  once, just less often).
- Use hashing for **point/equality** lookups; use ordered indices for **range**
  queries. Most production databases lean on B+-trees over hash indices.
