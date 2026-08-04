# Security — Reference Notes

Authentication and authorization with `spring-boot-starter-security`. Only
one note so far — folded into its own folder since it's the security
concern layered across [Web](../web/README.md) and
[Persistence](../persistence/README.md) (login/logout flow, and the
H2-console exceptions carved out of the default rules).

## Notes

- [Spring Security](spring-security-notes.md) — authentication and
  authorization with `spring-boot-starter-security`: an in-memory user,
  BCrypt password hashing, URL rules and form login/logout, CSRF tokens,
  and the dispatcher-type fix for JSP forwards.
