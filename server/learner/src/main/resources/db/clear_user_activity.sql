-- =============================================================
--  clear_user_activity.sql — delete ALL user history/activity
--  (attempt, sessions, list_progress, user_list) for every account,
--  keeping the account rows themselves and all catalog data
--  (language, lexeme, word_form, translation, list, list_item)
--  completely untouched.
--
--  Unlike wipe_except_basic.sql (also empties catalog data - lexeme,
--  word_form, translation, list, list_item) and wipe_all.sql (empties
--  language/account too), this is the "keep every account, forget
--  everything it ever did" reset - each user is left with just their
--  account row: no enrollments, no progress, no session history, no
--  attempt log. Safe to run with any number of real registered accounts,
--  not just the seeded demo user.
--
--  Run any time to clear activity for all users without touching account
--  identity or catalog data.
-- =============================================================
BEGIN;

TRUNCATE TABLE attempt, sessions, list_progress, user_list
  RESTART IDENTITY CASCADE;

COMMIT;
