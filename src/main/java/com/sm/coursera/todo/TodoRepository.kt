package com.sm.coursera.todo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.transaction.annotation.Transactional

/**
 * Spring Data JPA repository for Todo. Extending JpaRepository<Todo, Int> gives
 * us save / findById / findAll / deleteById etc. for free — Spring generates the
 * implementation at runtime.
 *
 * The methods below are "derived queries": Spring parses the method name and
 * writes the JPQL for us. "IgnoreCase" keeps the username match case-insensitive
 * (the login name's capitalisation may vary), matching the old in-memory logic.
 *
 * exported = false: this repository drives the MVC app, so we hide it from Spring
 * Data REST. Otherwise there would be TWO repositories for the Todo entity (this
 * one and TodoRestRepository), and Spring Data REST exports only one per entity.
 */
@RepositoryRestResource(exported = false)
interface TodoRepository : JpaRepository<Todo, Int> {

    fun findByUsernameIgnoreCase(username: String): List<Todo>

    // Scoped to the user so you can't load someone else's todo. null if absent.
    fun findByIdAndUsernameIgnoreCase(id: Int, username: String): Todo?

    // Derived delete queries run a select-then-delete, so they need a
    // transaction. Returns the number of rows removed (0 if nothing matched —
    // e.g. the todo belongs to another user).
    @Transactional
    fun deleteByIdAndUsernameIgnoreCase(id: Int, username: String): Long
}