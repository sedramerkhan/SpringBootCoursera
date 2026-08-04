# Object-Relational Mapping (ORM) — Notes

The concept behind [`jpa-h2-notes.md`](./jpa-h2-notes.md): what ORM actually
*is*, why it exists, and the five moving parts you configure to wire it up
with JPA. Read this first if `@Entity`/`EntityManager`/persistence units
don't have a mental model yet; read `jpa-h2-notes.md` for how it looks wired
into this specific project (Spring Data JPA + H2).

## What ORM is

**ORM (Object-Relational Mapping)** is a technique that bridges the gap
between two worlds that don't naturally fit together:

- **Object-oriented code** — objects, classes, references, inheritance.
- **Relational databases** — tables, rows, columns, foreign keys.

This gap is sometimes called the **object-relational impedance mismatch**:
a `Customer` object with a `List<Order>` doesn't map 1:1 onto rows in a
`customer` table joined to an `order` table. ORM is the layer that
translates between the two automatically, so you can persist/query objects
directly instead of hand-writing the translation (SQL) yourself every time.

## Why use ORM — the benefits

| Benefit | What it means |
|---|---|
| **Less boilerplate** | The ORM framework handles the SQL — no repetitive `SELECT`/`INSERT`/`UPDATE`/`DELETE` statements or JDBC connection/statement/result-set plumbing. |
| **Portability** | Application code targets Java objects, not a specific SQL dialect. Swapping the underlying database (e.g. H2 → PostgreSQL) is mostly a dependency + config change, not a rewrite. |
| **Maintainability** | Data access logic shrinks to entity classes and repository calls, which reads far more clearly than scattered SQL strings. |
| **Object-oriented approach** | You think and write in terms of Java classes and objects — the paradigm the rest of the app is already in — instead of context-switching into SQL. |

See also the JDBC → Spring JDBC → JPA → Spring Data JPA progression table in
[`jpa-h2-notes.md`](./jpa-h2-notes.md#the-evolution-jdbc--spring-jdbc--jpa--spring-data-jpa) —
that table is really "how much of the ORM benefit above do you get at each
layer."

## Configuring ORM with JPA

**JPA (Java Persistence API)** is the Java *specification* for ORM — it
defines the annotations and APIs; it doesn't do the work itself. **Hibernate**
(or EclipseLink/Toplink) is the implementation that actually runs it. See
[`jpa-h2-notes.md`](./jpa-h2-notes.md#jpa-vs-hibernate--the-api-vs-its-implementation)
for the full JPA-vs-Hibernate breakdown.

Five pieces to configure, in order:

1. **Dependencies** — add a JPA provider to the project (e.g. Hibernate via
   `spring-boot-starter-data-jpa`, or EclipseLink).
2. **Entity classes** — plain Java/Kotlin classes annotated `@Entity`, each
   representing one database table (fields → columns, `@Id` → primary key).
3. **Persistence unit** — in plain (non-Spring) JPA, this is declared in
   `META-INF/persistence.xml`: it names the unit, lists its entity classes,
   and configures the datasource connection, SQL dialect, and other
   provider properties.
4. **`EntityManager`** — the runtime object you use to talk to the database:
   `persist()` (insert), `find()` (lookup by id), `createQuery()` (JPQL).
   This is the raw-JPA equivalent of what a `JpaRepository` gives you for
   free in Spring Data JPA.
5. **Transaction management** — database operations are wrapped in
   transactions so they commit or roll back atomically. In Spring, this is
   `@Transactional` — it wraps the annotated method (or class) in a
   transaction and commits on success / rolls back on an unhandled
   exception, instead of you managing `begin()`/`commit()`/`rollback()` by
   hand.

### Plain JPA vs. this project (Spring Boot)

Spring Boot auto-configures nearly all of the above — see
[`auto-configuration-notes.md`](../core/auto-configuration-notes.md):

| Piece | Plain JPA | Spring Boot |
|---|---|---|
| Persistence unit | `META-INF/persistence.xml` | `application.properties`/`.yaml` (`spring.datasource.*`, `spring.jpa.*`) — no XML |
| `EntityManager` | obtained manually from an `EntityManagerFactory` | auto-configured bean, injectable directly, or hidden entirely behind a `JpaRepository` |
| Transactions | manual `EntityTransaction` begin/commit/rollback | `@Transactional` |

**Takeaway:** the underlying automated process — map objects to tables,
let the provider generate SQL, wrap writes in transactions — is the same
whether you configure it by hand (`persistence.xml` + `EntityManager`) or
let Spring Boot wire it up for you. Spring Boot just removes the manual
plumbing, the same way Spring Data JPA removes the repository plumbing on
top of that.

## Trade-offs to keep in mind

ORM isn't free — it trades some control for convenience:

- **Generated SQL isn't always optimal.** Complex queries may need a
  hand-written JPQL/native query (`@Query`) instead of relying on
  derived-query or default generation.
- **N+1 query problem.** Naively accessing a lazy-loaded association per
  row in a loop can silently issue one query per row instead of one join —
  worth knowing about before it shows up as a performance bug.
- **Leaky abstraction under load.** For very performance-sensitive or
  bulk operations, dropping to SQL (Spring JDBC / native queries) can still
  be the right call — ORM doesn't replace SQL knowledge, it reduces how
  often you need it.

## See also

- [`jpa-h2-notes.md`](./jpa-h2-notes.md) — this project's concrete JPA +
  Spring Data JPA + H2 setup: `@Entity` mapping, `JpaRepository`, a
  `CommandLineRunner` demo, and when to reach for H2.
- [`auto-configuration-notes.md`](../core/auto-configuration-notes.md) — how
  Spring Boot auto-configures the `EntityManager`/datasource pieces above.