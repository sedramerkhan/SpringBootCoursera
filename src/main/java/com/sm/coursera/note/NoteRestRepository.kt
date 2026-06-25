package com.sm.coursera.note

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

/**
 * Spring Data REST demo.
 *
 * Declaring this interface is enough for Spring Data REST to auto-expose a full
 * REST API for the Note entity — no @RestController, no method bodies. With the
 * base path set to /api (application.properties), you get:
 *
 *   GET    /api/notes              list (paginated)   ?page=0&size=2&sort=title,desc
 *   GET    /api/notes/{id}         one note
 *   POST   /api/notes             create  -> 201 Created + Location header
 *   PUT    /api/notes/{id}         replace
 *   PATCH  /api/notes/{id}         partial update
 *   DELETE /api/notes/{id}         delete
 *
 * - PagingAndSortingRepository adds ?page/?size/?sort; CrudRepository adds
 *   save/find/delete (in Spring Data 3.x they are separate interfaces, so we
 *   extend both).
 * - @RepositoryRestResource(path = "notes") sets the URL segment (the default
 *   would be the pluralised entity name, "notes" here anyway). collectionResourceRel
 *   is the key used for the collection inside the JSON _embedded block.
 *
 * Note is its OWN entity, so this is the only repository mapped to it — no clash
 * with TodoRepository, which stays a plain, unexposed repository for the MVC app
 * (`spring.data.rest.detection-strategy=annotated` keeps unannotated repos private).
 */
@RepositoryRestResource(path = "notes", collectionResourceRel = "notes")
interface NoteRestRepository :
    PagingAndSortingRepository<Note, Int>,
    CrudRepository<Note, Int>