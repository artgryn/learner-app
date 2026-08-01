-- =============================================================
--  03_reset.sql — wipe all USER progress, restore the 02_seed.sql
--  demo user exactly as seeded: enrolled, zero progress. Catalog data
--  (language, lexeme, word_form, translation, list, list_item) is
--  untouched: nothing in the app ever writes to it, so there is
--  nothing to reset there.
--
--  Run any time to undo whatever sessions/enrollments/attempts were
--  produced by exercising the API, back to the "default" fixture.
-- =============================================================
BEGIN;

-- RESTART IDENTITY resets account.id / sessions.id sequences too, so no
-- manual setval afterwards (unlike 02_seed.sql, which inserts explicit ids
-- into a table that already had rows from schema creation).
TRUNCATE TABLE attempt, sessions, list_progress, user_list, account
  RESTART IDENTITY CASCADE;

-- ============================= USER SAMPLE =============================
-- account no longer carries base_lang
INSERT INTO account (id, email, name, ui_lang, learn_base_lang, learn_target_lang, status)
  VALUES (1, 'demo@example.com', 'Demo', 'en', 'en', 'sv', 'free');

-- enrollment carries the taught-from language (learn sv list FROM en)
-- no list_progress / sessions / attempt rows: a fresh enrollment starts
-- with every word "not started" (no list_progress row = gets an intro card).
INSERT INTO user_list (user_id, list_id, base_lang, status) VALUES
  (1, 1, 'en', 'active');

COMMIT;
