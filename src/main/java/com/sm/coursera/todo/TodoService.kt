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

    // Adds a new todo (not done yet) and returns it. The id is max+1 so it stays
    // unique even though the data is just an in-memory list for now.
    fun addTodo(username: String, description: String, targetDate: LocalDate): Todo {
        val id = (todos.maxOfOrNull { it.id } ?: 0) + 1
        val todo = Todo(id, username, description, targetDate, false)
        todos.add(todo)
        return todo
    }
}