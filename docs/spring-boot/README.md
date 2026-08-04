# Spring Boot & Spring MVC — Reference Notes

Notes from the Spring Boot / Spring MVC portion of the coursework, grouped into
four areas. Each has its own indexed reference:

- **[Core](core/README.md)** — the IoC container and the machinery around it:
  what a bean is and why DI pays off, how Spring Boot auto-configures beans
  from the classpath, binding external config with `@ConfigurationProperties`,
  environment-specific config via profiles, live-reload with DevTools, and
  production-ready monitoring endpoints via Actuator.
- **[Web / MVC](web/README.md)** — the request/response layer: how a request
  travels through `DispatcherServlet` to a controller and back,
  `@Controller` (views) vs `@RestController` (JSON), server-rendered JSP
  views, two-way form binding + validation, and passing data
  controller → view with `Model`/`@SessionAttributes`.
- **[Persistence](persistence/README.md)** — storing and reading data: the
  ORM concept behind JPA, this project's concrete JPA + Spring Data JPA + H2
  setup, and auto-exposing a repository as a REST API with Spring Data REST.
- **[Security](security/README.md)** — authentication/authorization with
  `spring-boot-starter-security`: an in-memory user, password hashing, URL
  rules, form login/logout, CSRF, and the H2-console/JSP exceptions carved
  out of the default rules.

> These four are siblings so notes cross-link freely (e.g. Persistence →
> Security for the H2-console exceptions, `../security/...`).
