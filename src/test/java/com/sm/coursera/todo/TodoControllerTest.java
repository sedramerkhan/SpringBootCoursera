package com.sm.coursera.todo;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web-layer UNIT test (slice test) for TodoController.
 *
 * Contrast with NoteRestRepositoryIT (an integration test):
 *   - @SpringBootTest launches the WHOLE app (every layer, real DB);
 *   - @WebMvcTest launches ONLY the web layer for the named controller — no
 *     service beans, no repositories, no database. Far faster, and it tests the
 *     controller in isolation.
 *
 * TodoController is web layer; it depends on TodoService (business layer). In a
 * slice test we DON'T want the real service (it would drag in the repository and
 * H2), so we replace it with a Mockito mock and tell that mock exactly what to
 * return. This is the Controller -> Service -> Repository stack with the bottom
 * two layers mocked away.
 *
 * Two Spring Boot 4 changes from the in28minutes course:
 *   - @WebMvcTest moved to org.springframework.boot.webmvc.test.autoconfigure.
 *   - @MockBean was REMOVED; its replacement is @MockitoBean (Spring Framework 7,
 *     org.springframework.test.context.bean.override.mockito). Without it the
 *     context fails to start: "No qualifying bean of type 'TodoService'", because
 *     the controller needs a TodoService the slice doesn't provide.
 *
 * addFilters = false skips the Spring Security filter chain so we don't get
 * redirected to the login page; we supply the authenticated user by hand via the
 * request's Principal (which is all this controller reads).
 */
@WebMvcTest(TodoController.class)
@AutoConfigureMockMvc(addFilters = false)
class TodoControllerTest {

    private static final String USERNAME = "Sedra";
    private static final Principal PRINCIPAL = () -> USERNAME;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Test
    void listTodos_showsUsersTodos() throws Exception {
        // Stub the business layer: when asked for this user's todos, return two.
        when(todoService.findByUsername(USERNAME)).thenReturn(List.of(
                new Todo(1, USERNAME, "Learn Spring Boot", LocalDate.now().plusDays(10), false),
                new Todo(2, USERNAME, "Learn Spring MVC", LocalDate.now().plusDays(20), false)));

        mockMvc.perform(get("/todos").principal(PRINCIPAL))
                .andExpect(status().isOk())
                .andExpect(view().name("todo/listTodos"))
                .andExpect(model().attribute("name", USERNAME))
                .andExpect(model().attribute("todos", hasSize(2)));
    }

    @Test
    void showEditTodo_whenNotFound_redirectsToList() throws Exception {
        // We deliberately DON'T stub findById, so the mock returns null — the
        // controller's "not found" path. This mirrors the course's 404 scenario,
        // where an unstubbed mock returning null drives the error branch; here the
        // controller redirects back to the list instead of returning 404.
        mockMvc.perform(get("/todos/{id}/edit", 99).principal(PRINCIPAL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos"));
    }
}