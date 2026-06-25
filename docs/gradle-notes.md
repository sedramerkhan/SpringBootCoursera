# Gradle — Notes

Gradle is a build automation tool — like Maven, it compiles, tests, and packages
the project. The difference is *how* you configure it: a concise **DSL** (Groovy
or Kotlin) instead of Maven's XML, plus speed features that make builds much faster.

## The two build files

| File | Role |
|---|---|
| `build.gradle` | **Configuration** — plugins, dependencies, tasks for a project |
| `settings.gradle` | **Project structure** — the root project name and which sub-projects (modules) make up the build |

A single-module project still has a `settings.gradle` (it names the root project);
multi-module builds list every module there.

## Plugins

Plugins add tasks and conventions. Two common ones for this kind of project:

- **`java`** — Java compilation, the standard `src/main/java` layout, `test`, `jar`.
- **Spring Boot plugin** (`org.springframework.boot`) — bundles dependency
  management and packages an **executable (fat) JAR** containing all dependencies,
  runnable with `java -jar`. Run the app in dev with `./gradlew bootRun`.

## Custom tasks

Because the build script is real code, you can define your own tasks. `doLast`
holds the action that runs when the task executes:

```groovy
task.register('taskName') {
    doLast {
        // action — e.g. println 'Hello from Gradle'
    }
}
```

Run it with `./gradlew taskName`. (`doFirst { }` adds an action that runs *before*
`doLast` — handy for setup.) This programmatic style is one of Gradle's main
advantages over Maven's fixed XML configuration.

## Why Gradle over Maven

- **DSL, not XML** — tasks and config are written in Groovy/Kotlin, so they're
  shorter and you can add custom logic programmatically.
- **Faster builds** — claimed up to ~90% faster than Maven, via:
  - **Incremental builds** — only recompiles files that actually changed.
  - **Build caching** — reuses outputs from previous builds (even across machines).
- **Same project layout** as Maven (`src/main/java`, `src/test/java`), so moving
  between the two is easy.

Trade-off: IDE support for Gradle, while good, is still less mature than Maven's.

## Handy commands

Gradle ships a **wrapper** (`gradlew` / `gradlew.bat`) — run it instead of a
locally installed `gradle`, just like Maven's `mvnw`.

```bash
./gradlew tasks          # list available tasks
./gradlew build          # compile, test, and package
./gradlew test           # run unit tests
./gradlew clean          # delete the build/ output folder
./gradlew bootRun        # run the Spring Boot app (Spring Boot plugin)
./gradlew dependencies   # show the resolved dependency tree
```

## Maven vs Gradle at a glance

| | Maven | Gradle |
|---|---|---|
| Config file | `pom.xml` (XML) | `build.gradle` (Groovy/Kotlin DSL) |
| Structure file | (modules in `pom.xml`) | `settings.gradle` |
| Output folder | `target/` | `build/` |
| Wrapper | `mvnw` / `mvnw.cmd` | `gradlew` / `gradlew.bat` |
| Speed | baseline | incremental builds + build cache (much faster) |

> This project is built with **Maven** (see [`maven-notes.md`](./maven-notes.md));
> these notes are background on the Gradle alternative.