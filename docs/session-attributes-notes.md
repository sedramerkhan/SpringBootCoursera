# Model & @SessionAttributes — Passing Data Around — Notes

How data travels from a controller to a view, and how to keep a value across
several requests (like the logged-in user's name).

## Model / ModelMap — one request only

The `Model` (or `ModelMap`) is a map a controller fills; its entries become
available to the view as EL variables.

```kotlin
model.addAttribute("name", username)   // controller
```
```jsp
Hello, ${name}!                         <%-- view --%>
```

**Scope = one request.** The map is created fresh per request and thrown away
after the response. The next request starts empty — so the model alone can't
remember the user between pages.

## @SessionAttributes — promote a model attribute into the session

Class-level annotation. It says: *"whenever this attribute is put in the model
in this controller, also keep a copy in the HTTP session."*

```kotlin
@Controller
@SessionAttributes("name")              // <-- on AuthController
class AuthController(...) {
    ...
    model.addAttribute("name", name)    // now also stored in the session
}
```

**Scope = the session** (survives across requests, per browser) until removed.
This is how the name set at **login** is still available on the **todos** page.

Two consequences worth knowing:
- It also **copies the session value back into the model** on later requests *to
  the same controller* — so within that controller you can read it with
  `model.get("name")`.
- Don't put an attribute in the model on a failure path if its name is a
  session-attribute key, or you'll store junk in the session. (That's why the
  failed-login path uses `enteredName`, not `name`.)

## Two annotations that look alike

| | `@SessionAttributes` (plural) | `@SessionAttribute` (singular) |
|---|---|---|
| Placed on | the controller class | a handler **parameter** |
| Job | **store** model attrs in the session | **read** an existing session attr |
| Reading scope | same controller (via the model) | any controller (reads session directly) |

## Reading it back: `model.get` vs `@SessionAttribute`

Because `@SessionAttributes` re-injects the value into the model, a handler in
**the same controller** can read it straight off the model (the course's style):

```kotlin
val username = model["name"] as String   // works in AuthController
```

But `TodoController` is a **different** controller — the model is never seeded
there, so `model.get("name")` would be `null`. Instead it reads the session
directly:

```kotlin
@SessionAttribute(name = "name", required = false) username: String?
```

- `required = false` → `null` instead of a 400 when nobody is logged in (so we
  can redirect to `/login`).
- `String?` → Kotlin-nullable, matching that it may be absent.

## In this project

1. `AuthController` has `@SessionAttributes("name")`; a successful login puts
   `name` in the model → it lands in the session.
2. `TodoController` reads it with `@SessionAttribute("name")` to show the user's
   todos — the name "follows" the user across pages without being in the URL.

See also: [`spring-mvc-forms-notes.md`](./spring-mvc-forms-notes.md) for the
related `@ModelAttribute` command-bean binding.