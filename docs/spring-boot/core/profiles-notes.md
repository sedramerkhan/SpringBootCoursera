# Spring Profiles — Notes

Profiles let you provide **environment-specific configuration** — different
settings for `dev`, `test`, `prod`, etc. — and switch between them without
changing code.

## The idea

You keep one set of beans/properties per environment and activate the right
profile at startup. Beans and config files tied to an inactive profile are
simply ignored.

## Per-profile config files

Spring Boot loads a profile-specific file **on top of** the default one:

```
application.yaml            # always loaded (defaults)
application-dev.yaml        # loaded only when 'dev' is active
application-prod.yaml       # loaded only when 'prod' is active
```

(For `.properties`, the same pattern: `application-dev.properties`, etc.)
Profile-specific values **override** the defaults.

With a single YAML file you can also separate profiles by document:

```yaml
# default section
server:
  port: 8080
---
spring:
  config:
    activate:
      on-profile: dev
server:
  port: 8081
---
spring:
  config:
    activate:
      on-profile: prod
server:
  port: 80
```

## Activating a profile

Pick whichever fits your environment:

```properties
# in application.yaml / .properties
spring.profiles.active=dev
```

```bash
# command-line argument
java -jar app.jar --spring.profiles.active=prod

# JVM system property
-Dspring.profiles.active=prod

# environment variable
SPRING_PROFILES_ACTIVE=prod
```

You can activate **multiple** profiles at once: `dev,debug`.

## Profile-specific beans

Annotate a bean/configuration so it only loads under a given profile:

```java
@Component
@Profile("dev")
class DevDataSeeder { ... }      // created only when 'dev' is active

@Bean
@Profile("prod")
DataSource prodDataSource() { ... }
```

- `@Profile("!prod")` → active for **every profile except** `prod`.

## Reading the active profile in code

```java
@Autowired Environment env;
boolean isDev = env.acceptsProfiles(Profiles.of("dev"));
String[] active = env.getActiveProfiles();
```

## Quick mental model

1. Define defaults in `application.yaml`.
2. Add `application-<profile>.yaml` for each environment's overrides.
3. Tag environment-only beans with `@Profile("...")`.
4. Activate with `spring.profiles.active` (property, CLI arg, or env var).
5. Only the active profile's files and beans take effect — everything else is
   skipped.