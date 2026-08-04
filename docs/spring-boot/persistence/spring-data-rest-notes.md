# Spring Data REST — Notes

Spring Data REST auto-exposes a Spring Data repository as a **REST API** — full
CRUD plus paging and sorting — without writing a single `@RestController`. If you
just need a quick REST API over your data, it's an alternative to hand-writing
controllers.

## Why use it

- **No boilerplate** — declare a repository interface; the HTTP endpoints appear.
- **Paging, sorting, search for free** — `?page`, `?size`, `?sort`, and exported
  derived-query methods under `/search`.
- **HATEOAS/HAL output** — responses include `_links` and `_embedded`, so clients
  can navigate the API.

Trade-off: you give up fine-grained control over the URLs and payloads. For
anything with real business rules, a hand-written `@RestController` is clearer.

## How it's wired in this project

The demo uses a **dedicated `Note` entity** with its own repository:

```kotlin
@Entity
class Note(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
)

@RepositoryRestResource(path = "notes", collectionResourceRel = "notes")
interface NoteRestRepository :
    PagingAndSortingRepository<Note, Int>,
    CrudRepository<Note, Int>
```

That's all. With `spring.data.rest.base-path=/api`, you get:

| Method | URL | Result |
|---|---|---|
| GET | `/api/notes` | list (paginated) — `?page=0&size=2&sort=title,desc` |
| GET | `/api/notes/{id}` | one note |
| POST | `/api/notes` | create → **201 Created** + `Location` header |
| PUT | `/api/notes/{id}` | replace |
| PATCH | `/api/notes/{id}` | partial update |
| DELETE | `/api/notes/{id}` | delete |

- `PagingAndSortingRepository` adds the paging/sorting query params; `CrudRepository`
  adds save/find/delete. In Spring Data 3.x these are separate interfaces, so the
  repository extends **both** (`JpaRepository` would also work — it includes both).
- `@RepositoryRestResource(path = "notes")` sets the URL segment (the default is the
  pluralised entity name).
- Seed rows live in `data.sql` (`INSERT INTO note ...`).

## The key gotcha: one repository per entity

Spring Data REST maps **exactly one repository per entity**. This app already has
`TodoRepository` bound to the `Todo` entity (it backs the MVC web app). Adding a
**second** repository for the *same* `Todo` entity does **not** export reliably —
which repository "wins" depends on classpath/scan order, so the resource
intermittently 404s.

That's why the REST demo uses a **separate `Note` entity** instead of reusing
`Todo`: one entity, one repository, no ambiguity, and `TodoRepository` is left
completely untouched.

## Keeping `TodoRepository` private

By default Spring Data REST exposes **every** public repository — which would also
expose `TodoRepository` at `/api/todos`. To prevent that without editing it:

```properties
spring.data.rest.detection-strategy=annotated
```

`annotated` means "only export repositories explicitly annotated with
`@RepositoryRestResource`." `NoteRestRepository` is annotated (exported);
`TodoRepository` is not (stays private). Verified: `GET /api/notes` → 200,
`GET /api/todos` → 404.

## Two things that bit us (Spring Boot 4 specifics)

- **POST needs the Jackson-Kotlin module.** Spring Boot 4 ships **Jackson 3**
  (`tools.jackson`), so the dependency is `tools.jackson.module:jackson-module-kotlin`
  (not the old `com.fasterxml` one). Without it, posting JSON that omits a Kotlin
  primitive (like `id`) fails with *"Cannot map null into type int"*.
- **Security.** Endpoints sit behind Spring Security, so `/api/**` is permitted and
  CSRF-exempted for this dev demo (see `SpringSecurityConfiguration`). In a real app
  you'd secure these instead of opening them.

## DevTools warning

With `spring-boot-devtools` active, a partial restart can leave Spring Data REST in
a half-initialised state where endpoints 404. If a URL that should work doesn't,
**fully stop and restart the app** (a plain rebuild/DevTools reload isn't always
enough).

## In this project

- `note/Note.kt` — the demo `@Entity`.
- `note/NoteRestRepository.kt` — the exported Spring Data REST repository (`/api/notes`).
- `application.properties` — `base-path=/api`, `default-page-size`, `detection-strategy=annotated`.
- `data.sql` — seed notes.
- `pom.xml` — `spring-boot-starter-data-rest` + `tools.jackson.module:jackson-module-kotlin`.
- `TodoRepository` — unchanged; stays private to the MVC app.

See also [`jpa-h2-notes.md`](./jpa-h2-notes.md) for the JPA/repository basics.