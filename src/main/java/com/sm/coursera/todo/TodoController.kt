package com.sm.coursera.todo

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.SessionAttribute

/**
 * Lists the logged-in user's todos.
 *
 * The username is NOT a request parameter — it is read from the HTTP session
 * with @SessionAttribute. AuthController put it there at login time via its
 * class-level @SessionAttributes("name"). This is how the name "follows" the
 * user from page to page without passing it in every URL.
 */
@Controller
class TodoController(
    private val todoService: TodoService,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/todos")
    fun listTodos(
        // required = false so we can redirect instead of throwing a 400 when
        // someone hits /todos without logging in first.
        @SessionAttribute(name = "name", required = false) username: String?,
        model: ModelMap,
    ): String {
        if (username.isNullOrBlank()) {
            logger.debug("/todos hit with no session name -> redirecting to login")
            return "redirect:/login"
        }

        model.addAttribute("name", username)
        model.addAttribute("todos", todoService.findByUsername(username))
        return "todo/listTodos"
    }
}