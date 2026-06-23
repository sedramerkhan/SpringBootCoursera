# JPA, Spring Data JPA & H2 — Notes

How the app stores todos in a database with almost no SQL — and why we reach for
each piece. The todo list moved from an in-memory `MutableList` to a real
database table, but the controller and JSPs didn't change.

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

## How it looks in this project

### 1. The entity — map a class to a table (JPA)

```kotlin
@Entity                                   // this class IS a table ("todo")
class Todo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,                      // primary key, assigned by the DB
    var username: String = "",
    var description: String = "",
    var targetDate: LocalDate = LocalDate.now(),   // -> column target_date
    var done: Boolean = false,
)
```

That's the whole "mapping." Hibernate creates the `todo` table from it on startup
and translates objects ⇄ rows. (`id = 0` is treated as "new" → INSERT; a non-zero
id → UPDATE.)

> Kotlin note: JPA needs a no-arg constructor and a non-final class. The
> `kotlin-maven-noarg` (jpa) and `kotlin-maven-allopen` compiler plugins (see
> `pom.xml`) provide both for `@Entity` classes.

### 2. The repository — CRUD for free (Spring Data JPA)

```kotlin
interface TodoRepository : JpaRepository<Todo, Int> {
    fun findByUsernameIgnoreCase(username: String): List<Todo>          // derived query
    fun findByIdAndUsernameIgnoreCase(id: Int, username: String): Todo?
}
```

- Extending `JpaRepository<Todo, Int>` gives `save`, `findById`, `findAll`,
  `deleteById`, etc. — **no implementation written**.
- **Derived queries**: Spring parses the *method name* (`findBy` + `Username` +
  `IgnoreCase`) and generates the SQL. No `@Query`, no JPQL.

The repository is the only new layer the storage swap required — the controller
and JSPs needed **zero** changes.

## What is H2, and when should I use it?

**H2** is a tiny SQL database that can run **in-memory** (inside your app's JVM).
It is the *database*; JPA/Hibernate is the *layer that talks to it*. They're
independent — Hibernate can talk to H2, MySQL, PostgreSQL, etc. without your code
changing.

We configured H2 **in-memory** (`jdbc:h2:mem:tododb`): the schema is built from
`@Entity` on startup, seeded from `data.sql`, and **everything is wiped on
shutdown**.

**Use H2 (in-memory) when:**
- **Learning / prototyping** — zero install, zero setup, starts instantly.
- **Automated tests** — each run gets a clean, isolated database; nothing to
  clean up afterwards.
- **Local development** — you want a real SQL database to develop against without
  running a separate DB server.

**Do NOT use in-memory H2 when:**
- **You need the data to survive a restart** — it's gone every time. (H2 *can*
  persist to a file with `jdbc:h2:file:./data/tododb`, but even then it's a
  single-app embedded DB.)
- **Production / multiple app instances / real load** — use a server database
  (PostgreSQL, MySQL, …). Because we use JPA, switching is mostly a dependency +
  a few `application.properties` lines; the entity and repository code stay the
  same.

Rule of thumb: **H2 in-memory for learning and tests; a real server DB for
anything whose data must outlive the process.**

### The H2 web console

`spring.h2.console.enabled=true` exposes a browser UI at
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:tododb`, user `sa`,
empty password) to run SQL and inspect tables. It's a **dev-only** convenience —
keep it disabled in production. Spring Security had to be told to permit it,
disable CSRF for it, and allow same-origin frames (see
[`spring-security-notes.md`](./spring-security-notes.md)).

## Startup wiring gotcha

`data.sql` must run **after** Hibernate creates the schema, or the inserts fail
("table TODO not found"). That's what
`spring.jpa.defer-datasource-initialization=true` does. We also omit the `id`
column in `data.sql` so the auto-increment counter keeps advancing and later
app-created todos don't collide on the primary key.

## In this project

- `Todo` → `@Entity` mapped to the `todo` table.
- `TodoRepository` → Spring Data JPA interface (derived queries, no SQL).
- `application.properties` + `data.sql` → in-memory H2, schema auto-created,
  three seed rows.

See also: [`spring-security-notes.md`](./spring-security-notes.md) for the H2
console security exceptions.
