# Spring Boot Actuator — Notes

Actuator adds **production-ready endpoints** for monitoring and managing a running
app — health, metrics, environment, beans, mappings, and more — exposed over HTTP
under `/actuator`.

## 1. Add the dependency

Actuator is not on the classpath by default. Add it to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## 2. Expose the endpoints

By default only `/actuator/health` is exposed over the web. Choose what to expose
in `application.properties`:

```properties
# expose everything (fine for local/dev)
management.endpoints.web.exposure.include=*

# or expose a specific set (better for prod)
management.endpoints.web.exposure.include=health,info,metrics

# optionally hide some even when * is used
management.endpoints.web.exposure.exclude=env,beans
```

## 3. Hit the endpoints

With the app running on `http://localhost:8080`:

| Endpoint | Shows |
|----------|-------|
| `/actuator` | index of available endpoints |
| `/actuator/health` | UP/DOWN status (DB, disk, etc.) |
| `/actuator/info` | custom app info (build, version…) |
| `/actuator/metrics` | metric names; `/actuator/metrics/{name}` for values |
| `/actuator/env` | resolved `Environment` properties |
| `/actuator/beans` | every Spring bean in the context |
| `/actuator/mappings` | all `@RequestMapping` routes |
| `/actuator/loggers` | view/change log levels at runtime |

## Useful extras

**Show full health details** (component breakdown, not just the overall status):

```properties
management.endpoint.health.show-details=always
```

**Populate `/actuator/info`** with custom values:

```properties
management.info.env.enabled=true
info.app.name=Coursera
info.app.version=0.0.1
```

**Change the base path** (default `/actuator`):

```properties
management.endpoints.web.base-path=/manage
```

## Security note

These endpoints can leak sensitive data (`env` shows config values, `beans`/
`mappings` reveal internals). `include=*` is convenient for local dev, but in
production expose only what you need and protect the endpoints (e.g. with Spring
Security or a separate management port via `management.server.port`).

## Quick mental model

1. Add `spring-boot-starter-actuator`.
2. Expose endpoints with `management.endpoints.web.exposure.include`.
3. Browse `/actuator` to see what's available.
4. Lock it down before shipping to production.