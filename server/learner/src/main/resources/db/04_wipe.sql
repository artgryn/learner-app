-- =============================================================
--  04_wipe.sql — delete ALL data (catalog + user data), schema stays.
--  Unlike 03_reset.sql (which restores the 02_seed.sql demo fixture),
--  this leaves every table empty. Re-run 02_seed.sql afterwards to
--  repopulate, or nothing at all for a truly blank database.
-- =============================================================
BEGIN;

TRUNCATE TABLE
  attempt, sessions, list_progress, user_list, account,
  list_item, list, translation, word_form, lexeme, language
  RESTART IDENTITY CASCADE;

COMMIT;
