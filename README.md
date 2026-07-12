# Coursera — Java & Spring Boot Learning Notes

A practice project and study-notes repository spanning several Coursera courses,
built with Maven and Kotlin/Java. Notes are concise references written while
working through each course, grouped by course below.

## Study Notes

### Spring Boot & Spring MVC

- [Maven](docs/build-tools/maven-notes.md) — the build tool: the POM, dependencies (direct vs
  transitive), plugins/goals, the build lifecycle, and the key `mvn` commands.
- [Gradle](docs/build-tools/gradle-notes.md) — the Maven alternative: `build.gradle` vs
  `settings.gradle`, the Java/Spring Boot plugins, why its DSL and incremental
  builds are faster, and a Maven-vs-Gradle comparison.
- [Spring vs Spring MVC vs Spring Boot](docs/spring-boot/spring-stack-notes.md) — how the
  three layers relate (core IoC container → web module → auto-configured
  turnkey assembly) and the Starters that bundle them.
- [Spring Beans](docs/spring-boot/beans-notes.md) — what a "bean" is: an object whose
  lifecycle Spring manages and injects for you (IoC) — plus why DI pays off:
  programming to an interface to swap implementations (e.g. a new payment
  method, or cache vs. remote data source) without touching the consumers.
- [Auto-Configuration](docs/spring-boot/auto-configuration-notes.md) — how Spring Boot
  auto-wires beans, where the configs live, and the `@Conditional...` rules
  that activate them.
- [DispatcherServlet & Request Flow](docs/spring-boot/dispatcher-servlet-notes.md) — how an
  HTTP request travels through the front controller to your controller and back.
- [@Controller vs @RestController & JSON](docs/spring-boot/rest-controller-json-notes.md) —
  view names vs. response bodies, why `@RestController` drops `@ResponseBody`, and
  how Jackson serializes a returned bean/list to JSON automatically.
- [DevTools](docs/spring-boot/devtools-notes.md) — automatic application restart on code or
  property changes during development.
- [Profiles](docs/spring-boot/profiles-notes.md) — environment-specific configuration
  (dev/test/prod) via profile files and `@Profile` beans.
- [@ConfigurationProperties](docs/spring-boot/configuration-properties-notes.md) — binding a
  group of external properties onto a typed config class by prefix.
- [Actuator](docs/spring-boot/actuator-notes.md) — production-ready monitoring endpoints
  (health, metrics, env…) and how to expose them.
- [JSP & View Resolver](docs/spring-boot/jsp-notes.md) — server-rendered HTML views: how a
  controller returns a view name and the resolver's prefix/suffix turn it into a
  JSP file.
- [Spring MVC Forms](docs/spring-boot/spring-mvc-forms-notes.md) — command beans, two-way
  binding with the `<form:…>` tags, and server-side validation (`@Valid`,
  `@Size`, `BindingResult`, `<form:errors>`) — plus the Kotlin specifics.
- [Model & @SessionAttributes](docs/spring-boot/session-attributes-notes.md) — passing data
  controller → view via the request-scoped `Model`, and keeping a value (the
  logged-in name) across requests with `@SessionAttributes` / `@SessionAttribute`.
- [Spring Security](docs/spring-boot/spring-security-notes.md) — authentication and
  authorization with `spring-boot-starter-security`: an in-memory user, BCrypt
  password hashing, URL rules and form login/logout, CSRF tokens, and the
  dispatcher-type fix for JSP forwards.
- [JPA, Spring Data JPA & H2](docs/spring-boot/jpa-h2-notes.md) — storing todos in a database
  with little/no SQL: the JDBC → Spring JDBC → JPA → Spring Data JPA evolution,
  `@Entity` mapping, a `JpaRepository` with derived queries, a `CommandLineRunner`
  that exercises the repo at startup, and when to use the in-memory H2 database
  (learning/tests vs. a real server DB).
- [Spring Data REST](docs/spring-boot/spring-data-rest-notes.md) — auto-exposing a repository
  as a REST API (`/api/notes`) with no controller: the `Note` entity +
  `@RepositoryRestResource`, paging/sorting, the one-repository-per-entity gotcha,
  and keeping `TodoRepository` private via `detection-strategy=annotated`.

