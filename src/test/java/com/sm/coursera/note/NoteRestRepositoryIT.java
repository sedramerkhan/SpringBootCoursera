package com.sm.coursera.note;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Integration test for the Note REST API.
 *
 * Unlike a unit test (which exercises one class in isolation with mocked
 * collaborators), this launches the ENTIRE Spring Boot application:
 *   - @SpringBootTest               -> starts the full Spring context
 *   - WebEnvironment.RANDOM_PORT    -> on a free port (server.port=0), so it never
 *                                      clashes with a running app or with CI
 *   - @LocalServerPort              -> Spring injects the port that was chosen
 *
 * We then fire a real HTTP request through the whole web -> Spring Data REST ->
 * JPA -> H2 stack and assert on the response, exactly as a browser hitting
 * http://localhost:8080/api/notes/1 would see it.
 *
 * Note on the HTTP client: the in28minutes course uses TestRestTemplate, but that
 * class was REMOVED in Spring Boot 4. Its replacement is RestTestClient (Spring
 * Framework 7) — a fluent client with built-in response assertions. There is no
 * auto-configured, server-bound RestTestClient bean for @SpringBootTest, so we
 * build one against the live port in setUp().
 *
 * Note is exposed by NoteRestRepository (Spring Data REST), so the response is
 * HAL+JSON: the entity fields (title, content) plus a "_links" block — and NO
 * "id" in the body. We seed note id 1 in data.sql.
 *
 * The /api/** endpoints are permitAll() with CSRF disabled (see
 * SpringSecurityConfiguration), so no authentication is needed here.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class NoteRestRepositoryIT {

    private static final String NOTES_URL = "/api/notes";
    private static final String SPECIFIC_NOTE_URL = "/api/notes/1";

    @LocalServerPort
    private int port;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void retrieveSpecificNote_basicScenario() {
        // LENIENT means "the response must AT LEAST contain these fields" — extra
        // fields like the _links block are allowed and ignored, so the assertion
        // doesn't depend on the random port baked into the self link.
        String expected = """
                {
                    "title": "Spring Data REST",
                    "content": "Auto-exposes a repository as a REST API"
                }
                """;

        client.get().uri(SPECIFIC_NOTE_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.valueOf("application/vnd.hal+json"))
                .expectBody().json(expected, JsonCompareMode.LENIENT);
    }

    @Test
    void retrieveAllNotes_checksSeededIds() {
        // The collection endpoint returns a HAL document: the notes live under
        // _embedded.notes, and each note's id is exposed via its self link
        // (Spring Data REST keeps id out of the body). We assert there are exactly
        // the two seeded notes (data.sql) and that their ids are 1 and 2 — read
        // off the self href, since the numeric id never appears in the JSON.
        client.get().uri(NOTES_URL)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.valueOf("application/vnd.hal+json"))
                .expectBody()
                .jsonPath("$._embedded.notes.length()").isEqualTo(2)
                .jsonPath("$.page.totalElements").isEqualTo(2)
                // endsWith (not equals) so the assertion ignores the random port
                .jsonPath("$._embedded.notes[0]._links.self.href")
                .value(String.class, href -> assertThat(href).endsWith("/api/notes/1"))
                .jsonPath("$._embedded.notes[1]._links.self.href")
                .value(String.class, href -> assertThat(href).endsWith("/api/notes/2"));
    }

    @Test
    void createNote_returns201WithLocation() {
        String newNote = """
                {
                    "title": "Integration testing",
                    "content": "Launch the whole app and fire real HTTP requests"
                }
                """;

        // POST creates the note: Spring Data REST replies 201 Created with a
        // Location header pointing at the new resource. (It does not return a body
        // here, so we read back the content via a GET on the Location below.)
        URI location = client.post().uri(NOTES_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newNote)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .returnResult()
                .getResponseHeaders().getLocation();

        assertThat(location).isNotNull();
        assertThat(location.getPath()).matches("/api/notes/\\d+");

        try {
            // The created note is now retrievable at its Location, with the
            // title/content we posted.
            client.get().uri(location)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.title").isEqualTo("Integration testing")
                    .jsonPath("$.content").isEqualTo("Launch the whole app and fire real HTTP requests");
        } finally {
            // Always clean up, so this test doesn't perturb the seeded row count
            // that retrieveAllNotes_checksSeededIds relies on (RANDOM_PORT runs a
            // real server, so @Transactional rollback wouldn't apply here).
            client.delete().uri(location)
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }
}