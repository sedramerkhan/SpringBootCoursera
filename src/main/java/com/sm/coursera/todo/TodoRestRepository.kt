package com.sm.coursera.todo

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

/**
 * Spring Data REST demo.
 *
 * Just by declaring this interface, Spring Data REST auto-exposes a full REST API
 * for the Todo entity — no @RestController, no methods to write. With the base path
 * configured to /api (application.properties), you get:
 *
 *   GET    /api/todos              list (paginated)         ?page=0&size=2&sort=description
 *   GET    /api/todos/{id}         one todo
 *   POST   /api/todos             create  -> 201 Created + Location header
 *   PUT    /api/todos/{id}         replace
 *   PATCH  /api/todos/{id}         partial update
 *   DELETE /api/todos/{id}         delete
 *
 * - PagingAndSortingRepository adds the ?page/?size/?sort support;
 *   CrudRepository adds save/find/delete (in Spring Data 3.x the two are separate
 *   interfaces, so we extend both — JpaRepository would also work).
 * - @RepositoryRestResource(path = "todos") sets the URL segment. Without it the
 *   default would be the pluralised entity name ("todoes"). collectionResourceRel
 *   is the name used for this collection inside the JSON _embedded block.
 */
@RepositoryRestResource(path = "todos", collectionResourceRel = "todos")
interface TodoRestRepository :
    PagingAndSortingRepository<Todo, Int>,
    CrudRepository<Todo, Int>