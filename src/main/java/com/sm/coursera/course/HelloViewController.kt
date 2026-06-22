package com.sm.coursera.course

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class HelloViewController {

    // One logger per class. javaClass gives this class as the logger name,
    // so the log level is controlled by this class's package in .properties.
    private val logger = LoggerFactory.getLogger(javaClass)

    // Returns a VIEW NAME (not a body). The resolver turns "sayHello" into
    // /WEB-INF/jsp/sayHello.jsp using spring.mvc.view.prefix/suffix.
    // @RequestParam reads ?name=... from the URL; the ModelMap carries it to
    // the view, where it is read in the JSP as ${name}.
    @GetMapping("/say-hello-page")
    fun sayHelloPage(@RequestParam name: String, model: ModelMap): String {
        logger.debug("sayHelloPage called with name={}", name)
        model.addAttribute("name", name)
        return "sayHello"
    }
}