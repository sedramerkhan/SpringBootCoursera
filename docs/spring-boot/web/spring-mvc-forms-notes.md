# Spring MVC Forms — Command Beans, Two-Way Binding & Validation — Notes

How the "add todo" form binds to a `Todo` object and validates it, instead of
reading loose request parameters.

## The problem with `@RequestParam`

Reading each field separately is **one-way** (form → controller, on submit only):

```kotlin
@RequestParam description: String, @RequestParam targetDate: LocalDate
```

It can't pre-fill fields, can't re-show the user's input after an error, and
doesn't plug into validation cleanly.

## Command bean (a.k.a. form-backing object)

Bind the whole object instead. This gives **two-way binding**:

1. **Bean → form** (rendering): each field is read from the object to pre-fill it.
2. **Form → bean** (submit): submitted fields are mapped back onto a fresh object.

```kotlin
@PostMapping("/todos/add")
fun addTodo(
    @Valid @ModelAttribute("todo") todo: Todo,   // form -> bean + validate
    result: BindingResult,                        // MUST come right after
    ...
)
```

The GET handler must seed the bean, or `<form:form>` fails with *"Neither
BindingResult nor plain target object for bean todo available"*:

```kotlin
model.addAttribute("todo", Todo(username = username, targetDate = ...))
```

The name must line up everywhere: model key `"todo"` = `modelAttribute="todo"` =
`@ModelAttribute("todo")`.

## Spring form tag library in the JSP

A different taglib from JSTL — ships with `spring-webmvc`, no extra dependency:

```jsp
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form modelAttribute="todo" method="post" action="/todos/add">
    <form:input  path="description" cssClass="field"/>
    <form:errors path="description" cssClass="field-error"/>
    <form:hidden path="id"/>     <%-- round-trip fields not edited on screen --%>
    <form:hidden path="done"/>
</form:form>
```

- `path` maps a field to a bean property.
- `cssClass` is the Spring-tag equivalent of the HTML `class` attribute.
- **Hidden fields** carry properties that aren't shown (`id`, `done`) through the
  round-trip so they don't come back null.

## Validation

1. Add the **`spring-boot-starter-validation`** dependency.
2. Annotate the bean with `jakarta.validation` constraints (`@Size`, `@NotBlank`,
   `@Future`, …).
3. Add **`@Valid`** on the bound parameter and inspect **`BindingResult`**.
4. Show messages in the view with **`<form:errors>`**.

```kotlin
if (result.hasErrors()) return "todo/addTodo"   // re-render with errors + input
```

## Kotlin specifics (this project is Kotlin, the course is Java)

- The command bean needs **mutable `var` properties with default values** so
  Spring can instantiate an empty bean and bind via setters:
  `data class Todo(var id: Int = 0, var description: String = "", …)`.
- **`kotlin-reflect`** must be on the classpath — Spring uses it to instantiate
  the bean through its primary constructor.
- Constraints need the **`@field:`** use-site target so they land on the backing
  field the validator reads: `@field:Size(min = 10, message = "...")`.
- Date binding uses `@field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)` so the
  `yyyy-MM-dd` from `<input type="date">` parses to `LocalDate`.

See also: [`dispatcher-servlet-notes.md`](./dispatcher-servlet-notes.md) for how
the request reaches the controller in the first place.