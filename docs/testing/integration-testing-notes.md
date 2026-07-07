# Integration Testing the REST API — Notes

How we test the `Note` REST API end-to-end, and the JSONAssert framework used to
compare JSON responses. Code: `src/test/java/com/sm/coursera/note/`.

## Integration test vs. unit test

- **Unit test** — exercises *one* unit (a class, or one layer) in isolation, with
  its collaborators mocked. Fast, little or no Spring context. Examples in this
  repo: `examples/BusinessLayerTest` (plain Mockito, no Spring) and
  `todo/TodoControllerTest` (the **web layer** sliced out with `@WebMvcTest`).
- **Integration test** — launches the *whole* application and drives it the way a
  real client would. Slower, but proves the layers (web → Spring Data REST → JPA →
  H2) actually work together. Example: `note/NoteRestRepositoryIT`.

Convention: integration tests are named `…IT` (the suffix the in28minutes course
uses), unit tests `…Test`. Surefire (`mvn test`) runs `…Test`; the `…IT` classes
are for the integration-test phase, so a plain `mvn test` skips them — run an `IT`
explicitly with `-Dtest=NoteRestRepositoryIT`.

## The integration test — `NoteRestRepositoryIT`

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class NoteRestRepositoryIT {

    @LocalServerPort private int port;
    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port).build();
    }

    @Test
    void retrieveSpecificNote_basicScenario() {
        String expected = """
                { "title": "Spring Data REST",
                  "content": "Auto-exposes a repository as a REST API" }
                """;
        client.get().uri("/api/notes/1")
                .exchange()
                .expectStatus().isOk()                                       // 1. status
                .expectHeader().contentType("application/vnd.hal+json")      // 2. content-type
                .expectBody().json(expected, JsonCompareMode.LENIENT);       // 3. body
    }
}
```

Recommended assertion order (matches the course): **status → content-type →
body**. Check the cheap, decisive things first.

What each piece does:

| Piece | Why |
|-------|-----|
| `@SpringBootTest` | Starts the full Spring context. |
| `webEnvironment = RANDOM_PORT` | Real embedded server on a free port (`server.port=0`), so it never clashes with a running app or with CI. |
| `@LocalServerPort int port` | Spring injects the port that was actually chosen. |
| `RestTestClient` | The HTTP client; we point it at `http://localhost:{port}`. |

### Boot 4 note: TestRestTemplate is gone

The course uses **`TestRestTemplate`**. It was **removed in Spring Boot 4.1**. Its
replacement is **`RestTestClient`** (`org.springframework.test.web.servlet.client`,
Spring Framework 7) — a fluent client with response assertions built in, so the
status/header/body checks chain directly off `.exchange()` instead of being
separate JUnit `assertTrue`/`assertEquals` calls. There is no auto-configured,
server-bound `RestTestClient` bean, so we build one against `@LocalServerPort` in
`setUp()`.

Mapping the course's manual asserts onto the fluent chain:

| Course (TestRestTemplate + JUnit) | Here (RestTestClient) |
|-----------------------------------|------------------------|
| `assertTrue(resp.getStatusCode().is2xxSuccessful())` | `.expectStatus().isOk()` |
| `assertEquals("application/json", headers.getContentType()…)` | `.expectHeader().contentType("application/vnd.hal+json")` |
| `JSONAssert.assertEquals(expected, body, false)` | `.expectBody().json(expected, JsonCompareMode.LENIENT)` |

### Two differences from the course's `SurveyResource`

1. **Spring Data REST returns HAL**, not a plain object: the body has `title` +
   `content` *plus* a `_links` block, and **no `id`** (it lives in the self link).
2. **Content-Type is `application/vnd.hal+json`** (the vendor HAL type), not plain
   `application/json`.

Security isn't in the way: `/api/**` is `permitAll()` with CSRF disabled (see
`SpringSecurityConfiguration`), so no login is needed. The row under test is seeded
by `data.sql` (`note` id 1).

### The other tests in the class

`retrieveAllNotes_checksSeededIds` — GET the **collection** `/api/notes`. The HAL
document nests the items under `_embedded.notes` and the `id` is **not** a body
field; it's exposed only via each item's self link. So we assert the ids off the
href:

```java
.expectBody()
.jsonPath("$._embedded.notes.length()").isEqualTo(2)
.jsonPath("$.page.totalElements").isEqualTo(2)
.jsonPath("$._embedded.notes[0]._links.self.href")
    .value(String.class, href -> assertThat(href).endsWith("/api/notes/1"))
.jsonPath("$._embedded.notes[1]._links.self.href")
    .value(String.class, href -> assertThat(href).endsWith("/api/notes/2"));
```

`endsWith` (not `isEqualTo`) keeps the assertion independent of the random port in
the href. `$.page` is the paging metadata Spring Data REST adds to collections.

