package com.sm.coursera.note;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pure Mockito UNIT test for the Note layer — the "Mock to test GET and POST"
 * exercise applied to Notes.
 *
 * WHY THIS LOOKS DIFFERENT FROM THE COURSE'S SurveyResource TEST
 * --------------------------------------------------------------
 * The in28minutes SurveyResource test (and our own TodoControllerTest) is a
 * MockMvc slice test: a real @RestController depends on a service, we replace the
 * service with a Mockito mock (@MockitoBean), fire an HTTP request through
 * MockMvc, and assert the JSON that comes back.
 *
 * Note has NO custom controller. It is exposed entirely by Spring Data REST from
 * NoteRestRepository (see that interface) — the GET/POST endpoints are generated
 * by the framework, there is no @RestController or service class of ours to unit
 * test, and those generated endpoints are NOT part of the @WebMvcTest web slice.
 * The component we actually own and can mock is the repository itself.
 *
 * So this test demonstrates the SAME Mockito mechanic the transcript teaches —
 * when(mock.method(args)).thenReturn(bean) to stub, then verify(...) to confirm
 * the interaction — directly against NoteRestRepository:
 *   - GET   -> repository.findById(id)
 *   - POST  -> repository.save(note)
 *
 * No Spring context and no database are started (@ExtendWith(MockitoExtension)
 * only wires the @Mock / @InjectMocks fields), so it is fast and isolated. For an
 * end-to-end check of the real generated HTTP endpoints, see NoteRestRepositoryIT.
 */
@ExtendWith(MockitoExtension.class)
class NoteRestRepositoryMockTest {

    @Mock
    private NoteRestRepository repository;

    // ----------------------------- GET -----------------------------

    @Test
    void getNote_whenFound_returnsTheStubbedNote() {
        // Build the bean we want the mock to hand back — just like the course
        // populates a SpecificSurveyQuestion as the stubbed return value.
        Note note = new Note(1, "Spring Data REST", "Auto-exposes a repository as a REST API");

        // when this method is called with id 1, thenReturn our note.
        when(repository.findById(1)).thenReturn(Optional.of(note));

        Optional<Note> result = repository.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Spring Data REST");
        assertThat(result.get().getContent()).isEqualTo("Auto-exposes a repository as a REST API");

        // Confirm the read actually happened, exactly once.
        verify(repository, times(1)).findById(1);
    }

    @Test
    void getNote_whenMissing_returnsEmpty() {
        // We deliberately DON'T stub findById(99); a Mockito mock returns its
        // default for an unstubbed call, which for Optional is Optional.empty() —
        // the "not found" path (mirrors the course's 404 scenario, where an
        // unstubbed mock drives the error branch).
        Optional<Note> result = repository.findById(99);

        assertThat(result).isEmpty();
        verify(repository).findById(99);
    }

    // ----------------------------- POST ----------------------------

    @Test
    void createNote_savesAndReturnsPersistedNote() {
        // Incoming note has no id yet (id = 0), as a POST body would.
        Note incoming = new Note(0, "Integration testing", "Launch the whole app and fire real HTTP requests");
        // After save, the DB would assign an id; the stub stands in for that.
        Note persisted = new Note(42, "Integration testing", "Launch the whole app and fire real HTTP requests");

        when(repository.save(incoming)).thenReturn(persisted);

        Note result = repository.save(incoming);

        assertThat(result.getId()).isEqualTo(42);
        assertThat(result.getTitle()).isEqualTo("Integration testing");

        // ArgumentCaptor lets us assert WHAT was passed to save(), not just that
        // it was called — the title/content we intended to persist.
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("Integration testing");
        assertThat(captor.getValue().getContent())
                .isEqualTo("Launch the whole app and fire real HTTP requests");
    }

    @Test
    void getNote_doesNotWrite() {
        // A GET must never call save(): verify with never() that the read path
        // leaves the repository untouched for writes.
        when(repository.findById(1)).thenReturn(Optional.of(new Note(1, "t", "c")));

        repository.findById(1);

        verify(repository, never()).save(any(Note.class));
    }
}
