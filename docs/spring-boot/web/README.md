# Web / MVC — Reference Notes

The request/response layer: how a request travels through the front
controller to your code and back, the two flavors of response (view vs.
JSON), and the view-side concerns (JSP rendering, form binding, passing data
across requests). For the IoC container underneath see
[../core/](../core/README.md); for authentication/authorization around these
requests see [../security/](../security/README.md).

## Notes

- [DispatcherServlet & Request Flow](dispatcher-servlet-notes.md) — how an
  HTTP request travels through the front controller to your controller and
  back.
- [@Controller vs @RestController & JSON](rest-controller-json-notes.md) —
  view names vs. response bodies, why `@RestController` drops
  `@ResponseBody`, and how Jackson serializes a returned bean/list to JSON
  automatically.
- [JSP & View Resolver](jsp-notes.md) — server-rendered HTML views: how a
  controller returns a view name and the resolver's prefix/suffix turn it
  into a JSP file.
- [Spring MVC Forms](spring-mvc-forms-notes.md) — command beans, two-way
  binding with the `<form:…>` tags, and server-side validation (`@Valid`,
  `@Size`, `BindingResult`, `<form:errors>`) — plus the Kotlin specifics.
- [Model & @SessionAttributes](session-attributes-notes.md) — passing data
  controller → view via the request-scoped `Model`, and keeping a value (the
  logged-in name) across requests with `@SessionAttributes` /
  `@SessionAttribute`.
