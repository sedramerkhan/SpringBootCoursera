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

    // Loads a single todo for editing — scoped to the user so you can't open
    // someone else's. null if not found.
    fun findById(id: Int, username: String): Todo? =
        todos.find { it.id == id && it.username.equals(username, ignoreCase = true) }

    // Adds a new todo and returns it. We assign the id here (max+1) so it stays
    // unique, even though the data is just an in-memory list for now.
    fun addTodo(todo: Todo): Todo {
        todo.id = (todos.maxOfOrNull { it.id } ?: 0) + 1
        todos.add(todo)
        return todo
    }

    // Replaces the existing todo that has the same id (and belongs to the user).
    // Returns true if one was updated.
    fun updateTodo(todo: Todo): Boolean {
        val index = todos.indexOfFirst {
            it.id == todo.id && it.username.equals(todo.username, ignoreCase = true)
        }
        if (index == -1) return false
        todos[index] = todo
        return true
    }

    // Deletes by id, but only if the todo belongs to this user — so a crafted
    // request can't remove someone else's todo. Returns true if one was removed.
    fun deleteById(id: Int, username: String): Boolean =
        todos.removeIf { it.id == id && it.username.equals(username, ignoreCase = true) }
}