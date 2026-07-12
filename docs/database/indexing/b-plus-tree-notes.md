# B+ Tree Indexing — Notes

A **B+ tree** is a refinement of the [B-tree](b-tree-notes.md) where **all data
values live only in the leaf level**, and the **leaves are linked together**.
This makes it the **standard index structure in real databases** — excellent for
both point lookups and **range / sequential** access.

## Properties (order `m`)

| Rule | Value |
|---|---|
| Max children per node | `m` |
| Max keys per node | `m − 1` |
| Min children (non-root) | `⌈m/2⌉` |
| Min keys (non-root) | `⌈m/2⌉ − 1` |
| Root | ≥ 2 children |
| Leaves | **all at the same level** (balanced) |

Same shape as a B-tree — the difference is **where data lives and how leaves
connect**, below.

## What makes a B+ tree different from a B-tree

| | B-tree | **B+ tree** |
|---|---|---|
| Data (record) pointers | In **every** node | **Leaf nodes only** |
| Keys in internal nodes | Real keys with data | **Routing copies** only (guide the search) |
| Search ends | Sometimes early (at an internal node) | **Always at a leaf** (uniform cost) |
| Leaves linked? | No | **Yes — a linked list** across all leaves |
| Fan-out | Lower | **Higher** (internal nodes hold only keys+child pointers) |
| Range/sequential scan | Awkward | **Very efficient** (walk the leaf chain) |

The two headline wins: **higher fan-out** (shorter tree, fewer disk reads) and
the **leaf linked-list** (a range query finds the start, then just follows leaf
pointers).

## Node structure

**Non-leaf (internal) node** — a multi-level sparse index that routes searches:

```
[ P1 | K1 | P2 | K2 | … | K(n-1) | Pn ]
```

- `K` = search-key values (ordered); `P` = child pointers.
- Keys partition the subtrees: everything under `P1` is `< K1`; under `Pi`
  (for `2 ≤ i ≤ n−1`) is `≥ K(i-1)` and `< Ki`; under `Pn` is `≥ K(n-1)`.

**Leaf node** — holds keys + **record pointers**, plus a next-leaf pointer:

```
[ K1,P1 | K2,P2 | … | K(n-1),P(n-1) | Pnext ]
```

- Each `Pi` points to the **actual file record** for `Ki`.
- Leaves are kept in sorted order across the whole tree: if leaf `Li` precedes
  `Lj`, every key in `Li` ≤ every key in `Lj`.
- `Pnext` (the last pointer) links to the **next leaf** — forming the linked list
  that makes sequential/range scans fast.

## Advantages & disadvantages

**vs. plain index-sequential files:** those degrade as data grows (overflow
blocks pile up) and need periodic full reorganization. A B+ tree
**self-reorganizes** with small local splits/merges on each insert/delete — no
whole-file rebuild — and gives efficient access paths, especially for **range
queries** and **sequential access**.

- **Advantages:** balanced & self-maintaining; fast point *and* range queries;
  high fan-out → shallow tree → few I/Os; no periodic reorg.
- **Disadvantages:** extra insert/delete overhead and **space overhead** (keys
  are duplicated as routing copies in internal nodes; extra pointers).
- **Net:** the balance + self-maintenance make B+ trees the **preferred database
  index**.

## Search

Start at the root and follow child pointers down to a leaf, comparing at each
level (`<` → left, `≥` → right per the key partitioning). The search **always
reaches a leaf**, so every lookup costs the same — proportional to the tree's
height, `O(log n)`.

## Insertion

1. Find the target **leaf** and insert the key in sorted order.
2. If the leaf **overflows** (would exceed `m−1` keys): **split** it, and **copy
   up** the smallest key of the new right leaf into the parent as a separator.
   (Note: leaves are *copied* up — the key stays in the leaf too — because all
   data must remain in the leaves.)
3. If the **parent (internal) node** overflows, split it and **push up** its
   median key (internal splits *move* the key up, not copy). This can
   **propagate up to the root**, increasing height.

*Example — insert `Lamport`:* route from the root (`> Mozart` → right subtree),
find the correct leaf; if full, split and promote the separator. In the video's
tree this bubbles `Kim` up into an internal node.

### Splitting a full internal node (the detailed procedure)

1. Conceptually put the node plus the new `(K,P)` into a temporary area holding
   `m+1` pointers / `m` keys, in sorted order.
2. Keep the first `⌈m/2⌉` pointers (and their keys) in the original node.
3. Move the rest into a **new** node.
4. **Push the median key up** to the parent as the separator between the two.

## Deletion

1. Remove the `(key, record-pointer)` from its **leaf**.
2. If the leaf still meets the **minimum** (`⌈m/2⌉ − 1` keys), done.
3. If it **underflows**:
   - **Borrow** a key from a sibling that has spare keys, and update the parent's
     separator (e.g. deleting `Singh`/`Wu` → borrow from the left sibling).
   - If no sibling can spare one, **merge** with a sibling into one node and
     remove the now-unused separator key/pointer from the parent (e.g. deleting
     `Srinivasan` → merge with sibling).
4. If borrowing/merging leaves a node **too full**, **redistribute** entries so
   both siblings exceed the minimum, and update the parent's separator keys.
5. A merge that makes the **parent** underflow cascades the same borrow/merge
   **up the tree**.

## Update cost

An "update" (insert or delete) costs I/O **proportional to the tree height**.
For a B+ tree with `K` entries and max fan-out `n`, worst-case insert/delete is
`O(log⌈n/2⌉ K)` — logarithmic, so it stays efficient even as data grows.

## Summary

- **B+ tree = B-tree with all data in linked leaves.** Internal nodes hold only
  routing keys → higher fan-out, shallower tree.
- Search **always** ends at a leaf (uniform cost); the **leaf linked list** makes
  **range/sequential scans** fast — a key advantage over the B-tree.
- **Insert** splits & copies/pushes separators upward; **delete** borrows/merges
  and can cascade up. Both are `O(log n)` and **self-reorganizing** (no periodic
  file rebuild), which is why B+ trees are the go-to database index.
