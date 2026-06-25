# Coursera — Spring Boot Learning Project

A practice project for learning Spring Boot, built with Maven and Kotlin/Java.

## Study Notes

Concise references written while working through the course:

- [Maven](docs/maven-notes.md) — the build tool: the POM, dependencies (direct vs
  transitive), plugins/goals, the build lifecycle, and the key `mvn` commands.
- [Gradle](docs/gradle-notes.md) — the Maven alternative: `build.gradle` vs
  `settings.gradle`, the Java/Spring Boot plugins, why its DSL and incremental
  builds are faster, and a Maven-vs-Gradle comparison.
- [Spring vs Spring MVC vs Spring Boot](docs/spring-stack-notes.md) — how the
  three layers relate (core IoC container → web module → auto-configured
  turnkey assembly) and the Starters that bundle them.
- [Spring Beans](docs/beans-notes.md) — what a "bean" is: an object whose
  lifecycle Spring manages and injects for you (IoC).
- [Auto-Configuration](docs/auto-configuration-notes.md) — how Spring Boot
  auto-wires beans, where the configs live, and the `@Conditional...` rules
  that activate them.
- [DispatcherServlet & Request Flow](docs/dispatcher-servlet-notes.md) — how an
  HTTP request travels through the front controller to your controller and back.
- [@Controller vs @RestController & JSON](docs/rest-controller-json-notes.md) —
  view names vs. response bodies, why `@RestController` drops `@ResponseBody`, and
  how Jackson serializes a returned bean/list to JSON automatically.
- [DevTools](docs/devtools-notes.md) — automatic application restart on code or
  property changes during development.
- [Profiles](docs/profiles-notes.md) — environment-specific configuration
  (dev/test/prod) via profile files and `@Profile` beans.
- [@ConfigurationProperties](docs/configuration-properties-notes.md) — binding a
  group of external properties onto a typed config class by prefix.
- [Actuator](docs/actuator-notes.md) — production-ready monitoring endpoints
  (health, metrics, env…) and how to expose them.
- [JSP & View Resolver](docs/jsp-notes.md) — server-rendered HTML views: how a
  controller returns a view name and the resolver's prefix/suffix turn it into a
  JSP file.
- [Spring MVC Forms](docs/spring-mvc-forms-notes.md) — command beans, two-way
  binding with the `<form:…>` tags, and server-side validation (`@Valid`,
  `@Size`, `BindingResult`, `<form:errors>`) — plus the Kotlin specifics.
- [Model & @SessionAttributes](docs/session-attributes-notes.md) — passing data
  controller → view via the request-scoped `Model`, and keeping a value (the
  logged-in name) across requests with `@SessionAttributes` / `@SessionAttribute`.
- [Spring Security](docs/spring-security-notes.md) — authentication and
  authorization with `spring-boot-starter-security`: an in-memory user, BCrypt
  password hashing, URL rules and form login/logout, CSRF tokens, and the
  dispatcher-type fix for JSP forwards.
- [JPA, Spring Data JPA & H2](docs/jpa-h2-notes.md) — storing todos in a database
  with little/no SQL: the JDBC → Spring JDBC → JPA → Spring Data JPA evolution,
  `@Entity` mapping, a `JpaRepository` with derived queries, a `CommandLineRunner`
  that exercises the repo at startup, and when to use the in-memory H2 database
  (learning/tests vs. a real server DB).

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
```

## More

See [HELP.md](HELP.md) for the generated Spring Boot reference links.
