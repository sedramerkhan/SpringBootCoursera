package com.sm.coursera.todo

import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.SessionAttribute
import java.time.LocalDate

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

    // GET /todos/add -> show the empty add-todo form.
    @GetMapping("/todos/add")
    fun showAddTodo(
        @SessionAttribute(name = "name", required = false) username: String?,
        model: ModelMap,
    ): String {
        if (username.isNullOrBlank()) return "redirect:/login"
        model.addAttribute("name", username)
        return "todo/addTodo"
    }

    // POST /todos/add -> save the new todo, then redirect back to the list.
    // Redirect-after-POST (PRG) so a refresh won't re-submit the form.
    @PostMapping("/todos/add")
    fun addTodo(
        @SessionAttribute(name = "name", required = false) username: String?,
        @RequestParam description: String,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) targetDate: LocalDate,
    ): String {
        if (username.isNullOrBlank()) return "redirect:/login"
        todoService.addTodo(username, description.trim(), targetDate)
        logger.debug("added todo for {}: {}", username, description)
        return "redirect:/todos"
    }
}