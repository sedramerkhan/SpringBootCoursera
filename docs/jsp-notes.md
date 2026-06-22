# JSP & the View Resolver — Notes

**JSP (JavaServer Pages)** is a server-side **view technology**: an HTML file
that can embed dynamic Java content. The server compiles it, fills in the
dynamic bits, and sends plain HTML to the browser.

> **If you know Laravel:** JSP plays the same role as **Blade**. A controller
> returning `"sayHello"` is like `return view('sayHello')` — a *name* handed to a
> resolver that finds the template. (The closest match to Blade's ergonomics in
> Spring is actually **Thymeleaf**; JSP is the older option.)

## Why use a view (JSP) at all?

A `@RestController` returns **data** (JSON/text) — good for APIs. A **view**
returns a **rendered HTML page** — good for server-rendered web pages. JSP lets
you keep the page markup in its own file instead of building HTML strings inside
a controller, and lets you inject model data (a course list, a username…) into
that markup.

So the split is:

| Goal | Controller type | Returns |
|------|-----------------|---------|
| API (data) | `@RestController` | object → JSON |
| Web page (HTML) | `@Controller` | a **view name** → rendered by a view resolver |

## The flow — what Spring MVC actually does

1. A request hits a `@Controller` method.
2. The method returns a **view name** — a plain string like `"sayHello"`, **not**
   HTML and **not** a file path.
3. Spring MVC hands that name to a **View Resolver**.
4. The resolver turns the name into an actual view file and renders it.

```kotlin
@Controller
class HelloViewController {
    @GetMapping("/say-hello-page")
    fun sayHelloPage(): String = "sayHello"   // <-- a VIEW NAME, not a body
}
```

(Note it's `@Controller`, not `@RestController` — a `@RestController` would treat
`"sayHello"` as the response body and just print the word.)

## Where the View Resolver is configured

In `application.properties`:

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

The resolver **wraps the returned view name with the prefix and suffix**:

```
prefix + viewName + suffix
/WEB-INF/jsp/ + sayHello + .jsp   ->   /WEB-INF/jsp/sayHello.jsp
```

That resolved path is the JSP file Spring renders. Change the view name in the
controller and the same prefix/suffix apply — that's the whole point: controllers
stay free of file paths.

## Where the JSP file lives, and why

```
src/main/resources/META-INF/resources/WEB-INF/jsp/sayHello.jsp
```

- **`META-INF/resources/`** — makes the file discoverable when the app runs as an
  executable **JAR**. The usual `src/main/webapp/` folder is only packaged in a
  **WAR**; the Servlet 3.0 spec serves content from `META-INF/resources/` on the
  classpath, so JSPs survive JAR packaging here.
- **`WEB-INF/`** — a protected directory: the container refuses to serve it from a
  direct URL. So nobody can hit `/WEB-INF/jsp/sayHello.jsp` directly — the page is
  reachable **only** through the controller + view resolver. That enforces MVC.
- **`jsp/`** — convention; just matches the resolver's prefix.

## Enabling JSP support

Embedded Tomcat needs the JSP engine on the classpath:

```xml
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-jasper</artifactId>
    <scope>provided</scope>
</dependency>
```

## Caveat

JSP + Spring Boot **fat JARs** has known limitations and is officially
discouraged — it's most reliable with WAR packaging or `mvn spring-boot:run`. For
new apps, **Thymeleaf** is the recommended view engine and needs none of the
`META-INF/resources/WEB-INF` setup (templates just live in
`src/main/resources/templates/`).

## Quick mental model

1. Controller returns a **view name** (`"sayHello"`), not HTML.
2. The **View Resolver** adds `prefix` + `suffix` → `/WEB-INF/jsp/sayHello.jsp`.
3. Tomcat (via Jasper) compiles and renders that JSP to HTML.
4. JSP lives under `META-INF/resources/WEB-INF/jsp/` so it's JAR-safe and not
   directly reachable.