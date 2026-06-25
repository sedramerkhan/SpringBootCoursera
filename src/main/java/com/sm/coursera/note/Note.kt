package com.sm.coursera.note

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

/**
 * A simple JPA @Entity used to demo Spring Data REST (see NoteRestRepository).
 *
 * It is deliberately separate from Todo: Spring Data REST maps one repository per
 * entity, and the Todo entity is already owned by TodoRepository (the MVC app). A
 * dedicated entity gives the REST API its own table with no conflict.
 *
 * Like Todo, this is a regular (non-data) class with `var`s + defaults so the
 * Kotlin jpa/all-open compiler plugins can give Hibernate the no-arg constructor
 * and non-final class it needs.
 */
@Entity
class Note(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,
    var title: String = "",
    var content: String = "",
)