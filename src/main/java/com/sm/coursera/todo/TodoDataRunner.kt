package com.sm.coursera.todo

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * A CommandLineRunner demo for Spring Data JPA.
 *
 * Any bean implementing CommandLineRunner has its run() called ONCE, right after
 * the application context is ready (before the web server starts serving). It's a
 * handy place to exercise a repository and see JPA at work in the logs.
 *
 * Spring injects the TodoRepository (constructor injection). We use a throwaway
 * "demo" username so these rows don't show up for a logged-in user in the UI.
 */
@Component
class TodoDataRunner(private val repository: TodoRepository) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String) {
        // CREATE — save() inserts because id = 0 (new). Returns the row with its
        // database-assigned id.
        val saved = repository.save(
            Todo(
                username = "demo",
                description = "Learn Spring Data JPA",
                targetDate = LocalDate.now().plusDays(7),
                done = false,
            )
        )
        logger.info("Saved todo with generated id={}", saved.id)

        // READ (by id) — findById returns an Optional<Todo>.
        repository.findById(saved.id).ifPresent { logger.info("Found by id: {}", it.description) }

        // READ (derived query) — Spring generated this from the method name.
        logger.info("Demo user's todos: {}", repository.findByUsernameIgnoreCase("demo"))
    }
}