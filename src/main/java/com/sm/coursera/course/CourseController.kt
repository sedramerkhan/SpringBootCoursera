package com.sm.coursera.course

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/courses")
class CourseController {


    fun getFakeCourses(): List<Course> = listOf(
        Course(1, "Spring Boot Fundamentals", "John Doe"),
        Course(2, "Kotlin for Beginners", "Jane Smith"),
        Course(3, "REST API Design", "Bob Johnson")
    )



    @GetMapping("/say-hello")
    @ResponseBody
    fun sayHello() = "Hello World!"

    @GetMapping("/page", produces = [MediaType.TEXT_HTML_VALUE])
    fun getCoursesPage(): String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <title>Coursera Courses</title>
        </head>
        <body>
            <h1>Welcome to Coursera</h1>
            <p>Browse our available courses.</p>
        </body>
        </html>
    """.trimIndent()
}