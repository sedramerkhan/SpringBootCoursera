# Persistence — Reference Notes

Storing and reading data: the ORM concept behind JPA, this project's concrete
JPA + Spring Data JPA + H2 setup, and auto-exposing a repository as a REST
API with no controller. For the IoC container that wires the
`EntityManager`/datasource beans see [../core/](../core/README.md).

## Notes

- [Object-Relational Mapping (ORM)](orm-notes.md) — the concept behind JPA:
  the object-relational impedance mismatch ORM bridges, its benefits (less
  boilerplate, portability, maintainability, OO approach), the five
  configuration pieces (dependencies, `@Entity` classes, persistence unit,
  `EntityManager`, transactions), plain JPA vs. what Spring Boot
  auto-configures, and trade-offs (N+1, when to still drop to SQL).
- [JPA, Spring Data JPA & H2](jpa-h2-notes.md) — storing todos in a database
  with little/no SQL: the JDBC → Spring JDBC → JPA → Spring Data JPA
  evolution, `@Entity` mapping, a `JpaRepository` with derived queries, a
  `CommandLineRunner` that exercises the repo at startup, and when to use
  the in-memory H2 database (learning/tests vs. a real server DB).
- [Spring Data REST](spring-data-rest-notes.md) — auto-exposing a repository
  as a REST API (`/api/notes`) with no controller: the `Note` entity +
  `@RepositoryRestResource`, paging/sorting, the one-repository-per-entity
  gotcha, and keeping `TodoRepository` private via
  `detection-strategy=annotated`.
