-- =============================================================
--  02_seed.sql  — "ett hus" and "att gå" in sv / en / ru
--  Explicit IDs for readable FKs; sequences reset at the end.
-- =============================================================
BEGIN;

-- ---------- languages ----------
INSERT INTO language (code, name) VALUES
  ('sv','Swedish'), ('en','English'), ('ru','Russian');

-- =============================================================
--  LEXEMES
-- =============================================================
-- ett hus ------------------------------------------------------
INSERT INTO lexeme (id, lang, lemma, pos, gender, infl_class, tema) VALUES
  (1,'sv','hus',  'noun','ett','ZERO','{}');            -- indef_pl = indef_sg
INSERT INTO lexeme (id, lang, lemma, pos, infl_class, tema) VALUES
  (2,'en','house','noun','regular','{}');
INSERT INTO lexeme (id, lang, lemma, pos, infl_class, tema) VALUES        -- ru masc, 2nd decl
  (3,'ru','дом',  'noun','decl_m_hard','{"nom_pl":"дома"}'); -- stressed-plural irregularity

-- att gå -------------------------------------------------------
INSERT INTO lexeme (id, lang, lemma, pos, infl_class, tema) VALUES
  (4,'sv','gå','verb','strong',
   '{"present":"går","preteritum":"gick","supine":"gått","present_participle":"gående"}');
INSERT INTO lexeme (id, lang, lemma, pos, infl_class, tema) VALUES
  (5,'en','go','verb','irregular',
   '{"present_3sg":"goes","preteritum":"went","supine":"gone","present_participle":"going"}');
INSERT INTO lexeme (id, lang, lemma, pos, aspect, infl_class, tema) VALUES
  (6,'ru','идти','verb','imperfective','motion_unidir',
   '{"past_m":"шёл","past_f":"шла","past_n":"шло","past_pl":"шли"}');   -- suppletive past
INSERT INTO lexeme (id, lang, lemma, pos, aspect, infl_class, tema) VALUES
  (7,'ru','пойти','verb','perfective','motion_unidir',
   '{"past_m":"пошёл","past_f":"пошла","past_pl":"пошли"}');            -- perfective partner

-- ---------- aspect pair (идти ↔ пойти) ----------
INSERT INTO aspect_pair (imperfective, perfective) VALUES (6, 7);

-- =============================================================
--  WORD FORMS
-- =============================================================
-- hus (sv): ZERO plural → indef_pl equals indef_sg
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (1,'indef_sg','hus'), (1,'def_sg','huset'),
  (1,'indef_pl','hus'), (1,'def_pl','husen');

-- house (en)
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (2,'sg','house'), (2,'pl','houses');

-- дом (ru): 6 cases × 2 numbers
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (3,'nom_sg','дом'),   (3,'gen_sg','дома'),  (3,'dat_sg','дому'),
  (3,'acc_sg','дом'),   (3,'ins_sg','домом'), (3,'prep_sg','доме'),
  (3,'nom_pl','дома'),  (3,'gen_pl','домов'), (3,'dat_pl','домам'),
  (3,'acc_pl','дома'),  (3,'ins_pl','домами'),(3,'prep_pl','домах');

-- gå (sv)
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (4,'infinitive','gå'), (4,'present','går'), (4,'preteritum','gick'),
  (4,'supine','gått'),   (4,'imperative','gå'), (4,'present_participle','gående');

-- go (en)
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (5,'infinitive','go'), (5,'present_3sg','goes'), (5,'present_participle','going'),
  (5,'preteritum','went'), (5,'supine','gone');

-- идти (ru): present (6) + suppletive past (4) + imperative (2)
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (6,'infinitive','идти'),
  (6,'present_1sg','иду'),  (6,'present_2sg','идёшь'), (6,'present_3sg','идёт'),
  (6,'present_1pl','идём'), (6,'present_2pl','идёте'), (6,'present_3pl','идут'),
  (6,'past_m','шёл'), (6,'past_f','шла'), (6,'past_n','шло'), (6,'past_pl','шли'),
  (6,'imperative_sg','иди'), (6,'imperative_pl','идите');

