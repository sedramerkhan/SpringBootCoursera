# Spring Beans — Notes

A **bean** is just an object whose lifecycle is managed by Spring instead of by
your own `new` calls. When a class becomes a bean, Spring **creates it,
configures it, holds the single instance, and injects it wherever it's needed** —
then disposes of it on shutdown.

## What "managed by Spring" means

Spring keeps a registry called the **application context** (the IoC container).
Every bean lives there. Instead of code doing `val x = SomeClass()`, you *declare*
that the class is a bean and Spring hands you the instance — this is
**Inversion of Control (IoC)**: you don't build dependencies, the container does.

The `ApplicationContext` is what **manages each bean's lifecycle** — it
instantiates the bean, injects its dependencies, runs init callbacks, holds the
instance, and runs destroy callbacks on shutdown.

**What if a bean is removed from the context?** It stops being managed by
Spring: no more injection, no lifecycle callbacks. Once nothing else holds a
reference to it, it becomes eligible for **garbage collection** like any
ordinary object.

## How a class becomes a bean

Mark it with a stereotype annotation and Spring's component scan picks it up:

```kotlin
@Component
@ConfigurationProperties(prefix = "currency-service")
class CurrencyServiceConfiguration {
    var url: String? = null
    var username: String? = null
    var key: String? = null
}
```

`@Component` turns this class into a Spring bean — Spring handles its lifecycle
and can inject it wherever it's needed.

Common ways to define a bean:

| Approach | Use when |
|----------|----------|
| `@Component` | a general class you own (and want scanned) |
| `@Service` | business-logic class (a `@Component` with intent) |
| `@Repository` | data-access class (adds DB exception translation) |
| `@RestController` / `@Controller` | web endpoint class |
| `@Bean` method in a `@Configuration` | a type you *don't* own, or need to build manually |

```kotlin
@Configuration
class AppConfig {
    @Bean
    fun restClient(): RestClient = RestClient.create()   // bean = the return value
}
```

## Using a bean (injection)

Once it's a bean, ask for it — Spring supplies the instance. Constructor
injection is preferred:

```kotlin
@RestController
class CurrencyServiceConfigurationController(
    private val configuration: CurrencyServiceConfiguration  // injected by Spring
)
```

You never wrote `CurrencyServiceConfiguration(...)` — the container did, and gave
you the same shared instance it gives everyone else.

### Asking the context directly (`getBean`)

If you have a handle on the context, you can also pull a bean out of it
manually — handy in a `main`/`CommandLineRunner` or for quick experiments:

```kotlin
fun main(args: Array<String>) {
    val context = runApplication<CourseraApplication>(*args)

    val config = context.getBean(CurrencyServiceConfiguration::class.java)  // by type
    val client = context.getBean("restClient", RestClient::class.java)      // by name + type
}
```

Prefer constructor injection in normal code — it's testable and declares
dependencies up front. Reach for `getBean(...)` only when you're outside a
managed bean and genuinely need to ask the container yourself.

## Why this matters: swapping implementations

The real payoff of DI shows up when you **program to an interface**. Declare the
dependency as an interface; provide one or more bean implementations; Spring
injects the right one — and the code that *uses* it never changes.

```kotlin
interface Payment {
    fun pay(amount: Long)
}

@Component
class CreditCardPayment : Payment {
    override fun pay(amount: Long) { /* … */ }
}
```

```kotlin
@Service
class Checkout(
    private val payment: Payment   // depends on the interface, not a concrete class
)
```

To add a new payment method, write a new class that implements `Payment`,
annotate it `@Component`, and you're done — `Checkout` is untouched. The same
pattern lets you swap a data-access implementation (e.g. a cached source vs. a
remote database) based on configuration, again without changing the consumers.

This is the **maintainability / extensibility** win: new behavior is added by
adding a class, not by editing the classes that depend on it (open/closed).

> When more than one bean implements the same interface, disambiguate with
> `@Primary` (a default winner) or `@Qualifier("name")` (pick explicitly) —
> otherwise Spring can't decide which one to inject.

## Bean scope (the default)

By default a bean is a **singleton**: one shared instance for the whole
application context. (Other scopes exist — `prototype`, `request`, `session` —
but singleton covers most cases.)

## Quick mental model

1. A bean = an object the Spring container owns.
2. Mark a class (`@Component`/`@Service`/…) or declare a `@Bean` method.
3. Spring creates one instance and stores it in the application context.
4. Anywhere that needs it, inject it — Spring wires the same instance in.
5. This is Inversion of Control: the container builds and connects objects, not
   your code.