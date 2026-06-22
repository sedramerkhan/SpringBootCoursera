package com.sm.coursera.auth

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.SessionAttributes

/**
 * Two-page demo: a login form and a welcome screen that echoes the input.
 *
 * View names are returned (not response bodies). The resolver turns
 * "auth/login" into /WEB-INF/jsp/auth/login.jsp via spring.mvc.view
 * .prefix/suffix in application.properties.
 *
 * AuthService (a @Service bean) is injected via the constructor and decides
 * whether the credentials are valid — the controller just handles HTTP.
 */
@Controller
// Whenever "name" is added to the model in this controller, Spring also stores
// it in the HTTP session. TodoController then reads it back with
// @SessionAttribute so the user's name follows them across pages.
@SessionAttributes("name")
class AuthController(
    private val authService: AuthService,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    // GET /login -> just render the form. No model needed.
    @GetMapping("/login")
    fun loginPage(): String {
        logger.debug("loginPage requested")
        return "auth/login"
    }

    // POST /login -> validate via the service. On success show the welcome
    // page; on failure re-render the login form with an error message.
    // POST (not GET) keeps the password out of the URL.
    @PostMapping("/login")
    fun submitLogin(
        @RequestParam name: String,
        @RequestParam password: String,
        model: ModelMap,
    ): String {
        logger.debug("login attempt for name={}", name) // never log passwords

        if (!authService.isValid(name, password)) {
            // Brackets + lengths make any hidden whitespace visible in the log.
            logger.debug(
                "login failed: name=[{}] (len={}), password length={}",
                name, name.length, password.length,
            )
            model.addAttribute("error", "Invalid name or password. Please try again.")
            // Use "enteredName" (not "name") to repopulate the field: "name" is
            // a @SessionAttributes key, so storing it here would wrongly put an
            // invalid name in the session.
            model.addAttribute("enteredName", name)
            return "auth/login"
        }

        model.addAttribute("name", name)
        model.addAttribute("password", password)
        return "auth/welcome"
    }
}