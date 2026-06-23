package com.sm.coursera.todo

import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.SessionAttribute
import java.time.LocalDate

/**
 * Lists the logged-in user's todos and lets them add new ones.
 *
 * The username is read from the HTTP session with @SessionAttribute (it was put
 * there at login by AuthController's @SessionAttributes("name")).
 *
 * The add form uses a Todo command bean for two-way binding + validation.
 */
@Controller
class TodoController(
    private val todoService: TodoService,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/todos")
    fun listTodos(
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

    // GET /todos/add -> show the form. We seed a "todo" command bean with
    // sensible defaults; the form fields are pre-filled from it (binding,
    // direction 1: bean -> form). Without this bean the <form:form> tag fails.
    @GetMapping("/todos/add")
    fun showAddTodo(
        @SessionAttribute(name = "name", required = false) username: String?,
        model: ModelMap,
    ): String {
        if (username.isNullOrBlank()) return "redirect:/login"
        model.addAttribute("name", username)
        model.addAttribute("todo", Todo(username = username, targetDate = LocalDate.now().plusYears(1)))
        return "todo/addTodo"
    }

    // POST /todos/add -> Spring binds the submitted fields onto a Todo
    // (binding, direction 2: form -> bean) and validates it (@Valid). The
    // BindingResult MUST come directly after the validated bean. On errors we
    // re-render the form (errors + the user's input are shown). Otherwise we
    // save and redirect (PRG) so a refresh won't re-submit.
    @PostMapping("/todos/add")
    fun addTodo(
        @SessionAttribute(name = "name", required = false) username: String?,
        @Valid @ModelAttribute("todo") todo: Todo,
        result: BindingResult,
        model: ModelMap,
    ): String {
        if (username.isNullOrBlank()) return "redirect:/login"

        if (result.hasErrors()) {
            logger.debug("add todo validation failed: {} error(s)", result.errorCount)
            model.addAttribute("name", username)
            return "todo/addTodo"
        }

        todo.username = username // trust the session, not a client-supplied field
        todoService.addTodo(todo)
        logger.debug("added todo for {}: {}", username, todo.description)
        return "redirect:/todos"
    }

    //The annotation has to match the method the browser actually sends, and an HTML form can only send GET or POST
    // POST /todos/{id}/delete -> remove the row, then redirect back to the list
    // (PRG). POST (not a GET link) so a crawler/prefetch can't delete a todo.
    // Deletion is scoped to the logged-in user by passing the session name.
    @PostMapping("/todos/{id}/delete")
    fun deleteTodo(
        @SessionAttribute(name = "name", required = false) username: String?,
        @PathVariable id: Int,
    ): String {
        if (username.isNullOrBlank()) return "redirect:/login"
        val removed = todoService.deleteById(id, username)
        logger.debug("delete todo id={} for {} -> removed={}", id, username, removed)
        return "redirect:/todos"
    }
}