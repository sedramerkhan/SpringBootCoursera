# Indexing — Reference Notes

DBMS indexing structures — how databases locate rows without full scans. Part of
the [database notes](../README.md).

## Notes

- [Ordered Indices](ordered-indices-notes.md) — search keys; clustering vs
  secondary; dense vs sparse; multilevel indices; index updates on
  insert/delete.
- [B-Tree Indexing](b-tree-notes.md) — balanced m-way search tree; order &
  properties; node structure; B-tree vs B+ tree; search, insertion (split),
  deletion (borrow/merge).
- [B+ Tree Indexing](b-plus-tree-notes.md) — all data in **linked leaves**;
  higher fan-out; leaf-chain range scans; node structure; insertion (split &
  copy/push-up), deletion (borrow/merge/redistribute); update cost. The standard
  DB index.
- [Hashing](hashing-notes.md) — static hashing; hash index vs hash file
  organization; bucket overflow (closed/open hashing); dynamic hashing
  (periodic rehashing, linear, extendible); hashing vs ordered indices.
