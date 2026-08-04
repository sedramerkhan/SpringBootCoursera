# Object-Relational Mapping (ORM) — Notes

The general JPA/Hibernate/ORM reference: what ORM actually *is* and why it
exists, the JDBC → Spring Data JPA evolution, JPA vs. Hibernate, the pieces
you configure to wire ORM up, the layered architecture around it, core
terminology, entity relationships & cascading, field-mapping annotations,
JPQL, and transactions. Everything here applies to any JPA-backed Spring
Boot app, illustrated with both `Todo` (this project) and the course's
`Book`/`Author`/`Category` "Library Management" example. Read this for the
concepts; read [`jpa-h2-notes.md`](./jpa-h2-notes.md) for how they're wired
into this project's concrete `Todo` + H2 setup.

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

See the JDBC → Spring JDBC → JPA → Spring Data JPA progression
[below](#the-evolution-jdbc--spring-jdbc--jpa--spring-data-jpa) — that table
is really "how much of the ORM benefit above do you get at each layer."

## The evolution: JDBC → Spring JDBC → JPA → Spring Data JPA

Each step exists to make you **write less code and less SQL**.

| Approach | What you write | Pain |
|---|---|---|
| **JDBC** | SQL queries **+** tons of Java to execute them (connections, statements, result-set loops) | Thousands of lines of boilerplate just to run one query |
| **Spring JDBC** | SQL queries + a little Java (`jdbcTemplate.update(sql, id)`) | Much less Java, but you **still write every query** |
| **JPA** (e.g. Hibernate) | **No queries** — you map Java objects to tables with annotations; JPA generates the SQL | You still write the repository/`EntityManager` plumbing |
| **Spring Data JPA** | **Almost nothing** — declare an interface; Spring implements CRUD for you | — |

- **JDBC** — `Select * from ...`, open a connection, prepare a statement, loop
  the result set, map columns to fields by hand. Powerful but enormous boilerplate.
- **Spring JDBC** — a Spring module that keeps the SQL but slashes the Java:
  `jdbcTemplate.update("delete from todo where id=?", id)`. Fetching got easier,
  but you still hand-write every query (and most Java devs would rather not).
- **JPA (Java Persistence API)** — a *specification*; **Hibernate** is the most
  popular implementation. You **map** a Java class to a table with annotations
  (`@Entity`, `@Id`, …) and JPA **generates the queries for you**. Code becomes
  "find this entity by this id" — pure Java, no SQL.
- **Spring Data JPA** — makes JPA even simpler. You define an **interface** that
  extends `JpaRepository`; Spring writes the implementation at runtime. Insert,
  read, update, delete — for free, no SQL, no boilerplate.

**Bottom line:** we avoid SQL because it's error-prone boilerplate. JPA removes
the queries (via mapping); Spring Data JPA removes the repository code too.

### Abbreviations

- **JDBC** — **J**ava **D**ata**b**ase **C**onnectivity.
- **JPA** — **J**ava **P**ersistence **A**PI.

## JPA vs Hibernate — the API vs its implementation

**JPA is a specification (an API); Hibernate is the most popular implementation
of it.** Think of JPA as an *interface* and Hibernate as one *class* that
implements it (Toplink/EclipseLink is another).

| | JPA | Hibernate |
|---|---|---|
| **What it is** | A specification / API — defines *how* to persist objects | A framework that *implements* that specification |
| **Provides** | Annotations (`@Entity`, `@Id`, `@Column`), the `EntityManager` API, JPQL | The actual runtime that turns your mapped objects into SQL |
| **Package** | `jakarta.persistence.*` | `org.hibernate.*` |
| **Role** | Says *what* the contract is | Does *the work* behind the contract |

- Both arrive via **one** dependency: `spring-boot-starter-data-jpa` pulls in the
  JPA API jar (`jakarta.persistence-api`) **and** Hibernate (`hibernate-core`).
  Check `mvn dependency:tree` / the IDE's Dependency Hierarchy to see both.
- **Watch the imports.** In this project everything comes from
  `jakarta.persistence.*` — e.g. `@Entity` is `jakarta.persistence.Entity`, not
  `org.hibernate.annotations.Entity`. That means we code against **JPA** and let
  Hibernate be the implementation underneath.
- **Why avoid Hibernate annotations directly?** To not lock into Hibernate. If
  you stick to the JPA API, you could swap the implementation (e.g. to
  EclipseLink/Toplink) without rewriting your entities.

> ⚠️ Some annotations exist in **both** packages (e.g. `@Entity`). When the IDE
> offers `jakarta.persistence.Entity` *and* `org.hibernate.annotations.Entity`,
> pick the `jakarta.persistence` one to stay on the standard API.

**Takeaway:** always code to **JPA** (`jakarta.persistence.*`); use **Hibernate**
as the implementation that runs it.

### What Hibernate adds on top of the JPA spec

JPA defines the contract; Hibernate is what actually makes it convenient day
to day:

- **Automatic table generation** — `@Entity` classes become tables at startup
  (see the `Todo` example in
  [`jpa-h2-notes.md`](./jpa-h2-notes.md#1-the-entity--map-a-class-to-a-table-jpa));
  no `CREATE TABLE` to write by hand.
- **Transparent persistence** — mutate a managed entity's fields and
  Hibernate tracks the change; no manual `UPDATE` call needed for objects
  it's already tracking.
- **Caching** — repeated lookups of the same entity can be served from
  Hibernate's session/second-level cache instead of hitting the DB again.
- **Lazy loading** — related entities (e.g. a `Book`'s `Author`) are only
  fetched from the DB the moment code actually accesses them, not eagerly
  with the parent.

## Configuring ORM with JPA

Five pieces to configure, in order:

1. **Dependencies** — add a JPA provider to the project (e.g. Hibernate via
   `spring-boot-starter-data-jpa`, or EclipseLink).
2. **Entity classes** — plain Java/Kotlin classes annotated `@Entity`, each
   representing one database table (fields → columns, `@Id` → primary key).
   See [Core ORM terminology](#core-orm-terminology) and
   [Entity Relationships & Cascading](#entity-relationships--cascading)
   below for the annotations involved.
3. **Persistence unit** — in plain (non-Spring) JPA, this is declared in
   `META-INF/persistence.xml`: it names the unit, lists its entity classes,
   and configures the datasource connection, SQL dialect, and other
   provider properties.
4. **`EntityManager`** — the runtime object you use to talk to the database:
   `persist()` (insert), `find()` (lookup by id), `createQuery()` (JPQL —
   see [below](#jpql--querying-entities-not-tables)). This is the raw-JPA
   equivalent of what a `JpaRepository` gives you for free in Spring Data
   JPA.
5. **Transaction management** — database operations are wrapped in
   transactions so they commit or roll back atomically — see
   [Transactions](#transactions--transactional) below for the full picture.

### Plain JPA vs. Spring Boot

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

## The layered architecture around JPA

A typical Spring Boot + JPA app — this project, or the course's Library
Management example (`Book`/`Author`/`Category`) — is built from four layers,
each with one job:

| Layer | Role | Example |
|---|---|---|
| **Entity** | Represents a database table | `Todo`; or `Book`, `Author`, `Category` |
| **Repository** | CRUD + queries against the entity | `TodoRepository : JpaRepository<Todo, Int>` |
| **Service** | Business logic — what a request *does* | issuing a book, marking a todo done |
| **Controller** | Handles HTTP requests/responses, delegates to the service | `TodoController`; `BookController` |

**Request flow: Controller → Service → Repository → Entity/database.** The
controller calls the service; the service holds the business logic and calls
the repository; the repository is the only layer that touches the database
directly. Keeping the service in between means business logic lives in one
place instead of being scattered across controllers, and the repository
stays a thin, single-purpose data-access layer.

> This project currently calls `TodoRepository` straight from the
> controller — fine while the logic is plain CRUD. A service layer becomes
> worth adding once real business logic (validation, multi-step operations,
> `@Transactional` boundaries) shows up.

## Core ORM terminology

| Term | Meaning |
|---|---|
| **Entity** | A class mapped to a table via `@Entity` (see above). |
| **Primary key** | The entity's unique identifier, marked `@Id`. |
| **Generated value** | A primary key the DB assigns automatically, via `@GeneratedValue` (e.g. auto-increment) — [strategies below](#primary-key-generation--generatedvalue-strategies). |
| **Relationship** | How entities reference each other — `@OneToMany`, `@ManyToOne`, `@ManyToMany`, etc. (e.g. a `Book` → its `Author`) — [below](#entity-relationships--cascading). |
| **JPQL** | Java Persistence Query Language — queries written against **entities and their fields**, not tables and columns (below). |
| **Transaction** | A sequence of operations treated as one atomic unit — commit together or roll back together (below). |
| **`EntityManager`** | The JPA API for persisting/finding/querying entities directly (`persist`, `find`, `createQuery`) — what a `JpaRepository` implements on your behalf. |

## Primary Key Generation — `@GeneratedValue` Strategies

`@GeneratedValue(strategy = ...)` controls **how** the primary key value
gets produced. Four `GenerationType` options:

| Strategy | How the key is generated | Typical use |
|---|---|---|
| `AUTO` | JPA picks a strategy for you, based on the DB dialect — the default if `strategy` is omitted. | Quick prototyping — let the provider decide. |
| `IDENTITY` | Delegates to the database's own auto-increment/identity column. | Simplest, most common — H2/MySQL/PostgreSQL identity columns (what this project uses for `Todo.id`). |
| `SEQUENCE` | Reads the next value from a separate DB **sequence** object. | Databases with native sequence support (Oracle, PostgreSQL) — lets Hibernate pre-allocate batches of ids for faster inserts. |
| `TABLE` | Simulates a sequence using an ordinary backing table of counters. | Portable across any DB with no native sequence/identity support — slowest option, rarely the first choice. |

```kotlin
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
var id: Long = 0                                    // DB auto-increment column

@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_seq")
@SequenceGenerator(name = "book_seq", sequenceName = "book_sequence", allocationSize = 1)
var id: Long = 0                                    // reads from a DB sequence

@Id @GeneratedValue(strategy = GenerationType.TABLE)
var id: Long = 0                                    // reads from a backing "counters" table

@Id @GeneratedValue(strategy = GenerationType.AUTO)
var id: Long = 0                                    // JPA/Hibernate picks for you
```

- **`IDENTITY` is what this project uses** for `Todo.id` (see
  [`jpa-h2-notes.md`](./jpa-h2-notes.md#1-the-entity--map-a-class-to-a-table-jpa)) —
  simplest to reason about, but it prevents Hibernate from batching inserts:
  each insert needs the DB to hand back the generated id immediately.
- **`SEQUENCE`** is generally preferred over `IDENTITY` on databases that
  support it, precisely because Hibernate *can* pre-fetch/batch ids instead
  of round-tripping to the DB on every single insert.
- Omitting `strategy` entirely defaults to `GenerationType.AUTO`.

## Entity Relationships & Cascading

### Relationship annotations

Entity classes link to each other with relationship annotations, mirroring
foreign-key relationships in the database and controlling how related data
is joined and fetched:

| Annotation | Relationship | Example |
|---|---|---|
| `@OneToOne` | one row ↔ exactly one row | `Book` ↔ `BookDetail` |
| `@OneToMany` | one row → many rows | `Author` → its `List<Book>` |
| `@ManyToOne` | many rows → one row | `Book` → its `Author` |
| `@ManyToMany` | many rows ↔ many rows (via a join table) | `Book` ↔ `Category` |

```kotlin
@Entity
class Author(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String = "",

    @OneToMany(mappedBy = "author")               // "one" side — no FK column here
    var books: MutableList<Book> = mutableListOf(),
)

@Entity
class Book(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var title: String = "",

    @ManyToOne                                     // "many" side — owns the FK
    @JoinColumn(name = "author_id")
    var author: Author? = null,

    @ManyToMany
    @JoinTable(
        name = "book_category",                    // the join table
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")],
    )
    var categories: MutableList<Category> = mutableListOf(),
)
```

- **Owning side vs. inverse side.** The side with `@JoinColumn`/`@JoinTable`
  (here, `Book`) owns the foreign key and is what Hibernate actually writes
  to the DB. The other side (`Author.books`) uses `mappedBy` to say "I'm just
  the mirror image of `Book.author`" — it doesn't create its own column.
- **Fetch type defaults** matter for the [lazy loading](#what-hibernate-adds-on-top-of-the-jpa-spec)
  mentioned earlier: `@OneToMany`/`@ManyToMany` default to `FetchType.LAZY`
  (don't load the collection until accessed); `@ManyToOne`/`@OneToOne`
  default to `FetchType.EAGER` (load immediately). Override with
  `@ManyToOne(fetch = FetchType.LAZY)` etc. when the default doesn't fit.

### Cascading operations

**Cascading** propagates an operation performed on the parent entity to its
related entities, via the `cascade` attribute on the relationship
annotation — so saving/deleting the parent doesn't require saving/deleting
every related entity by hand:

```kotlin
@Entity
class Author(
    // ...
    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL])
    var books: MutableList<Book> = mutableListOf(),
)
```

With `cascade = [CascadeType.ALL]`, `authorRepository.save(author)` also
persists every `Book` in `author.books` — no separate `bookRepository.save()`
call per book.

| `CascadeType` | Propagates... |
|---|---|
| `PERSIST` | saving the parent → saves new children |
| `MERGE` | updating the parent → updates children |
| `REMOVE` | deleting the parent → deletes children |
| `REFRESH` | reloading the parent from the DB → reloads children |
| `DETACH` | detaching the parent from the persistence context → detaches children |
| `ALL` | all of the above |

- Stick to `jakarta.persistence.CascadeType` (JPA) rather than
  `org.hibernate.annotations.Cascade` (Hibernate's own, slightly larger enum)
  — same "code to JPA, not Hibernate" reasoning as
  [JPA vs Hibernate](#jpa-vs-hibernate--the-api-vs-its-implementation) above.
- **Be careful with `CascadeType.REMOVE` on `@ManyToMany`.** Deleting a
  `Book` shouldn't delete `Category` rows other books still reference —
  cascade removal only makes sense when the child is truly *owned by* the
  parent (e.g. an `Author` deleting their `Book`s), not when it's a shared
  lookup entity.

### Other field-mapping annotations worth knowing

Beyond `@Id`/`@GeneratedValue` and the relationship annotations above, these
cover most of what comes up mapping individual fields:

| Annotation | Purpose |
|---|---|
| `@Table` | Override the table name/schema (default: the class name) — also where `uniqueConstraints`/`indexes` go. |
| `@Column` | Override a column's name/length/`nullable`/`unique` (default: the field name). |
| `@Transient` | The field exists on the Java object but is **not persisted** — for derived/computed values. |
| `@Temporal` | Sets the DB precision (`DATE`/`TIME`/`TIMESTAMP`) for a legacy `java.util.Date`/`Calendar` field. |
| `@Lob` | Maps the field to a large-object column — `CLOB` for a `String`, `BLOB` for a `byte[]`. |
| `@Enumerated` | Stores a Java `enum` as `STRING` or `ORDINAL`. |
| `@Embeddable` / `@Embedded` | Inlines a reusable value-object's fields as columns on the owning table, instead of a separate table + foreign key. |
| `@Version` | Optimistic locking — an auto-checked version column that prevents silently overwriting a concurrent update. |

```kotlin
@Entity
@Table(name = "books", uniqueConstraints = [UniqueConstraint(columnNames = ["isbn"])])
class Book(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(name = "title", nullable = false, length = 200)
    var title: String = "",

    @Enumerated(EnumType.STRING)                  // stores "AVAILABLE", not 0
    var status: BookStatus = BookStatus.AVAILABLE,

    @Lob
    var coverImage: ByteArray = ByteArray(0),      // -> BLOB column

    @Transient
    var isOverdueDisplay: Boolean = false,         // computed in the service layer, never saved

    @Version
    var version: Int = 0,                          // optimistic locking
)
```

- **Why `@Temporal` is needed:** without it, JPA maps a `java.util.Date`/
  `Calendar` field to SQL `TIMESTAMP` by default — storing the full date
  **and** time even if the field only conceptually needs one of them (e.g. an
  event's date, with no meaningful time component). That wastes storage and
  can confuse anyone querying the column expecting just a date. `@Temporal`
  lets you say explicitly which precision you actually want:

  ```kotlin
  @Temporal(TemporalType.DATE)       // store only the date — no time part
  var eventDate: Date = Date()

  @Temporal(TemporalType.TIME)       // store only the time — no date part
  var eventTime: Date = Date()

  @Temporal(TemporalType.TIMESTAMP)  // store both (the JPA default without @Temporal)
  var eventDateTime: Date = Date()
  ```
- **`@Temporal` note:** modern `java.time` types (`LocalDate`,
  `LocalDateTime`, `Instant`) — like `Todo.targetDate` in
  [`jpa-h2-notes.md`](./jpa-h2-notes.md#1-the-entity--map-a-class-to-a-table-jpa) —
  map to the right SQL precision automatically without any annotation, since
  the Java type itself already says whether it's date-only, time-only, or
  both. `@Temporal` is only needed on the legacy `java.util.Date`/`Calendar`
  types shown above.
- **`@Enumerated(EnumType.STRING)` over the default `ORDINAL`:** `ORDINAL`
  stores the enum's position (`0`, `1`, `2`, …), which silently breaks if
  someone reorders the enum's constants later. `STRING` stores the constant's
  name — safer, slightly more storage.
- **`@Column` is optional** as long as the defaults fit (field name → column
  name); reach for it to rename, constrain, or resize a column explicitly.

## JPQL — querying entities, not tables

**JPQL** looks like SQL but operates on **entity names and fields**, not
table and column names. Spring Data JPA runs a JPQL query for you when you
annotate a repository method with `@Query`, binding named parameters with
`@Param`:

```kotlin
interface BookRepository : JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.title = :title")
    fun findBookByTitle(@Param("title") title: String): Book?
}
```

- `@Query` marks the JPQL string to run instead of a derived query.
- `SELECT b FROM Book b` selects the **`Book` entity**, not a `book` table —
  JPQL is written against your Java class name.
- `:title` is a named parameter; `@Param("title")` binds the method argument
  to it.
- Reach for `@Query`/JPQL when the query can't be expressed as a
  [derived query](./jpa-h2-notes.md#2-the-repository--crud-for-free-spring-data-jpa)
  (method name parsing) or needs to join/filter/aggregate across
  relationships.

## Transactions — `@Transactional`

A **transaction** groups a sequence of operations into a single unit of
work: either everything commits, or (on an unhandled exception) everything
rolls back — no partial writes. `@Transactional` marks the method boundary:

```kotlin
@Transactional
fun saveBook(book: Book): Book {
    return bookRepository.save(book)
}
```

- Put `@Transactional` on **service** methods, especially ones that do more
  than one write (e.g. save a book *and* update a category's book count) —
  that's the case a single repository call alone doesn't protect.
- A single `JpaRepository.save()`/`findById()` call is already transactional
  on its own under the hood; `@Transactional` matters once a method
  represents *multiple* related operations that must succeed or fail
  together.

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