### Modern API Development

Framework-agnostic notes on API design and operation — the concepts that apply
whether the API is built with Spring Boot, Django REST Framework, or Node.js.
Based on Coursera's
[Modern API Development](https://www.coursera.org/learn/modern-api-development) course:

- [API Fundamentals](docs/api/api-fundamentals-notes.md) — the starting point:
  what an API is (a contract between software), why use one (abstraction, reuse,
  decoupling, interoperability), the client–server request/response model, HTTP
  methods and status codes, and REST basics.
- [API Versioning](docs/api/api-versioning-notes.md) — managing change without
  breaking existing clients: why it matters (backward compatibility, controlled
  evolution), the four strategies (URI/path, query parameter, header, content
  negotiation) with their trade-offs and on-the-wire examples, the choose →
  version-on-breaking-change → document → deploy gradually → deprecate workflow,
  and managing multiple live versions (parallel support, LTS, feature flags,
  deprecation vs sunset policies, and communication best practices).
- [JSON & XML Serialization](docs/api/serialization-notes.md) — turning in-memory
  objects into transferable data and back: the serialize → transfer → deserialize
  flow, JSON (lightweight key–value, weak schema) and XML (verbose tag-based,
  strong schema), and a side-by-side comparison.
- [Robust Data Handling](docs/api/data-handling-notes.md) — seven practices for
  predictable, secure APIs: consistent formats/schemas, server-side validation,
  transparent errors, thoughtful transformation, security (size limits, authn/authz,
  masking), logging/monitoring, and testing under load.
- [Data Transformation](docs/api/data-transformation-notes.md) — reshaping data
  between systems: why it's needed, normalization vs denormalization (OLTP vs
  OLAP), mapping vs format conversion, and the common tool categories (ETL, big
  data, middleware, schema validation).
- [Asynchronous APIs](docs/api/async-apis-notes.md) — non-blocking communication: key
  features, the request → background processing → event notification → response
  flow, WebSockets for real-time bidirectional communication, and message queues
  for decoupling, scalability, and reliable delivery.
- [Caching Strategies](docs/api/caching-notes.md) — why cache, the types by
  location (client, server, reverse proxy, CDN), the four strategies on a
  read/write axis (cache-aside, read-through, write-through, write-back),
  invalidation *mechanisms* (TTL, manual, versioning), and the *goals* they serve
  (freshness, consistency) — with the mechanisms-vs-properties framing.
- [Rate Limiting & Throttling](docs/api/rate-limiting-throttling-notes.md) —
  controlling traffic for stability and fairness: rate-limiting algorithms (fixed
  window, sliding window, token bucket, leaky bucket), throttling strategies
  (request delays, dynamic limits, fair usage), and the "blocks vs slows"
  distinction between the two.
- [Performance Metrics](docs/api/performance-metrics-notes.md) — measuring API
  health: latency vs response time (speed), throughput & error rate (reliability
  under load), availability, uptime & SLA compliance (stability), and the tooling
  to collect/visualize/alert (Actuator + Micrometer → Prometheus → Grafana, or
  cloud-native stacks; plus the metrics/logs/traces observability pillars).
### Java Database Engineering: Spring Boot MVC & SQL Mastery

The current course. All database notes are grouped under
**[docs/database/](docs/database/README.md)** — the hub links the three areas below.

**Module 1 — SQL Primer** ✅ — indexed reference in
**[docs/database/sql/](docs/database/sql/README.md)** (full learning path,
command → note lookup, the five command categories):

- **Foundations** — [Databases & SQL Intro](docs/database/sql/databases-and-sql-intro-notes.md)
- **Defining structure (DDL)** — [DDL: Creating, Altering & Dropping Tables](docs/database/sql/ddl-notes.md) ·
  [Data Types](docs/database/sql/data-types-notes.md) · [Table Constraints](docs/database/sql/table-constraints-notes.md)
