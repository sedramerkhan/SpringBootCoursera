package com.sm.coursera.todo

import org.springframework.stereotype.Service

/**
 * Service layer for todos — the middle tier in `Controller -> Service ->
 * Repository`. The controller handles HTTP, the repository handles data access,
 * and this class holds everything in between: the **business logic**.
 *
 * Why a service layer (what it's for):
 *  1. Business rules — decisions that are neither HTTP nor SQL. Here: forcing
 *     `id = 0` on add so a forged hidden form field can't overwrite another
 *     row, and verifying ownership before update/delete so a user can only
 *     touch their own todos.
 *  2. Transaction boundaries — `@Transactional` belongs on service methods; an
 *     operation spanning several repository calls commits/rolls back as one.
 *  3. Orchestration — coordinating multiple repositories/services for one
 *     feature (none needed yet, but this is where it would live).
 *  4. Translation — adapting between web shapes and the domain, e.g. turning the
 *     repository's deleted-row count into a simple Boolean.
 *  5. A single reuse point — any caller (this MVC controller, a future REST API,
 *     a scheduled job, a test) gets the same rules without duplicating them.
 *
 * Keeping this logic here (not in the controller) means it isn't repeated per
 * handler and stays reusable; keeping it out of the repository keeps data access
 * thin. The class is currently small, but it's the natural place for this logic
 * to grow.
 *
 * Backed by a JPA repository (H2 database) instead of the original in-memory
 * list — the controller didn't change because the method signatures are the
 * same. Access stays scoped to the logged-in user throughout.
 */
@Service
class TodoService(
    private val todoRepository: TodoRepository,
) {

    fun findByUsername(username: String): List<Todo> =
        todoRepository.findByUsernameIgnoreCase(username)

    // Loads a single todo for editing — scoped to the user. null if not found.
    fun findById(id: Int, username: String): Todo? =
        todoRepository.findByIdAndUsernameIgnoreCase(id, username)

    // Force id = 0 so this is always an INSERT and the DB assigns the id — a
    // client can't smuggle an id in via the hidden form field to overwrite an
    // existing row. save() returns the persisted entity (with its new id).
    fun addTodo(todo: Todo): Todo {
        todo.id = 0
        return todoRepository.save(todo)
    }

    // Update only if the todo exists AND belongs to this user; otherwise a
    // crafted request could edit someone else's row. Returns true if updated.
    fun updateTodo(todo: Todo): Boolean {
        if (todoRepository.findByIdAndUsernameIgnoreCase(todo.id, todo.username) == null) return false
        todoRepository.save(todo)
        return true
    }

    // Deletes by id, scoped to the user. Returns true if a row was removed.
    fun deleteById(id: Int, username: String): Boolean =
        todoRepository.deleteByIdAndUsernameIgnoreCase(id, username) > 0
}