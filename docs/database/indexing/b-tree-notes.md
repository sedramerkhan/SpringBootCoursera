# B-Tree Indexing — Notes

A **B-tree** is a **self-balancing** search tree optimized for systems that read
and write **large blocks** of data — which makes it ideal for database and file
indexes. It keeps data sorted and supports **search, insertion, and deletion** in
`O(log n)` time. It generalizes the [multilevel index](ordered-indices-notes.md#multilevel-index).

> **Transcript note:** the video repeatedly says "B+ tree" while describing the
> B-tree, and "m/2 **factorial**" — that's a mis-transcription of **⌈m/2⌉**
> (*ceiling* of m/2). Both are corrected below. See [B+ Tree](b-plus-tree-notes.md)
> for the more common variant.

## m-way tree & order

A B-tree is a specialized **m-way tree** — each node can have **multiple
children** (not just 2 like a binary tree). The **order `m`** sets the maximum
fan-out.

## Properties (order `m`)

| Rule | Value |
|---|---|
| Max children per node | `m` |
| Max keys per node | `m − 1` |
| Min children (non-root internal node) | `⌈m/2⌉` |
| Min keys (non-root node) | `⌈m/2⌉ − 1` |
| Root | ≥ 2 children (unless it's a leaf) |
| Leaves | **all at the same level** (this is what keeps it balanced) |

Keeping every node between its min and max, and all leaves level, is what makes
search cost depend only on the tree's (small) height.

## Node structure

A B-tree node interleaves **keys**, **tree pointers**, and **data pointers**:

```
[ B1 | K1,P1 | B2 | K2,P2 | … | B(m-1) | K(m-1),P(m-1) | Bm ]
```

- `K` — a **key** (search-key value).
- `P` — a **data pointer**: points to the actual record for that key.
- `B` — a **tree pointer**: points to a child node (subtree).

The defining trait of a **B-tree**: keys carry **data pointers in *every* node**,
including internal ones. So a key appears **once** anywhere in the tree, and a
search can sometimes finish at an internal node without reaching a leaf.

## B-tree vs B+ tree

| | B-tree | B+ tree |
|---|---|---|
| Where keys live | Internal **and** leaf nodes | **Only** in leaves (internal = routing copies) |
| Data pointers | In every node | In leaves only |
| Search can end early | Yes (key may be found in an internal node) | No (always goes to a leaf) |
| Fan-out | Lower (internal nodes carry data pointers → bigger → fewer fit) | Higher |
| Typical use | Rarer | **The standard DB index** |

### Advantages vs disadvantages of the B-tree

- **Advantages:** may use **fewer nodes** than a B+ tree; can locate a key
  **before** reaching a leaf → occasionally faster.
- **Disadvantages:** only a *small fraction* of searches end early (most still
  descend all levels); internal nodes are **larger** (they store data pointers),
  which **reduces fan-out and increases depth**; insert/delete are more complex.
- **Net:** the advantages rarely outweigh the costs, so **B+ trees are more
  commonly used** in practice.

## Search

Like a binary-search-tree lookup, but m-way: at each node, compare the target
against the node's keys and follow the tree pointer into the correct subtree;
repeat until found (or a leaf is reached). Cost is `O(log n)` — proportional to
the tree's height.

## Insertion (always at a leaf)

1. Traverse to the correct **leaf**.
2. If the leaf has room (**< m−1** keys), insert the key in sorted order — done.
3. If the leaf is **full** (**overflow**):
   - Insert in order (temporarily over-full), then **split** the node at its
     **median** key.
   - **Push the median up** into the parent.
   - If the parent now overflows, split it the same way — **recursively up to
     the root** (a root split increases the tree's height).

*Example — insert `8` into an order-5 tree:* `8` goes to the right of `5` in its
leaf; that leaf now holds 5 keys but the max is `m−1 = 4` → **overflow**. Split
at the median and push the median key up to the parent, restoring balance.

## Deletion (at a leaf; may cascade up)

1. Locate the leaf holding the key.
2. If the leaf keeps **more than the minimum** (`⌈m/2⌉ − 1`) keys after removal,
   just delete it — done.
3. Otherwise (**underflow**), restore the minimum:
   - **Borrow** from a sibling that has spare keys: move a key up to the parent
     and the parent's separating key down into the deficient node (works with a
     left *or* right sibling).
   - If no sibling can spare a key, **merge** the node with a sibling (pulling
     the separating key down), then fix up the parent.
4. **Deleting from an internal node:** replace the key with its **in-order
   predecessor** (largest key in the left subtree) or **successor** (smallest in
   the right subtree), then delete that key from the leaf.
5. If a merge leaves the **parent** below minimum, apply borrow/merge to the
   parent too — **recursively up the tree**.

## Summary

- A B-tree is a **balanced m-way** search tree; order `m` caps children at `m`
  and keys at `m−1`, with a `⌈m/2⌉` minimum and **all leaves level**.
- Unlike a B+ tree, it stores **data pointers in every node**, so searches can
  end early — but larger internal nodes mean lower fan-out, so **B+ trees are
  usually preferred**.
- Search is `O(log n)`; **insert** splits overflowing leaves upward; **delete**
  borrows/merges to fix underflow, cascading up if needed.
