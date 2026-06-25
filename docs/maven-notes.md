# Maven — Notes

Maven is the build tool that compiles the project, manages its dependencies, runs
its tests, and packages it — all driven by one file, the **POM**.

## The POM (`pom.xml`)

The **Project Object Model**: a single XML file describing the project. It holds:

- **Metadata** — `groupId`, `artifactId`, `version` (the project's coordinates).
- **Dependencies** — the external libraries the project needs.
- **Build configuration** — plugins and settings that control how it's built.

## Dependencies

Declared in the `<dependencies>` section; Maven downloads them (and their own
dependencies) from a repository so you don't manage JARs by hand.

- **Transitive dependencies** — a library you declare pulls in the libraries *it*
  needs, automatically.
- **Direct beats transitive** — if you declare a version directly, it takes
  precedence over a version pulled in transitively.
- `mvn dependency:tree` shows the full resolved tree and *why* each JAR is present.

## Plugins

Maven itself does little; **plugins** do the work. Each plugin exposes **goals**
(e.g. `compiler:compile`, `surefire:test`, `jar:jar`) that handle tasks like
compiling, testing, packaging, and code generation. Plugins **bind to lifecycle
phases**, so running a phase runs the goals bound to it.

## Build lifecycle

Phases run in order — running one runs every phase before it.

| Phase / command | What it does |
|---|---|
| `mvn validate` | Checks the project is correct and complete |
| `mvn compile` | Compiles **source** code only |
| `mvn test-compile` | Compiles **source + test** code |
| `mvn test` | Runs unit tests (compiles source + tests first) |
| `mvn package` | Bundles compiled code into a **JAR** (in `target/`) |
| `mvn verify` | Runs integration tests / checks on the package |
| `mvn install` | Installs the artifact into the **local** repository |

`mvn clean` is separate: it deletes the `target/` folder. Commonly combined:
`mvn clean package`.

## Handy commands

```bash
mvn --version            # Maven version, Java version, system details
mvn compile              # compile source only
mvn test-compile         # compile source + test code
mvn test                 # run unit tests
mvn package              # build the JAR into target/
mvn clean                # delete target/ (build artifacts)
mvn help:effective-pom   # the fully-resolved POM (inherited + default config)
mvn dependency:tree      # how dependencies resolve, incl. transitive ones
```

> This project ships the **Maven Wrapper** (`mvnw` / `mvnw.cmd`), so you can run
> `./mvnw …` (or `mvnw.cmd …` on Windows) without installing Maven — it downloads
> the pinned Maven version for you.

## In this project

- `pom.xml` — coordinates (`com.sm` / `coursera`), Spring Boot + Kotlin
  dependencies, and the build plugins (Spring Boot, Kotlin compiler with the
  `no-arg`/`all-open` plugins for JPA entities).
- `mvnw`, `mvnw.cmd`, `.mvn/` — the Maven Wrapper.
- `target/` — build output (compiled classes, the packaged JAR); safe to delete
  with `mvn clean`.