`createNote_returns201WithLocation` — POST a new note:

```java
URI location = client.post().uri("/api/notes")
        .contentType(MediaType.APPLICATION_JSON)
        .body(newNoteJson)
        .exchange()
        .expectStatus().isCreated()
        .expectHeader().exists("Location")
        .returnResult()                       // ExchangeResult — escape the fluent chain
        .getResponseHeaders().getLocation();  // -> /api/notes/{id}
```

Two things worth knowing:

- **`body(Object)`**, not `bodyValue(...)` — `RestTestClient` is built on the
  blocking `RestClient`, whose request spec uses `body(...)`.
- **POST returns 201 + `Location` but no body** here, so we read the content back
  with a GET on the `Location` and assert `title`/`content` there.

**Test isolation.** With `RANDOM_PORT` a *real* server runs, so the usual
`@Transactional` rollback (which works for `MockMvc` slice tests) does **not** roll
back the POST — the request runs on a different thread/connection. The create test
therefore **deletes the note it created in a `finally` block**, so it always
restores the seeded row count that `retrieveAllNotes_checksSeededIds` depends on,
regardless of test order or a mid-test assertion failure.

## JSONAssert — `JsonAssertTest`

Comparing JSON responses with `assertEquals(expectedString, actualString)` is
painful: a single stray space (text blocks add them) or a reordered field fails
the test, with an unreadable diff. **JSONAssert** compares JSON *semantically*.

`JsonAssertTest` is a **learning test** (no Spring context, hard-coded strings)
that demonstrates the behaviour:

- **Ignores whitespace and field order** — it compares structure, not text.
- **Pinpoints failures** — the error names the exact path, e.g.
  `title Expected: X got: Y`.
- **`strict` flag** — the third argument of `JSONAssert.assertEquals(expected,
  actual, strict)`:
  - `strict = true` → documents must match **exactly** (no extra fields).
  - `strict = false` → assert only a **subset**; extra fields in the actual
    response are ignored. Use this to skip volatile data (timestamps) — and here,
    the port-dependent `_links` block.
- Throws `JSONException` if a string isn't valid JSON (hence `throws JSONException`).

`RestTestClient`'s `.json(expected, JsonCompareMode.LENIENT)` is the same engine:
`LENIENT` == `strict = false`, `STRICT` == `strict = true`.

## Web-layer slice test — `TodoControllerTest`

A *unit* test for the web layer. Where `@SpringBootTest` starts the whole app,
**`@WebMvcTest(TodoController.class)`** starts **only** that controller plus the
MVC infrastructure — no services, no repositories, no database. The controller's
business-layer dependency (`TodoService`) is replaced with a Mockito mock we
program per test.

```java
@WebMvcTest(TodoController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip the security filter chain
class TodoControllerTest {

    private static final Principal PRINCIPAL = () -> "Sedra";

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TodoService todoService;   // the mocked business layer

    @Test
    void listTodos_showsUsersTodos() throws Exception {
        when(todoService.findByUsername("Sedra")).thenReturn(List.of(/* 2 todos */));

        mockMvc.perform(get("/todos").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/listTodos"))
                .andExpect(model().attribute("todos", hasSize(2)));
    }
}
```

Why `TodoController` and not the `Note` API: `@WebMvcTest` is a controller slice,
and it doesn't load Spring Data REST's auto-generated endpoints — there's no
controller class to name and no service to mock. `TodoController` is our only
hand-written `Controller -> Service -> Repository` stack, so it's the real fit for
this pattern.

### Boot 4 changes from the course

- **`@WebMvcTest`** moved to `org.springframework.boot.webmvc.test.autoconfigure`.
- **`@MockBean` was removed.** Its replacement is **`@MockitoBean`**
  (`org.springframework.test.context.bean.override.mockito`). Drop it and the
  slice fails to start with *"No qualifying bean of type 'TodoService'"* — the
  exact failure the course demonstrates, since the controller needs a service the
  slice doesn't otherwise provide.

### Two things this test shows

- **Security is sliced off** with `addFilters = false`, and the authenticated user
  is supplied directly as the request's `Principal` (all this controller reads).
  `MockMvc` doesn't render JSPs, so we assert the **view name** and **model**, not
  HTML.
- **The "unstubbed mock returns null" branch.** `showEditTodo_whenNotFound...`
  deliberately leaves `findById` unstubbed; the mock returns `null`, driving the
  not-found path. This is our analogue of the course's 404 scenario — here the
  controller redirects to `/todos` instead of returning 404.

## Running

```bash
./mvnw -Dtest=NoteRestRepositoryIT test     # integration test (whole app)
./mvnw -Dtest=TodoControllerTest test       # web-layer slice (unit) test
./mvnw -Dtest=JsonAssertTest test           # the JSONAssert learning test
./mvnw test                                 # all unit tests (…Test); skips …IT
```