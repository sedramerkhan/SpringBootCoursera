# @ConfigurationProperties — Notes

`@ConfigurationProperties` binds a group of external properties (from
`application.properties`/`.yaml`, env vars, etc.) onto the fields of a class, so
you read configuration as a **typed object** instead of scattered
`@Value("${...}")` lookups.

## The idea

You give the class a **prefix**, and every property under that prefix is mapped
to a matching field by name. Spring does the lookup, type conversion, and
binding for you.

```properties
# application.properties
currency-service.url=https://api.example.com/currency
currency-service.username=changeme
currency-service.key=changeme
```

```kotlin
@Component
@ConfigurationProperties(prefix = "currency-service")
class CurrencyServiceConfiguration {
    var url: String? = null
    var username: String? = null
    var key: String? = null
}
```

`currency-service.url` → `url`, `currency-service.username` → `username`, etc.

## Relaxed binding

The property key and the field name don't have to match character-for-character.
Spring normalizes both, so all of these bind to a `userName` field:

```
currency-service.user-name     # kebab-case (recommended in files)
currency-service.user_name     # underscore
CURRENCY_SERVICE_USERNAME      # env-var style (UPPER_SNAKE)
```

## Injecting it

Once it's a bean, inject it like any other dependency — constructor injection
preferred:

```kotlin
@RestController
@RequestMapping("/currency-configuration")
class CurrencyServiceConfigurationController(
    private val configuration: CurrencyServiceConfiguration
) {
    @GetMapping
    fun getConfiguration() = configuration
}
```

## Registering the bean — two ways

1. **`@Component`** on the properties class (used above) — simplest; needs
   mutable `var` fields for setter binding.
2. **`@EnableConfigurationProperties(...)`** (or `@ConfigurationPropertiesScan`)
   on a config/the application class — lets you use an **immutable** constructor-
   bound class:

   ```kotlin
   @ConfigurationProperties(prefix = "currency-service")
   data class CurrencyServiceConfiguration(
       val url: String,
       val username: String,
       val key: String,
   )
   ```

## `@ConfigurationProperties` vs `@Value`

| | `@ConfigurationProperties` | `@Value("${...}")` |
|--|--|--|
| Binds | a whole group by prefix | one property at a time |
| Type | structured object, nesting, lists | single value |
| Relaxed binding | yes | no |
| Validation | yes (`@Validated` + JSR-303) | no |
| Best for | a cohesive block of config | a one-off value |

## Validation (optional)

Add `@Validated` and standard constraints to fail fast at startup if config is
missing or malformed:

```kotlin
@Component
@ConfigurationProperties(prefix = "currency-service")
@Validated
class CurrencyServiceConfiguration {
    @field:NotBlank var url: String? = null
    @field:NotBlank var username: String? = null
    @field:NotBlank var key: String? = null
}
```

## Quick mental model

1. Annotate a class with `@ConfigurationProperties(prefix = "...")`.
2. Name its fields after the keys under that prefix (relaxed binding helps).
3. Register it (`@Component` **or** `@EnableConfigurationProperties`).
4. Inject it wherever you need config — typed, grouped, and validated.