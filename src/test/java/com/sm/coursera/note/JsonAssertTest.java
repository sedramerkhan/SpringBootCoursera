package com.sm.coursera.note;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * A learning test for JSONAssert — NOT a test of our application.
 *
 * It needs no Spring context: we hard-code an "actual response" string (a HAL
 * note, exactly the shape NoteRestRepositoryIT gets back from GET /api/notes/1)
 * and explore how JSONAssert compares it against various "expected" strings.
 *
 * Why JSONAssert instead of assertEquals(expectedString, actualString)?
 *   1. A plain String compare is brutal: one stray space (text blocks love to
 *      add them) or a reordered field fails the test, and the diff is unreadable.
 *   2. JSONAssert compares JSON *semantically* — whitespace and field order don't
 *      matter — and on failure tells you the exact path that differs
 *      (e.g. "title Expected: X got: Y").
 *   3. strict=false lets you assert only the fields you care about and ignore the
 *      rest (volatile data like timestamps, or here the port-dependent _links).
 *
 * JSONAssert.assertEquals throws JSONException if a string isn't valid JSON, so
 * every test declares `throws JSONException`.
 *
 * It's pulled in transitively by the Spring Boot test starter
 * (org.skyscreamer:jsonassert).
 */
class JsonAssertTest {

    /** The actual response — a HAL note, including the _links block. */
    private static final String ACTUAL_RESPONSE = """
            {
                "title": "Spring Data REST",
                "content": "Auto-exposes a repository as a REST API",
                "_links": {
                    "self": { "href": "http://localhost:8080/api/notes/1" },
                    "note": { "href": "http://localhost:8080/api/notes/1" }
                }
            }
            """;

    @Test
    void strict_exactMatch_passes() throws JSONException {
        // strict=true: the two documents must match EXACTLY (every field present,
        // nothing extra). Whitespace and field order still don't matter.
        String expected = """
                {
                    "title": "Spring Data REST",
                    "content": "Auto-exposes a repository as a REST API",
                    "_links": {
                        "note": { "href": "http://localhost:8080/api/notes/1" },
                        "self": { "href": "http://localhost:8080/api/notes/1" }
                    }
                }
                """;
        JSONAssert.assertEquals(expected, ACTUAL_RESPONSE, true);
    }

    @Test
    void lenient_subset_passes() throws JSONException {
        // strict=false: assert only a SUBSET of fields. The _links block in the
        // actual response is simply ignored — this is what we want in the real
        // integration test, since _links carries a port-dependent URL.
        String expected = """
                {
                    "title": "Spring Data REST",
                    "content": "Auto-exposes a repository as a REST API"
                }
                """;
        JSONAssert.assertEquals(expected, ACTUAL_RESPONSE, false);
    }

    @Test
    void lenient_ignoresWhitespaceAndFieldOrder() throws JSONException {
        // Same data, squashed onto one line and reordered — still passes, because
        // JSONAssert compares structure, not text.
        String expected = "{\"content\":\"Auto-exposes a repository as a REST API\",\"title\":\"Spring Data REST\"}";
        JSONAssert.assertEquals(expected, ACTUAL_RESPONSE, false);
    }

    @Test
    void strict_failsOnSubset() {
        // The SAME subset that passes leniently FAILS under strict=true, because
        // the actual response has extra fields (content, _links). JSONAssert
        // signals a mismatch with an AssertionError naming what was unexpected.
        String expected = """
                {
                    "title": "Spring Data REST"
                }
                """;
        assertThrows(AssertionError.class,
                () -> JSONAssert.assertEquals(expected, ACTUAL_RESPONSE, true));
    }

    @Test
    void lenient_failsOnWrongValue() {
        // strict=false still fails if a field we DO assert has the wrong value —
        // and the error names the field, e.g. "title Expected: ... got: ...".
        String expected = """
                {
                    "title": "WRONG TITLE"
                }
                """;
        assertThrows(AssertionError.class,
                () -> JSONAssert.assertEquals(expected, ACTUAL_RESPONSE, false));
    }
}