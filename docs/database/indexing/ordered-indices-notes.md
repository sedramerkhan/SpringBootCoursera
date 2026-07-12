# Ordered Indices — Notes

An **index** speeds up data access — like a book's index or a library catalog,
it lets you locate records without scanning the whole table. The trade-off: an
index is extra structure the database must **store and keep updated** on every
write.

- **Search key** — the attribute (or set of attributes) used to look records up.
- **Index file** — a file of `(search-key, pointer)` **index entries**, much
  smaller than the data file.

Two basic kinds of index:

| Kind | How keys are organized | Best for |
|---|---|---|
| **Ordered index** | Search keys stored **sorted** | Range scans, ordered access |
| **Hash index** | Keys distributed across **buckets** by a hash function | Fast equality (`=`) lookups |

This note covers **ordered indices**; hashing is a separate topic.

## Clustering vs secondary index

| | Clustering (primary) index | Secondary (non-clustering) index |
|---|---|---|
| Definition | Index order **matches** the file's physical sort order | Index order **differs** from the file's physical order |
| Search key | Usually (not necessarily) the primary key | Any other attribute |
| Per table | **One** (a file can be physically sorted only one way) | **Many** |
| Can be sparse? | Yes | **No — must be dense** (data isn't sorted by this key, so every value needs its own pointer) |

An **index-sequential file** is a data file sorted on a search key, with a
clustering index on that key.

## Evaluating an index

Choose an index type by the **access it supports** and its costs:

- **Access types** — point/equality lookup (a specific value) vs **range** query
  (values within a span).
- **Access time** — how fast a lookup is.
- **Insertion time** / **deletion time** — cost of keeping the index updated.
- **Space overhead** — extra storage the index consumes.

## Dense index

An index entry for **every search-key value** in the file — each key has a
direct pointer to its record.

```
search key            data file
10101  ──────────────▶ 10101  Srinivasan  Comp. Sci.  65000
12121  ──────────────▶ 12121  Wu          Finance     90000
15151  ──────────────▶ 15151  Mozart      Music       40000
```

- **Fast, direct access** — every value points straight at its row; no scanning.
- Works on a non-key attribute too: a dense index on `department` has one entry
  per distinct department pointing to the **first** matching record, then the
  file continues in order (e.g. `Comp. Sci.` → Srinivasan, then Katz, Brandt…).

## Sparse index

An index entry for **only some** search-key values. **Requires the file to be
sorted on the search key** (so it only works as a *clustering* index).

```
search key            data file (sorted by key)
10101  ──────────────▶ 10101  Srinivasan …
                       12121  Wu …
                       15151  Mozart …
22222  ──────────────▶ 22222  Einstein …
                       32343  El Said …
```

**To find a record:** locate the largest index entry **≤** your search key, jump
to its record, then **scan forward** in the file until you find it.

- **Less space** and **less maintenance** than a dense index.
- Slower per lookup (the sequential scan), so it trades speed for storage — good
  for large, sequentially-ordered files.

**Dense vs sparse:** dense = faster lookups, more space/maintenance; sparse =
compact and cheap to maintain, but slower and only on a sorted (clustering) key.

## Multilevel index

If the index itself is too large to search efficiently, build **an index on the
index**: a **sparse outer index** over the sorted **inner index**. You search
the small outer level first, which points into the inner level, which points into
the data. This keeps large indices scalable (the idea B-trees generalize).

## Updating an index

Every index on a table must be updated on **insert**, **delete**, and (if the
indexed attribute changes) **update**.

### Deletion

- If the deleted record was the **only** one with its search-key value, remove
  that key from the index.
- **Dense index** — remove the record's index entry (mirrors the file deletion).
- **Sparse index** — if the deleted key has an index entry, **replace it with the
  next search-key value** in the file; if that next key already has its own
  entry, just delete (no replacement) to avoid duplicates.

### Insertion

Do a lookup by the new record's search key to find where it goes, then:

- **Dense index** — if the key isn't already present, add an entry. (New rows may
  need **overflow blocks** when a data block is full.)
- **Sparse index** — usually **no change**, unless a **new block** is created —
  then insert the **first search key of the new block** into the index.

### Multilevel

The multilevel insert/delete algorithms are just the single-level algorithms
applied at each level — the same principles, extended for scalability.

## Summary

- An index maps a **search key** to record pointers to avoid full scans; costs
  storage + write-time maintenance.
- **Ordered** (sorted keys) vs **hash** (bucketed) — this note is ordered.
- **Clustering** = matches physical order (one per table, can be sparse);
  **secondary** = different order (many per table, must be dense).
- **Dense** = entry per key (fast, bigger); **sparse** = entry per block (compact,
  needs a sorted file + sequential scan).
- **Multilevel** = index-on-index for scalability; all levels update on
  insert/delete.
