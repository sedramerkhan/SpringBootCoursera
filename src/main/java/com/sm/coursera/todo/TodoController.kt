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
import java.security.Principal
import java.time.LocalDate

/**
 * Lists the logged-in user's todos and lets them add new ones.
 *
 * The username comes from the authenticated principal supplied by Spring
 * Security (java.security.Principal). Because every /todos** URL requires
 * authentication (see SpringSecurityConfiguration), principal is always
 * present here — no manual "redirect to login if missing" checks are needed.
 *
 * The add form uses a Todo command bean for two-way binding + validation.
 */
@Controller
class TodoController(
    private val todoService: TodoService,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/todos")
    fun listTodos(principal: Principal, model: ModelMap): String {
        val username = principal.name
        model.addAttribute("name", username)
        model.addAttribute("todos", todoService.findByUsername(username))
        return "todo/listTodos"
    }

    // GET /todos/add -> show the form. We seed a "todo" command bean with
    // sensible defaults; the form fields are pre-filled from it (binding,
    // direction 1: bean -> form). Without this bean the <form:form> tag fails.
    @GetMapping("/todos/add")
    fun showAddTodo(principal: Principal, model: ModelMap): String {
        val username = principal.name
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
        principal: Principal,
        @Valid @ModelAttribute("todo") todo: Todo,
        result: BindingResult,
        model: ModelMap,
    ): String {
        val username = principal.name

        if (result.hasErrors()) {
            logger.debug("add todo validation failed: {} error(s)", result.errorCount)
            model.addAttribute("name", username)
            return "todo/addTodo"
        }

        todo.username = username // trust the principal, not a client-supplied field
        todoService.addTodo(todo)
        logger.debug("added todo for {}: {}", username, todo.description)
        return "redirect:/todos"
    }

    // GET /todos/{id}/edit -> load the existing todo and show it in the form,
    // pre-filled (bean -> form). Same view machinery as add, just seeded with a
    // real todo instead of a blank one.
    @GetMapping("/todos/{id}/edit")
    fun showEditTodo(
        principal: Principal,
        @PathVariable id: Int,
        model: ModelMap,
    ): String {
        val username = principal.name
        val todo = todoService.findById(id, username) ?: return "redirect:/todos"
        model.addAttribute("name", username)
        model.addAttribute("todo", todo)
        return "todo/editTodo"
    }

    // POST /todos/{id}/update -> validate and save the edited todo (PRG). Same
    // binding/validation rules as add; BindingResult must follow the bean.
    @PostMapping("/todos/{id}/update")
    fun updateTodo(
        principal: Principal,
        @PathVariable id: Int,
        @Valid @ModelAttribute("todo") todo: Todo,
        result: BindingResult,
        model: ModelMap,
    ): String {
        val username = principal.name

        if (result.hasErrors()) {
            logger.debug("update todo validation failed: {} error(s)", result.errorCount)
            model.addAttribute("name", username)
            return "todo/editTodo"
        }

        todo.id = id                 // trust the path + principal, not client fields
        todo.username = username
        todoService.updateTodo(todo)
        logger.debug("updated todo id={} for {}", id, username)
        return "redirect:/todos"
    }

    //The annotation has to match the method the browser actually sends, and an HTML form can only send GET or POST
    // POST /todos/{id}/delete -> remove the row, then redirect back to the list
    // (PRG). POST (not a GET link) so a crawler/prefetch can't delete a todo.
    // Deletion is scoped to the logged-in user by passing the principal name.
    @PostMapping("/todos/{id}/delete")
    fun deleteTodo(
        principal: Principal,
        @PathVariable id: Int,
    ): String {
        val username = principal.name
        val removed = todoService.deleteById(id, username)
        logger.debug("delete todo id={} for {} -> removed={}", id, username, removed)
        return "redirect:/todos"
    }
}