# Persistence — Reference Notes

Storing and reading data: the ORM concept behind JPA, this project's concrete
JPA + Spring Data JPA + H2 setup, and auto-exposing a repository as a REST
API with no controller. For the IoC container that wires the
`EntityManager`/datasource beans see [../core/](../core/README.md).

## Notes

- [Object-Relational Mapping (ORM)](orm-notes.md) — the general
  JPA/Hibernate/ORM reference: what ORM is and why it exists, the JDBC →
  Spring JDBC → JPA → Spring Data JPA evolution, JPA vs. Hibernate (+ what
  Hibernate adds), the five configuration pieces and the layered
  Controller → Service → Repository → Entity architecture, core ORM
  terminology, primary key generation strategies
  (`AUTO`/`IDENTITY`/`SEQUENCE`/`TABLE`), entity relationships
  (`@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`) and cascading
  (`CascadeType`), other field-mapping annotations (`@Table`, `@Column`,
  `@Transient`, `@Temporal`, `@Lob`, `@Enumerated`, `@Embeddable`/`@Embedded`,
  `@Version`), JPQL (`@Query`/`@Param`), transactions (`@Transactional`),
  and trade-offs (N+1, when to still drop to SQL).
- [JPA, Spring Data JPA & H2](jpa-h2-notes.md) — this project's concrete
  setup built on those concepts: storing todos in a database with
  little/no SQL, `@Entity` mapping, a `JpaRepository` with derived queries,
  a `CommandLineRunner` that exercises the repo at startup, and when to use
  the in-memory H2 database (learning/tests vs. a real server DB).
- [Spring Data REST](spring-data-rest-notes.md) — auto-exposing a repository
  as a REST API (`/api/notes`) with no controller: the `Note` entity +
  `@RepositoryRestResource`, paging/sorting, the one-repository-per-entity
  gotcha, and keeping `TodoRepository` private via
  `detection-strategy=annotated`.