-- пойти (ru, perfective): "present-shaped" forms carry FUTURE meaning
INSERT INTO word_form (lexeme_id, form_type, form) VALUES
  (7,'infinitive','пойти'),
  (7,'future_1sg','пойду'), (7,'future_3sg','пойдёт'), (7,'future_3pl','пойдут'),
  (7,'past_m','пошёл'), (7,'past_f','пошла'), (7,'past_pl','пошли'),
  (7,'imperative_sg','пойди'), (7,'imperative_pl','пойдите');

-- =============================================================
--  SENSES  (each in its OWN language; gloss_dev is a dev label)
-- =============================================================
-- hus: monosemous
INSERT INTO sense (id, lexeme_id, sense_num, gloss, gloss_dev) VALUES
  (10, 1, 1, 'byggnad som man bor eller arbetar i', 'building for living/working');
INSERT INTO sense (id, lexeme_id, sense_num, gloss, gloss_dev) VALUES
  (11, 2, 1, 'a building for people to live or work in', 'house');
INSERT INTO sense (id, lexeme_id, sense_num, gloss, gloss_dev) VALUES
  (12, 3, 1, 'здание для жилья; тж. дом как жилище', 'house / home');

-- gå: polysemous → TWO senses
INSERT INTO sense (id, lexeme_id, sense_num, gloss, gloss_dev) VALUES
  (20, 4, 1, 'förflytta sig till fots',        'move on foot'),
  (21, 4, 2, 'fungera, avlöpa (det går bra)',  'function / turn out');
INSERT INTO sense (id, lexeme_id, sense_num, gloss, gloss_dev) VALUES
  (22, 5, 1, 'move or travel from one place to another', 'go / move'),
  (23, 5, 2, 'proceed or turn out (how did it go)',      'go / turn out');
INSERT INTO sense (id, lexeme_id, sense_num, gloss, gloss_dev) VALUES
  (24, 6, 1, 'двигаться пешком в одном направлении', 'go on foot, one direction');

-- =============================================================
--  TRANSLATION EDGES  (directed sense→sense, typed)
-- =============================================================
-- hus → house : exact ; hus → дом : broader (дом also = home)
INSERT INTO translation (from_sense, to_sense, equivalence, usage_note) VALUES
  (10, 11, 'exact',   NULL),
  (10, 12, 'broader', 'ryska "дом" täcker även "hem", inte bara byggnaden');

-- gå#1 (on foot) → go : broader (go = any mode) ; → идти : approximate
INSERT INTO translation (from_sense, to_sense, equivalence, usage_note) VALUES
  (20, 22, 'broader',     'English "go" covers all modes; "walk" is the exact match'),
  (20, 24, 'approximate', 'идти = pesky specific: on foot, one direction, in progress');

-- gå#2 (turn out) → go#2 : approximate (no clean Russian 1-word link here)
INSERT INTO translation (from_sense, to_sense, equivalence, usage_note) VALUES
  (21, 23, 'approximate', 'idiomatic "det går bra" ≈ "it is going well"');

-- =============================================================
--  fix identity sequences after explicit-id inserts
-- =============================================================
SELECT setval(pg_get_serial_sequence('lexeme','id'), (SELECT max(id) FROM lexeme));
SELECT setval(pg_get_serial_sequence('sense','id'),  (SELECT max(id) FROM sense));

COMMIT;

-- =============================================================
--  sanity queries (run manually)
-- =============================================================
-- All forms of the Swedish verb gå:
--   SELECT form_type, form FROM word_form WHERE lexeme_id = 4 ORDER BY form_type;
-- What does Swedish "hus" translate to, with equivalence + note:
--   SELECT l.lang, l.lemma, t.equivalence, t.usage_note
--   FROM translation t
--   JOIN sense s2 ON s2.id = t.to_sense
--   JOIN lexeme l ON l.id = s2.lexeme_id
--   WHERE t.from_sense = 10;
-- Reverse (what maps TO Russian дом): uses translation_to_idx
--   SELECT from_sense FROM translation WHERE to_sense = 12;