- **Manipulating data (DML)** — [DML: Insert / Update / Delete](docs/database/sql/dml-notes.md)
- **Querying (DQL)** — [SELECT / WHERE / ORDER BY / GROUP BY / HAVING / aggregates](docs/database/sql/dql-notes.md)
- **Access control (DCL)** — [GRANT / REVOKE](docs/database/sql/dcl-notes.md)
- **Transactions (TCL)** — [COMMIT / ROLLBACK / SAVEPOINT](docs/database/sql/tcl-notes.md)
- **Functions** — [String Functions](docs/database/sql/string-functions-notes.md)
- **Best practices** — [Best Practices & Pitfalls](docs/database/sql/best-practices-notes.md)

**Functions, Stored Procedures & Triggers** (Oracle PL/SQL) — indexed reference in
**[docs/database/plsql/](docs/database/plsql/README.md)**:

- **Subprograms** — [Functions & Procedures](docs/database/plsql/subprograms-notes.md) —
  local vs stored, header (`RETURN`/`IS`/`AS`), parameter modes, functions
  (incl. recursion) vs procedures (`OUT` params), stored-procedure benefits.
- **Triggers** — [Triggers](docs/database/plsql/triggers-notes.md) — DML/DDL/logon events,
  timing, `:NEW`/`:OLD`, advantages & disadvantages.

**Indexes** (DBMS indexing structures; in progress) — [docs/database/indexing/](docs/database/indexing/README.md):

- **Ordered Indices** — [Ordered Indices](docs/database/indexing/ordered-indices-notes.md) —
  search keys, clustering vs secondary, dense vs sparse, multilevel indices, and
  index updates on insert/delete.
- **B-Tree Indexing** — [B-Tree](docs/database/indexing/b-tree-notes.md) —
  balanced m-way tree, order/properties, B-tree vs B+ tree, search/insert/delete.
- **B+ Tree Indexing** — [B+ Tree](docs/database/indexing/b-plus-tree-notes.md) —
  data in linked leaves, higher fan-out, range scans, insert/delete/update; the
  standard DB index.

## Testing examples

Runnable JUnit 5 and Mockito examples live under
`src/test/java/com/sm/coursera/examples/` (with the classes under test in
`src/main/java/com/sm/coursera/examples/`):

- **JUnit** — [`MyMathTest`](src/test/java/com/sm/coursera/examples/MyMathTest.java):
  `@Test`, assertions (`assertEquals`, `assertTrue`, `assertArrayEquals`), and the
  `@BeforeEach` / `@AfterEach` / `@BeforeAll` / `@AfterAll` lifecycle hooks.
- **Mockito** — [`BusinessLayerTest`](src/test/java/com/sm/coursera/examples/BusinessLayerTest.java):
  mocking a dependency with `mock(...)` and stubbing only the needed method via
  `when(...).thenReturn(...)`, so the class under test is tested without a real database.

Both come with `spring-boot-starter-(webmvc-)test`. Run them with:

```bash
./mvnw test                                   # all tests
./mvnw -Dtest='MyMathTest,BusinessLayerTest' test   # just the examples
```

## Running the app

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080` by default.

### Useful flags

- `--debug` — prints the **auto-configuration report** (positive/negative
  matches), handy for seeing exactly what Spring Boot configured and why.

## Project layout

```
src/main/java/com/sm/coursera/
├── CourseraApplication.java   # @SpringBootApplication entry point
└── CourseController.kt        # REST controller

src/main/resources/
└── application.yaml           # configuration

docs/                          # study notes (see above)
├── spring-boot/               # Spring Boot & Spring MVC concepts
├── build-tools/               # Maven, Gradle
├── api/                       # framework-agnostic API topics
├── database/                  # the DB course: grouped notes
│   ├── sql/                   #   SQL & relational database concepts
│   ├── plsql/                 #   Oracle PL/SQL (procedural SQL)
│   └── indexing/              #   DBMS indexing structures
└── testing/                   # testing notes
```

## More

See [HELP.md](HELP.md) for the generated Spring Boot reference links.
