# Spring Core / IoC — Reference Notes

The IoC container and the machinery Spring Boot builds around it: what a bean
is, how auto-configuration wires beans for you, and the operational knobs
(config binding, profiles, DevTools, Actuator) built on top. For the
request-handling layer see [../web/](../web/README.md).

## Notes

- [Spring vs Spring MVC vs Spring Boot](spring-stack-notes.md) — how the
  three layers relate (core IoC container → web module → auto-configured
  turnkey assembly) and the Starters that bundle them.
- [Spring Beans](beans-notes.md) — what a "bean" is: an object whose
  lifecycle Spring manages and injects for you (IoC) — plus why DI pays off:
  programming to an interface to swap implementations (e.g. a new payment
  method, or cache vs. remote data source) without touching the consumers.
- [Auto-Configuration](auto-configuration-notes.md) — how Spring Boot
  auto-wires beans, where the configs live, and the `@Conditional...` rules
  that activate them.
- [@ConfigurationProperties](configuration-properties-notes.md) — binding a
  group of external properties onto a typed config class by prefix.
- [Profiles](profiles-notes.md) — environment-specific configuration
  (dev/test/prod) via profile files and `@Profile` beans.
- [DevTools](devtools-notes.md) — automatic application restart on code or
  property changes during development.
- [Actuator](actuator-notes.md) — production-ready monitoring endpoints
  (health, metrics, env…) and how to expose them.
