package com.sm.coursera.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

/**
 * Spring Security setup.
 *
 * Replaces the old hand-rolled AuthController/AuthService login flow: Spring
 * Security now authenticates the user, manages the session, protects against
 * CSRF, and guards every URL. We keep the credentials hard-coded (in memory)
 * exactly as before — Sedra / 123456789 — just stored and checked by Spring
 * Security instead of our own code. In a real app these would live in a
 * database and the password would already be hashed.
 */
// proxyBeanMethods = false: no CGLIB proxy is created, so this Kotlin class
// needn't be `open`. Safe here because no @Bean method calls another directly —
// each gets its dependencies as parameters (Spring injects them).
@Configuration(proxyBeanMethods = false)
class SpringSecurityConfiguration {

    /**
     * How passwords are hashed/compared. The in-memory user's password is
     * encoded with this, and Spring Security re-encodes the submitted password
     * to compare — plain text is never stored or compared.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /**
     * The user store. One hard-coded user, password BCrypt-hashed at startup.
     * InMemoryUserDetailsManager is the in-memory implementation of the user
     * store Spring Security authenticates against.
     */
    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val sedra = User.withUsername("Sedra")
            .password(passwordEncoder.encode("123456789"))
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(sedra)
    }

    /**
     * The security rules:
     *  - static CSS and the login page are open to everyone;
     *  - every other URL requires an authenticated user;
     *  - we point form login at our own styled /login page and land on /welcome
     *    after a successful sign-in;
     *  - logout (POST /logout) clears the session and returns to /login?logout.
     *
     * CSRF protection stays on (the default) except for the H2 console. The
     * Spring <form:form> tags add the token automatically; plain HTML forms
     * include it via the _csrf request attribute (see the JSPs).
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    // Spring Security 6+/7 filters ALL dispatcher types by default.
                    // JSP views are rendered via an internal FORWARD to
                    // /WEB-INF/jsp/**, and errors via an ERROR dispatch — neither
                    // is a real client request, so permit them. Without this, the
                    // forward to the login JSP is itself blocked and bounces back
                    // to /login, causing an infinite redirect loop.
                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                    // H2 web console (dev-only) — let it through without login.
                    .requestMatchers("/h2-console/**").permitAll()
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
            // The H2 console posts plain forms (no CSRF token) and renders inside
            // frames. Disable CSRF for it and allow same-origin framing so the
            // console works; the rest of the app keeps CSRF + frame protection.
            .csrf { csrf -> csrf.ignoringRequestMatchers("/h2-console/**") }
            .headers { headers -> headers.frameOptions { frame -> frame.sameOrigin() } }
        return http.build()
    }
}