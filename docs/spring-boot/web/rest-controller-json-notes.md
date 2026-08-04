# @Controller vs @RestController & JSON with Jackson — Notes

Two ways a controller method can answer a request: return a **view name** (HTML
page) or return **data** (JSON/text). The annotation decides which is the default.

## `@Controller` vs `@RestController`

| | `@Controller` | `@RestController` |
|---|---|---|
| Default return meaning | a **view name** (resolved to a JSP/template) | the **response body** (data) |
| Body responses need | `@ResponseBody` on each method | nothing — it's automatic |
| Typical use | server-rendered HTML pages | REST APIs / JSON endpoints |

`@RestController` is just `@Controller` + `@ResponseBody` applied to every method.
So with `@RestController` you never write `@ResponseBody` — the return value is
serialized straight into the HTTP response.

```kotlin
@Controller                       // returns VIEW NAMES
class HelloViewController {
    @GetMapping("/say-hello-page")
    fun page(model: ModelMap): String = "sayHello"   // -> /WEB-INF/jsp/sayHello.jsp
}

@RestController                   // returns RESPONSE BODIES
@RequestMapping("/courses")
class CourseController {
    @GetMapping("/say-hello")
    fun sayHello() = "Hello World!"                   // body, not a view name
}
```

In this project both styles exist side by side: `HelloViewController` (`@Controller`,
renders JSP) and `CourseController` (`@RestController`, returns data).

## Jackson — bean → JSON

When a `@RestController` method returns an **object** (a bean) instead of a String,
Spring uses **Jackson** (auto-configured by `spring-boot-starter-web`) to serialize
it to JSON. No manual conversion code is needed.

```kotlin
data class Course(val id: Long, val name: String, val author: String)

@GetMapping                                   // GET /courses
fun getFakeCourses(): List<Course> = listOf(
    Course(1, "Spring Boot Fundamentals", "John Doe"),
    Course(2, "Kotlin for Beginners", "Jane Smith"),
)
```

`GET /courses` returns a JSON array — Jackson maps each property to a JSON field:

```json
[
  { "id": 1, "name": "Spring Boot Fundamentals", "author": "John Doe" },
  { "id": 2, "name": "Kotlin for Beginners", "author": "Jane Smith" }
]
```

How it works: Jackson reads the bean's properties (Kotlin `val`s / Java getters)
and writes a matching JSON field for each. A `List` becomes a JSON array. The same
mechanism runs in reverse for `@RequestBody`, turning incoming JSON back into a bean.

> Note: an endpoint method **must** carry a mapping (`@GetMapping`, etc.). Without
> one, the method is never exposed — that was the fix applied to `getFakeCourses()`
> in this project.

## Path variables vs. request (query) params

Two ways to read values from the URL — they differ in **where** the value sits.

| | Path variable | Request / query param |
|---|---|---|
| Annotation | `@PathVariable` | `@RequestParam` |
| Lives in | the **path** itself: `/courses/5` | the **query string**: `/courses?id=5` |
| Mapping | `@GetMapping("/courses/{id}")` | `@GetMapping("/courses")` |
| Good for | identifying a resource (REST style) | filters, options, search terms |

```kotlin
// Path variable — the {id} placeholder is bound to the parameter.
// GET /courses/5  ->  id = 5
@GetMapping("/{id}")
fun getCourse(@PathVariable id: Long): Course = ...

// Request param — read from the query string.
// GET /courses/search?author=Jane  ->  author = "Jane"
@GetMapping("/search")
fun byAuthor(@RequestParam author: String): List<Course> = ...
```

- Name the method parameter the same as the placeholder/key, or set it explicitly:
  `@PathVariable("id")`, `@RequestParam(name = "author")`.
- A request param can be optional with a default: `@RequestParam(defaultValue = "")`.
- `HelloViewController` in this project already uses `@RequestParam name` to read
  `?name=...`.

## POST → 201 Created with a `Location` header

When a `@PostMapping` **creates** a resource, the polite REST response is
`201 Created` plus a `Location` header pointing at the new resource's URL — so the
client can immediately fetch it. Return `ResponseEntity.created(uri)` to set both
the status and the header at once.

Build that URL from the **current request path** and append the new id, instead of
hard-coding it:

```kotlin
@PostMapping
fun addQuestion(@PathVariable surveyId: String,
                @RequestBody question: Question): ResponseEntity<Void> {
    val questionId = service.addQuestion(surveyId, question)   // e.g. a random id

    // current request path: /surveys/{surveyId}/questions
    // append /{questionId} and substitute the real id -> the new resource URL
    val location = ServletUriComponentsBuilder
        .fromCurrentRequest()          // path up to the current request
        .path("/{questionId}")         // append the placeholder
        .buildAndExpand(questionId)    // replace {questionId} with the real value
        .toUri()

    return ResponseEntity.created(location).build()   // 201 + Location header
}
```

Step by step:

- **`fromCurrentRequest()`** — takes the URL of the request being handled (e.g.
  `POST /surveys/5/questions`).
- **`.path("/{questionId}")`** — appends a placeholder segment to that path.
- **`.buildAndExpand(questionId)`** — fills `{questionId}` with the actual value;
  the placeholder name must match the argument you pass.
- **`.toUri()`** — produces the final URI, e.g. `/surveys/5/questions/8421`.
- **`ResponseEntity.created(location)`** — sends `201 Created` and puts that URI in
  the `Location` response header.

After the POST, the response carries `Location: …/questions/8421`; opening that URL
(a `GET`) returns the newly created resource.

### `ResponseEntity` builders

`ResponseEntity<T>` lets you control the **status code, headers, and body**
explicitly (instead of letting `@RestController` default everything to `200 OK`).
Common factory methods:

| Builder | Status | Typical use |
|---|---|---|
| `ResponseEntity.ok(body)` | `200 OK` | normal success with a body |
| `ResponseEntity.created(uri)` | `201 Created` | after creating a resource (sets `Location`) |
| `ResponseEntity.noContent()` | `204 No Content` | success with **no** body (e.g. DELETE/PUT) |
| `ResponseEntity.notFound()` | `404 Not Found` | resource doesn't exist |
| `ResponseEntity.badRequest()` | `400 Bad Request` | invalid input |
| `ResponseEntity.status(code)` | any | anything not covered above |

The ones returning a **builder** (`created`, `noContent`, `notFound`, …) need a
final `.build()` (or `.body(x)` to attach a body):

```kotlin
return ResponseEntity.noContent().build()              // 204, empty body
return ResponseEntity.created(location).build()        // 201 + Location
return ResponseEntity.ok(course)                       // 200 + JSON body
return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
```

`ok(body)` takes the body directly; `created(uri)` takes the URI and returns a
builder, so you still call `.build()` (or `.body(...)`).

## In this project

- `CourseController` (`@RestController`) — `GET /courses` returns `List<Course>`
  (Jackson → JSON array); `GET /courses/say-hello` returns plain text; `GET
  /courses/page` returns raw HTML via `produces = TEXT_HTML_VALUE`.
- `HelloViewController` (`@Controller`) — returns the view name `"sayHello"`.
- `Course` — the `data class` (bean) Jackson serializes.