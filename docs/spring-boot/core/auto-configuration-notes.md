# Spring Boot Auto-Configuration — Notes

A short reference on how Spring Boot configures your app for you.

## What it is

Auto-configuration is Spring Boot's mechanism for **automatically creating
beans** based on what's on the classpath, what beans already exist, and what
properties are set — so you don't have to declare them manually.

Triggered by `@SpringBootApplication`, which bundles:
- `@SpringBootConfiguration`
- `@ComponentScan`
- **`@EnableAutoConfiguration`** ← turns auto-configuration on

## Where the configurations live

Auto-configuration classes ship inside the `spring-boot-autoconfigure.jar`
dependency (pulled in transitively by the starters). Module-specific ones now
also live in their own jars, e.g.:

- `spring-boot-autoconfigure.jar` — core auto-configs
- `spring-boot-webmvc.jar` — web MVC auto-configs (Spring Boot 4.x)

Examples seen in the course:
- `DispatcherServletAutoConfiguration` — configures the front-controller
  `DispatcherServlet` that routes every incoming request.
- `ErrorMvcAutoConfiguration` — configures default error handling
  (e.g. the "Whitelabel Error Page").

## How a class gets discovered

Spring Boot reads the list of candidate auto-configuration classes from:

```
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

(inside the jar). Each listed class is a `@AutoConfiguration` /
`@Configuration` class that only applies when its conditions match.

## Conditional activation

Auto-config beans are guarded by `@Conditional...` annotations, so they only
kick in when appropriate:

| Annotation | Applies the config when... |
|---|---|
| `@ConditionalOnClass` | a given class is on the classpath |
| `@ConditionalOnMissingClass` | a class is NOT on the classpath |
| `@ConditionalOnBean` | a given bean already exists |
| `@ConditionalOnMissingBean` | no such bean is defined (lets you override defaults) |
| `@ConditionalOnProperty` | a property has a specific value |
| `@ConditionalOnWebApplication` | running as a web app |

**Key idea:** `@ConditionalOnMissingBean` means *your* bean wins. If you define
your own, Spring Boot backs off and skips its default.

## Inspecting what got configured

- Run with `--debug` (or set `debug=true` in `application.properties`) to print
  the **Auto-configuration report** at startup: a list of "Positive matches"
  (applied) and "Negative matches" (skipped, with the reason each was skipped).
- Hit the **`/actuator/conditions`** endpoint for the same report as JSON, live,
  without restarting in debug mode. It groups beans into `positiveMatches` and
  `negativeMatches` with the condition that decided each. Requires the
  `spring-boot-starter-actuator` dependency and exposing the endpoint, e.g.:

  ```properties
  management.endpoints.web.exposure.include=health,conditions
  ```

  Then browse `http://localhost:8080/actuator/conditions`. (See
  [`actuator-notes.md`](./actuator-notes.md).)
- In the IDE, use **Ctrl+Shift+T** (Find/Navigate to Class) to open a class
  like `DispatcherServletAutoConfiguration` and see which jar it comes from.

## Overriding defaults with properties

Auto-configuration sets **sensible defaults**; `application.properties` is how
you explicitly override them without writing any bean. The property you set wins
over the auto-configured default. A few common examples:

| Property | Overrides the default... |
|---|---|
| `server.port=8081` | embedded server port (default `8080`) |
| `spring.jpa.hibernate.ddl-auto=update` | schema generation strategy |
| `spring.datasource.url=...` | auto-configured DataSource connection |
| `logging.level.org.springframework=DEBUG` | default log level |

Two ways to override an auto-config default:
1. **Set a property** — for anything the auto-config exposes as a property
   (the common case).
2. **Declare your own bean** — the auto-config backs off via
   `@ConditionalOnMissingBean` (see above) when a knob isn't exposed as a property.

## Quick mental model

1. You add a starter (e.g. `spring-boot-starter-web`).
2. Its jars land on the classpath.
3. `@EnableAutoConfiguration` reads the `.imports` files.
4. Each candidate config checks its `@Conditional` rules.
5. Matching configs create sensible default beans.
6. You override any default just by declaring your own bean or property.
