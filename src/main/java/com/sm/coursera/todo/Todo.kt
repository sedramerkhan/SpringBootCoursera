package com.sm.coursera.todo

import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

/**
 * A single todo item — and the command bean (form-backing object) for the add
 * form. Two-way binding: Spring fills the form from this object when rendering,
 * and binds the submitted fields back onto a fresh object on POST.
 *
 * Why this shape (vs. the immutable val data class before):
 *  - `var` + default values => Spring can create an empty instance and bind via
 *    setters. (Kotlin reflection, pulled in by kotlin-reflect, instantiates it
 *    through the primary constructor.)
 *  - LocalDate lives in java.time (not java.util) — the modern date type.
 *
 * Validation uses jakarta.validation constraints. The `@field:` use-site target
 * is required in Kotlin so the annotation lands on the backing field (where the
 * validator reads it) rather than on the constructor parameter.
 */
data class Todo(
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