package com.sm.coursera.todo

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

/**
 * A single todo item — it serves two roles:
 *  1. a JPA @Entity mapped to the "todo" table (Hibernate creates the schema
 *     from these fields on startup);
 *  2. the command bean (form-backing object) for the add/edit forms — Spring
 *     fills the form from it when rendering and binds submitted fields back.
 *
 * Shape notes:
 *  - Regular class (not a data class) + the Kotlin jpa/all-open compiler plugins
 *    give Hibernate the no-arg constructor and non-final class it expects.
 *  - `var` + default values => Spring/Hibernate can create an empty instance and
 *    populate it. (kotlin-reflect instantiates via the primary constructor.)
 *
 * Annotation targets:
 *  - @Id / @GeneratedValue have no use-site target; since they can't target a
 *    constructor parameter, Kotlin places them on the backing field, so
 *    Hibernate uses field access throughout.
 *  - @field:Size / @field:DateTimeFormat are forced onto the field so the
 *    validator/formatter read them there.
 */
@Entity
class Todo(

    // id = 0 means "new" — IDENTITY lets the database assign the real value on
    // insert. Spring Data treats a 0 id as a new row (insert) and a non-zero id
    // as an existing one (update).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,

    var username: String = "",

    @field:Size(min = 10, message = "Enter at least 10 characters")
    var description: String = "",

    // ISO date (yyyy-MM-dd) — matches what <input type="date"> submits, so the
    // string binds straight to LocalDate.
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var targetDate: LocalDate = LocalDate.now(),

    var done: Boolean = false,
)