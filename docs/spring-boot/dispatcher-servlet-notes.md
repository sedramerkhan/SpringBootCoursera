# DispatcherServlet & Request Flow — Notes

How a Spring MVC web request travels from the browser to your controller and
back.

## DispatcherServlet — the front controller

`DispatcherServlet` is a single servlet that receives **every** HTTP request
for the app and delegates the work to the right components. This is the
**Front Controller** pattern: one entry point coordinates everything.

It **coordinates** — routes requests and calls helpers — but holds **no
business logic** of its own; that lives in your controllers and services.

It is created for you by `DispatcherServletAutoConfiguration` (in
`spring-boot-webmvc.jar`) — Spring Boot auto-configures it when the web starter
is present, so you never register it manually.

**Analogy:** a receptionist — sends each visitor (request) to the right
department (controller), then makes sure they leave with what they came for
(response).

## The request lifecycle

```
Browser
   │  HTTP request
   ▼
DispatcherServlet  ──►  HandlerMapping      (which controller method handles this URL?)
   │                        │
   │                        ▼
   │                    HandlerAdapter       (invoke the matched @Controller method)
   │                        │
   │                        ▼
   │                    Your @Controller / @RestController
   │                        │  returns a value (view name, or @ResponseBody object)
   │                        ▼
   │   ┌── @RestController / @ResponseBody ──► HttpMessageConverter (e.g. JSON)
   │   │
   │   └── view name ──► ViewResolver ──► View renders the model
   ▼
Browser  ◄── HTTP response
```

### The players

| Component | Responsibility |
|---|---|
| `DispatcherServlet` | Front controller; orchestrates the whole flow |
| `HandlerMapping` | Maps a request URL → handler (controller method) |
| `HandlerAdapter` | Actually invokes the handler method |
| `Controller` | Your code; produces a model and/or response body |
| `HttpMessageConverter` | Serializes return objects (e.g. POJO → JSON) for REST |
| `ViewResolver` | Resolves a logical view name → an actual view (for HTML/templates) |
| `View` | Renders the model into the response |

## REST vs. MVC view

- **`@RestController`** (or `@ResponseBody`): the return value is written
  straight to the response body via an `HttpMessageConverter` — typically JSON.
  No view resolution happens. This is what REST APIs use.
- **`@Controller` returning a view name**: a `ViewResolver` finds a template
  (Thymeleaf, JSP, …) and renders HTML.

## When things go wrong

If no handler matches or an exception escapes, the request is forwarded to an
error handler configured by **`ErrorMvcAutoConfiguration`** — by default the
**Whitelabel Error Page** (or a JSON error body for REST clients).

## Quick mental model

1. One `DispatcherServlet` catches all requests.
2. `HandlerMapping` finds the matching controller method.
3. `HandlerAdapter` calls it.
4. REST → object becomes JSON; MVC → view name becomes HTML.
5. Errors fall through to `ErrorMvcAutoConfiguration`'s handler.

See also: [`auto-configuration-notes.md`](./auto-configuration-notes.md) for how
these components get auto-wired.
