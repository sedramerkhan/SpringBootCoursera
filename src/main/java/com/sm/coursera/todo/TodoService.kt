package com.sm.coursera.todo

import org.springframework.stereotype.Service
import java.time.LocalDate

/**
 * Holds the todos. Backed by a static in-memory list for now — no database.
 * Swapping this for a JPA repository later would not change the controller.
 */
@Service
class TodoService {

    // Hard-coded test data. mutableListOf so add/delete can be added later.
    private val todos = mutableListOf(
        Todo(1, "Sedra", "Learn Spring Boot", LocalDate.now().plusMonths(3), false),
        Todo(2, "Sedra", "Learn Spring MVC", LocalDate.now().plusMonths(6), false),
        Todo(3, "Sedra", "Learn JSP & JSTL", LocalDate.now().plusMonths(1), true),
    )

    // Case-insensitive so it matches whatever capitalisation was typed at login.
    fun findByUsername(username: String): List<Todo> =
        todos.filter { it.username.equals(username, ignoreCase = true) }
}