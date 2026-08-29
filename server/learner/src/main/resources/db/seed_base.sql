-- =============================================================
--  seed_base.sql — foundational reference data (languages + base account)
--
--  This is the data the app needs to FUNCTION, independent of any learning
--  content or user progress. It is IDEMPOTENT (ON CONFLICT DO NOTHING), so it
--  is safe to run anytime — on a fresh DB, or to restore the base after
--  wipe_all.sql / wipe_except_basic.sql.
--
--  Run order:  01_schema.sql  ->  seed_base.sql  ->  (content seeds, e.g.
--  02_seed.sql or seed_first50.sql)
-- =============================================================
BEGIN;

-- ---------- languages (reference data) ----------
INSERT INTO language (code, name) VALUES
  ('en','English'),
  ('sv','Swedish')
  -- add more supported languages here as they come online:
  -- ,('uk','Ukrainian')
  -- ,('de','German')
  -- ,('es','Spanish')
ON CONFLICT (code) DO NOTHING;

-- ---------- base account ----------
-- Default account so the app is usable immediately (auth is stubbed; this is the
-- account StubCurrentUserProvider resolves to). ui_lang / learn_base_lang /
-- learn_target_lang FK to language(code) above.
INSERT INTO account (id, email, name, ui_lang, learn_base_lang, learn_target_lang, status)
VALUES (1, 'demo@example.com', 'Demo', 'en', 'en', 'sv', 'free')
ON CONFLICT (email) DO NOTHING;

-- keep the identity sequence ahead of the explicit id
SELECT setval(pg_get_serial_sequence('account','id'), (SELECT max(id) FROM account));

COMMIT;
