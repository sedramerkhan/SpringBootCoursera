# Spring vs Spring MVC vs Spring Boot — Notes

How the three pieces relate, plus the Starters that wire them together.

## Spring vs Spring MVC vs Spring Boot

| Layer | What it is |
|---|---|
| **Spring (Framework)** | The foundation. Core feature is the **IoC container** with **Dependency Injection** — Spring creates and injects objects for you, giving loose coupling and easy testing. Large and modular, but needs heavy manual configuration. |
| **Spring MVC** | A module within Spring for the **web layer** (web apps + REST APIs). Follows **Model-View-Controller**. Provides `@Controller`, `@RestController`, `@GetMapping`, request routing, etc. Powerful, but traditionally requires lots of setup. |
| **Spring Boot** | Sits on top of the other two and removes the configuration pain. |

Spring Boot removes config pain via:

- **Auto-configuration** — sensible defaults based on what's on the classpath.
- **Starters** — bundled dependencies (e.g. `spring-boot-starter-web`).
- **Embedded server** — Tomcat runs inside the app; ship and run a single JAR.

→ Modern apps use **Spring Boot**; your web annotations are still **Spring MVC**,
running on **Spring's** core container.

**Analogy:** Spring = engine/chassis · Spring MVC = the part handling the road
(web layer) · Spring Boot = assembly line + turnkey ignition.

## Spring Boot Starters

One dependency that bundles all the libraries needed for a task, pre-configured
("**convention over configuration**").

| Starter | Bundles |
|---|---|
| `spring-boot-starter-web` | Web apps & REST APIs — Spring MVC, embedded Tomcat, JSON handling |
| `spring-boot-starter-test` | Testing toolkit — JUnit, Mockito, AssertJ |
| `spring-boot-starter-data-jpa` | Database access via JPA (work with Java objects instead of raw SQL); bundles Hibernate + Spring Data |

See also: [`dispatcher-servlet-notes.md`](./dispatcher-servlet-notes.md) for how
Spring MVC routes a request once it reaches the app, and
[`auto-configuration-notes.md`](./auto-configuration-notes.md) for how the
starters' beans get auto-wired.