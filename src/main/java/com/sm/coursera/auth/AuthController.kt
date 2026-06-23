package com.sm.coursera.auth

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

/**
 * Thin controller around the two auth views. Authentication itself is now
 * handled by Spring Security (see SpringSecurityConfiguration):
 *  - POST /login   is processed by Spring Security's filter, not a controller;
 *  - POST /logout  is handled by Spring Security too.
 *
 * This controller only renders pages:
 *  - GET /login   -> our custom login form (Spring Security's configured login page);
 *  - GET /welcome -> the landing page after a successful sign-in.
 *
 * View names are returned (not response bodies). The resolver turns
 * "auth/login" into /WEB-INF/jsp/auth/login.jsp via spring.mvc.view.prefix/suffix.
 */
@Controller
class AuthController {

    private val logger = LoggerFactory.getLogger(javaClass)

    // GET /login -> render the form. Spring Security adds ?error / ?logout
    // query params, which the JSP reads to show the right banner.
    @GetMapping("/login")
    fun loginPage(): String {
        logger.debug("loginPage requested")
        return "auth/login"
    }

    // GET /welcome -> shown right after login. The username comes from the
    // authenticated principal (set by Spring Security), not a form field or the
    // session. We expose it as "name" so the shared navbar/JSPs can show it.
    @GetMapping("/welcome")
    fun welcome(principal: Principal, model: ModelMap): String {
        logger.debug("welcome page for {}", principal.name)
        model.addAttribute("name", principal.name)
        return "auth/welcome"
    }
}