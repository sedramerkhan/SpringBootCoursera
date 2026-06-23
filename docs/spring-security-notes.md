# Spring Security — Authentication & Authorization — Notes

How the app authenticates users, guards URLs, and protects forms — all handled
by Spring Security against a single hardcoded in-memory user (`Sedra` /
`123456789`). This replaced the earlier hand-rolled `AuthController` /
`AuthService` login flow.

## What the starter gives you for free

Adding the dependency is enough to change behavior before you write any config:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Auto-configuration then:
- inserts a **filter chain** (`FilterChainProxy`, ~12 filters) in front of the
  `DispatcherServlet` — every request passes through it before any controller;
- **locks down every URL** (all requests require authentication);
- generates a default login page + a random startup password;
- turns on **CSRF protection** and security response headers.

`SpringSecurityConfiguration` replaces those defaults with our own rules.

## The config class

```kotlin
@Configuration(proxyBeanMethods = false)   // see "Kotlin gotcha" below
class SpringSecurityConfiguration {
```

### PasswordEncoder — how passwords are hashed

```kotlin
@Bean
fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
```

BCrypt is a slow, salted one-way hash. The stored password is hashed once at
startup; at login the typed password is hashed and **hashes are compared** —
plaintext is never stored or compared.

### UserDetailsService — the user store

```kotlin
@Bean
fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
    val sedra = User.withUsername("Sedra")
        .password(passwordEncoder.encode("123456789"))
        .roles("USER")
        .build()
    return InMemoryUserDetailsManager(sedra)
}
```

A `UserDetailsService` looks up a user by username. Here it's in-memory with one
hardcoded user. In a real app you'd swap **only this bean** for a JDBC/DB-backed
one — nothing else changes. Spring auto-wires this + the encoder into an
`AuthenticationManager` that performs the credential check.

### SecurityFilterChain — the rules

```kotlin
@Bean
fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
        .authorizeHttpRequests { auth ->
            auth
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers("/login", "/css/**").permitAll()
                .anyRequest().authenticated()
        }
        .formLogin { form ->
            form
                .loginPage("/login")
                .defaultSuccessUrl("/welcome", true)
                .permitAll()
        }
        .logout { logout ->
            logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
        }
    return http.build()
}
```

- **`authorizeHttpRequests`** — rules top-to-bottom, first match wins: the login
  page and CSS are public; everything else requires login. (The
  `dispatcherTypeMatchers` line is explained under "The redirect-loop trap".)
- **`formLogin`** — `loginPage("/login")` uses our JSP *and* declares that
  **`POST /login`** is the credential-processing endpoint (intercepted by a
  filter, never reaching a controller; it reads the `username` + `password`
  params — that's why the form field is `username`, not `name`).
  `defaultSuccessUrl("/welcome", true)` always lands on `/welcome`; failure
  redirects to `/login?error`.
- **`logout`** — Spring's default is **`POST /logout`** (POST so a prefetch/GET
  can't log you out); it invalidates the session and redirects to
  `/login?logout`.

## Request lifecycle

1. Request `/todos` while logged out → `anyRequest().authenticated()` denies →
   redirect to `/login`.
2. Submit form → `POST /login` → `AuthenticationManager` uses the
   `UserDetailsService` + `PasswordEncoder` to compare hashes.
3. Success → the `Authentication` (principal) is stored in the session →
   redirect to `/welcome`.
4. Later requests carry the session cookie; controllers read the user via a
   `java.security.Principal` parameter (`principal.name`).

## CSRF protection (on by default)

Every state-changing POST must echo a per-session token, or it's rejected with
**403**.

- Spring `<form:form>` tags (add/edit todo) inject the token **automatically**.
- Plain HTML `<form>`s need it by hand — added to the login, delete, and logout
  forms:

```jsp
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
```

`_csrf` is a request attribute exposed by Spring Security's `CsrfFilter`.

## The redirect-loop trap (Spring Security 6+/7)

JSP views render via an internal **`FORWARD`** to `/WEB-INF/jsp/...`. In Spring
Security 6+, the authorization filter runs on **all dispatcher types**, not just
the original client request. So:

```
GET /login → allowed → controller returns "auth/login"
           → FORWARD to /WEB-INF/jsp/auth/login.jsp
           → filter runs again → matches anyRequest().authenticated()
           → denied → redirect to /login → FORWARD → ... infinite loop
```

Fix — let internal dispatches through (real client requests are always type
`REQUEST`, so security is unchanged for them):

```kotlin
.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
```

## Kotlin gotcha

`@Configuration` classes are normally CGLIB-subclassed, but **Kotlin classes are
`final`**, which breaks that. `@Configuration(proxyBeanMethods = false)` removes
the proxy requirement. Safe here because no `@Bean` method calls another — each
gets its dependencies as parameters.

## In this project

- `AuthService` was **deleted**; credential checking is now the
  `UserDetailsService` + `PasswordEncoder`.
- `AuthController` only renders views (`GET /login`, `GET /welcome`); Spring
  Security owns `POST /login` and `POST /logout`.
- `TodoController` reads the username from a `Principal` parameter instead of a
  session attribute — superseding the approach in
  [`session-attributes-notes.md`](./session-attributes-notes.md).