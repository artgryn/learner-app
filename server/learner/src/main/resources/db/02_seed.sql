-- =============================================================
--  02_seed.sql — "ett hus", "att gå", "jag" (sv+en)
--  + curated list, themed en/ett list, minimal user sample.
--  Matches revised 01_schema.sql (base_lang on user_list; enrollment-parented
--  sessions & progress).
-- =============================================================
BEGIN;

-- ---------- languages ----------
INSERT INTO language (code, name) VALUES ('sv','Swedish'), ('en','English');

-- ---------- lexemes ----------
INSERT INTO lexeme (id, lang, lemma, pos, gender, infl_class, tema, note, freq_rank) VALUES
  (1,'sv','hus','noun','ett','ZERO','{}', NULL, 600);
INSERT INTO lexeme (id, lang, lemma, pos, infl_class, tema, note, freq_rank) VALUES
  (3,'sv','gå','verb','strong',
   '{"present":"går","preteritum":"gick","supine":"gått","present_participle":"gående","past_participle":"gången"}',
   'to go, to walk', 80);
INSERT INTO lexeme (id, lang, lemma, pos, infl_class, note, freq_rank) VALUES
  (5,'sv','jag','pronoun','irregular','I (subject form)', 5);
INSERT INTO lexeme (id, lang, lemma, pos, freq_rank) VALUES (2,'en','house','noun', 500);
INSERT INTO lexeme (id, lang, lemma, pos, infl_class, tema, freq_rank) VALUES
  (4,'en','go','verb','irregular',
   '{"present_3sg":"goes","preteritum":"went","supine":"gone","present_participle":"going"}', 40);
INSERT INTO lexeme (id, lang, lemma, pos, freq_rank) VALUES (6,'en','I','pronoun', 3);

-- ---------- word forms ----------
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (1,'indef_sg','hus'), (1,'def_sg','huset'), (1,'indef_pl','hus'), (1,'def_pl','husen');
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (3,'infinitive','gå'), (3,'present','går'), (3,'preteritum','gick'),
  (3,'supine','gått'), (3,'imperative','gå'),
  (3,'present_participle','gående'), (3,'past_participle','gången');
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (5,'subject','jag'), (5,'object','mig'),
  (5,'possessive_c','min'), (5,'possessive_n','mitt'), (5,'possessive_pl','mina');
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (2,'sg','house'), (2,'pl','houses');
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (4,'infinitive','go'), (4,'present_3sg','goes'), (4,'preteritum','went'),
  (4,'supine','gone'), (4,'present_participle','going');
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (6,'subject','I'), (6,'object','me');

-- ---------- translations (undirected, a < b) ----------
INSERT INTO translation (lexeme_a, lexeme_b) VALUES (1,2), (3,4), (5,6);

-- ---------- lists ----------
INSERT INTO list (id, name, target_lang, user_id, allowed_exercises) VALUES
  (1, 'Swedish Basics', 'sv', NULL, NULL);
INSERT INTO list_item (list_id, lexeme_id, position) VALUES
  (1, 5, 1), (1, 3, 2), (1, 1, 3);
INSERT INTO list (id, name, target_lang, user_id, allowed_exercises) VALUES
  (2, 'En/Ett Nouns', 'sv', NULL, '{en_ett,translate}');
INSERT INTO list_item (list_id, lexeme_id, position) VALUES
  (2, 1, 1);

-- ============================= USER SAMPLE =============================
-- account no longer carries base_lang
INSERT INTO account (id, email) VALUES (1, 'demo@example.com');

-- enrollment carries the taught-from language (learn sv list FROM en)
INSERT INTO user_list (user_id, list_id, base_lang, status) VALUES
  (1, 1, 'en', 'active');

-- progress (parented to the enrollment above)
INSERT INTO list_progress (user_id, list_id, lexeme_id, times_practiced, correct, wrong) VALUES
  (1, 1, 5, 3, 3, 0),   -- jag: mastered
  (1, 1, 3, 1, 0, 1);   -- gå:  started, 1 wrong
-- hus: no row = not started

-- one completed session (parented to the enrollment)
INSERT INTO sessions (id, user_id, list_id, started_at, ended_at) VALUES
  (1, 1, 1, now() - interval '10 minutes', now());

-- attempts logged during that session
INSERT INTO attempt (user_id, list_id, lexeme_id, session_id, exercise_type, form_type, is_correct, elapsed_ms) VALUES
  (1, 1, 5, 1, 'translate',  'subject',    true,  1800),
  (1, 1, 5, 1, 'assemble',   'subject',    true,  4200),
  (1, 1, 5, 1, 'translate',  'object',     true,  1500),
  (1, 1, 3, 1, 'base_form',  'preteritum', false, 6100);

-- ---------- reset identity sequences ----------
SELECT setval(pg_get_serial_sequence('lexeme','id'),   (SELECT max(id) FROM lexeme));
SELECT setval(pg_get_serial_sequence('list','id'),     (SELECT max(id) FROM list));
SELECT setval(pg_get_serial_sequence('account','id'),  (SELECT max(id) FROM account));
SELECT setval(pg_get_serial_sequence('sessions','id'), (SELECT max(id) FROM sessions));

COMMIT;

-- =============================================================
--  sanity queries (run manually)
-- =============================================================
-- Enrollment language pair for user 1:
--   SELECT ul.base_lang AS from_lang, l.target_lang AS to_lang, l.name
--   FROM user_list ul JOIN list l ON l.id = ul.list_id WHERE ul.user_id = 1;
--
-- Forms of every word in "Swedish Basics", ordered:
--   SELECT li.position, lx.lemma, wf.form_type, wf.form
--   FROM list_item li JOIN lexeme lx ON lx.id=li.lexeme_id
--   JOIN word_form wf ON wf.lexeme_id=lx.id
--   WHERE li.list_id=1 ORDER BY li.position, wf.form_type;
--
-- User progress in Swedish Basics:
--   SELECT lx.lemma, p.times_practiced, p.correct, p.wrong
--   FROM list_progress p JOIN lexeme lx ON lx.id=p.lexeme_id
--   WHERE p.user_id=1 AND p.list_id=1;
