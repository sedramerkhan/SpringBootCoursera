# Spring Boot DevTools — Notes

A development-time dependency that speeds up the edit → run feedback loop.

## What it gives you

Once **Spring Boot DevTools** is on the classpath, it watches your project and
**automatically restarts the application** whenever it detects a change — a
Java/Kotlin class change or a change to a property/config file. You no longer
need to manually stop and restart the server.

## Adding the dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

- `optional`/`runtime` keeps DevTools from leaking into other modules that
  depend on this project.
- DevTools is **automatically disabled** when the app runs from a fully packaged
  jar (i.e. in production), so it only helps during development.

## How the restart works

DevTools uses **two classloaders**:

| Classloader | Loads | Behaviour |
|---|---|---|
| *base* | third-party jars (rarely change) | kept as-is |
| *restart* | your own classes (change often) | thrown away & rebuilt |

Because only your code is reloaded, this **automatic restart is much faster**
than a full cold start.

> Tip: the restart triggers when the compiled output changes. In IntelliJ,
> trigger a **Build** (Ctrl+F9) — or enable "Build project automatically" — so a
> saved file gets compiled and the restart fires.

## Other handy features

- **LiveReload** — bundled server that refreshes the browser automatically when
  a resource changes (needs the LiveReload browser extension).
- **Sensible dev defaults** — disables template/resource caching so edits show
  up immediately.

## Quick mental model

1. Add the `spring-boot-devtools` dependency.
2. Edit a `.java`/`.kt` or property file and let it compile.
3. DevTools detects the changed output and restarts via the *restart*
   classloader — fast, automatic, no manual restart.
4. In a packaged production jar, DevTools switches itself off.