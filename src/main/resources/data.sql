-- Seed data, loaded on startup after Hibernate creates the schema
-- (spring.jpa.defer-datasource-initialization=true).
--
-- The class Todo maps to table "todo"; "targetDate" maps to column
-- "target_date" (Spring Boot's camelCase -> snake_case naming strategy).
--
-- We deliberately omit the id column so the IDENTITY counter assigns 1, 2, 3
-- and keeps advancing — inserting explicit ids would let the next app-created
-- todo collide on the primary key.
INSERT INTO todo (username, description, target_date, done) VALUES ('Sedra', 'Learn Spring Boot', '2026-09-23', false);
INSERT INTO todo (username, description, target_date, done) VALUES ('Sedra', 'Learn Spring MVC', '2026-12-23', false);
INSERT INTO todo (username, description, target_date, done) VALUES ('Sedra', 'Learn JSP & JSTL', '2026-07-23', true);

-- Notes for the Spring Data REST demo (entity Note -> table "note"). Exposed at
-- /api/notes by NoteRestRepository.
INSERT INTO note (title, content) VALUES ('Spring Data REST', 'Auto-exposes a repository as a REST API');
INSERT INTO note (title, content) VALUES ('Paging & sorting', 'Use ?page, ?size and ?sort on the collection');