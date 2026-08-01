# CLAUDE.md — project context

This file gives Claude Code the full context for this project. Read it before
answering. When a decision here conflicts with an instinct, follow this file —
these decisions were made deliberately and have rationale behind them.

## What this is

A mobile vocabulary-learning app. The goal is to learn 3000–5000 words of a
target language to a durable, usable level. The distinctive premise: in many
languages "one word" is really several — Swedish nouns carry en/ett gender and
inflect (hus/huset/hus/husen); verbs inflect (gå/går/gick/gått). So the app
teaches not just meaning but **gender and all word forms**, and exercises are
built around producing and recognizing those forms.

- Client: React Native (mobile, iPhone-first).
- Server: Spring Boot 4+, Java 25, PostgreSQL. Spring Security (JWT) later.
- App-only API. No public API.
- MVP scope: Swedish learned from English. No AI. Pre-defined curated word lists.

## Core architectural decisions (do not relitigate without reason)

**Database: PostgreSQL, not a document store.** The data is relational —
translation is a many-to-many over shared word entities, and the core reads are
joins and COUNT(DISTINCT) aggregates. `jsonb` covers the one document-shaped
field (`tema`), so nothing is lost.

**One word = one lexeme + many word_forms (store-vs-derive).** A word is ONE
`lexeme` row. Its inflected forms are `word_form` rows. Irregular forms are
stored in `lexeme.tema` (jsonb); regular forms are derived from `infl_class`.
Never model the forms of one word as separate words. `tema` is the irreducible
input; `word_form` is the regenerable, queryable output.

**Translation attaches to the lexeme, not the form, and is language-symmetric.**
`translation` is an undirected pairwise link between two `lexeme` rows, stored
once with a canonical ordering (`lexeme_a < lexeme_b`) and read both directions.
Every lexeme belongs to exactly one language (`lang` column). No language is
privileged — Swedish is just the first one loaded. Target is 4–6 languages
eventually; pairwise (not a concept hub) is correct at that scale.

**Learning is list-scoped.** Progress is keyed (user, list, lexeme). The same
word in two lists is learned twice, independently. This is deliberate.

**Sessions are derived on demand, never precomputed.** A session = "what's due +
new words, sliced to a 6–10 min budget", computed fresh each time. Only COMPLETED
sessions are logged (`sessions` table). "N sessions total" is an estimate
(`ceil(list_size / words_per_session)`); "M done" counts logged sessions.

**Grading is client-side.** Exercises are generated fully-built on the server
(question + options + correct answer) and shipped to the client. The client
grades locally and submits results for tracking. The server trusts `isCorrect`
and logs it. This enables offline sessions and instant feedback. Cheating only
hurts the learner (no scores/money), so exposing the answer is an accepted trade.

**Write pattern per answered exercise: 1 INSERT (attempt) + 1 UPSERT
(list_progress).** `attempt` is an append-only log and the source of truth;
`list_progress` is a maintained summary derived from it. On list completion, raw
attempts may be pruned (keep the completion record).

**Option A scheduling (MVP):** progress counter lives on the lexeme, not per
form. Forms drive exercise VARIETY across a word's spaced encounters, not extra
counters. `attempt.form_type` logs which form was tested, so per-form scheduling
(Option B) stays possible later without data loss.

## Data model (10 tables)

Language data: `language`, `lexeme`, `word_form`, `translation`, `list`,
`list_item`.
User data: `account`, `user_list` (enrollment), `list_progress`, `sessions`,
`attempt`.
`learning_plan` is computed on demand — NOT a table.

Key relationships and rules:
- `lexeme(id, lang, lemma, pos, gender, infl_class, tema jsonb DEFAULT '{}',
  note, freq_rank)`. UNIQUE(lang, lemma, pos). `gender` = en/ett for Swedish
  nouns. `note` carries translation nuance ("to go, to walk").
- `word_form(lexeme_id, form_type, form)`, PK(lexeme_id, form_type). `form_type`
  is OPEN TEXT, not an enum — inventories differ per language (Swedish ~15,
  Ukrainian ~20 incl. cases + vocative). Catalog is documented, not constrained.
- `translation(lexeme_a, lexeme_b)`, CHECK(lexeme_a < lexeme_b).
- `list(id, name, target_lang, user_id NULL=curated, allowed_exercises
  exercise_type[] NULL=all)`.
- `exercise_type` IS a native Postgres enum (small, closed, app-controlled):
  en_ett, assemble, translate, base_form, produce_form, multi_select.
- `account(id, email, password_hash, name, ui_lang DEFAULT 'en', learn_base_lang,
  learn_target_lang, status DEFAULT 'free', created_at)`. `password_hash` (BCrypt)
  is set only by `POST /auth/register` / `/auth/reset/confirm` — auth stays
  otherwise stubbed (login never checks it); never returned by any DTO.
  `learn_base_lang`/`learn_target_lang` are the account's DEFAULT learning pair
  (catalog filter + enroll pre-fill), validated against `app.language-pairs`
  config (`LanguagePairService`) — NOT the per-enrollment truth, that's
  `user_list.base_lang`. `status` is a billing tier ('free'/'paid'), not
  user-editable, no billing logic yet.
- `user_list(user_id, list_id, base_lang, status, started_at, completed_at)`,
  PK(user_id, list_id). `base_lang` = taught-from language, PER ENROLLMENT (NOT
  on account — a user may learn from different languages per list). `status`
  is 'active' | 'completed'. **At most one 'active' row per user** — enforced
  both in `EnrollmentService` AND at the DB level (`one_active_enrollment`
  partial unique index, `01_schema.sql`). Enrolling a different list while one
  is active DELETES it (cascades progress/sessions) — destructive, UI warns
  first. Completion is automatic/server-computed (`EnrollmentService.
  completeIfMastered`, checked after every `/sessions/{id}/complete`): all
  words mastered flips `status` to 'completed' WITHOUT touching
  `list_progress` (only `attempt` rows are ever prunable). Re-enrolling a
  completed list REACTIVATES the same row (resume/review with mastery intact),
  not a new row and not a progress wipe — see `doc/app-info/Data/user_list.md`.
- `list_progress` and `sessions` FK their (user_id, list_id) to `user_list`
  (the enrollment) — neither can exist without enrollment.
- `attempt` is denormalized (individual FKs) for query speed, append-only.

Citation form (shown when presenting a bare lexeme) is derived by pos convention:
noun→indef_sg, verb→infinitive, pronoun→subject, number→base, adjective→utrum.
No stored flag.

## Session items and interleaving

- A session response is an ordered `items` array, NOT a bare exercise list. Each
  item is discriminated by `itemType`: `introduce` (a no-action teaching card) or
  `exercise` (a graded task).
- **`introduce` is an itemType, NOT an exerciseType.** It never enters the
  `exercise_type` Postgres enum, is never graded, and never produces an `attempt`
  or `list_progress` row. Viewing a card writes nothing.
- An introduce card is assembled from lexeme + word_form + translation (composed
  citation `word` with article/marker, translation in the enrollment base_lang,
  full labelled paradigm). NO new table/column — it is a read/generation concern.
- A word gets an intro card when it has no `list_progress` row yet ("not started").
  Its first real exercise creates that row.
- **The ORDER of items encodes the pedagogy** and is a server-side scheduling
  decision the client must not reorder: intro card just-in-time before a word's
  first task; that word's tasks then spaced and INTERLEAVED with other words'
  tasks. Do NOT bunch all of one word's tasks after its intro (that is massed
  practice; the point is spacing). Reference flow for 3 words:
  w1-intro -> w1-t1 -> w1-t2 -> w2-intro -> w2-t1 -> w1-t3 -> w2-t2 -> w3-intro ...
  This is the spec for session-building ("split list into sessions").
- Item identifier is `itemId`. Results submitted in /complete reference `itemId`
  and come only from exercise items (intro cards are never submitted).

## Exercise generation rules

- Exercises are server-generated and fully built. Client renders + reports only.
- Each exercise item = envelope (itemId, itemType=exercise, exerciseType, lexemeId,
  formType) + prompt {text, lang} + exercise {type-specific payload + correct answer}.
- `lang` lives in `prompt` (and `optionsLang` in the payload when the answer side
  differs) — NEVER at root, because a translation exercise spans two languages.
- Instruction labels are constant-per-type and localized CLIENT-side — never sent
  in the payload. Payload carries instance data only.
- Shuffling (letters, options) is ALWAYS server-side so answer position leaks
  nothing.
- assemble: letters are longer than the answer (~1.5x), shuffled, may contain
  duplicates; treat as an ordered array (positional). Decoy letters from the
  target-language alphabet (include å/ä/ö for Swedish).
- Distractors for choice types: same target-language, same pos, near freq_rank,
  excluding the answer. Mixed-pos distractors are too easy — avoid.
- Which types a word supports is a data precondition (en_ett needs pos=noun +
  gender; assemble/produce_form need a word_form). Generated set = list's
  allowed_exercises ∩ what the word supports. Skip a word that qualifies for
  nothing rather than failing.
- List coherence (every word supports ≥1 of the list's allowed_exercises) is
  validated at data-load time, not enforced by schema.

## API (see openapi.yaml)

- REST, JSON, camelCase. Plural resource nouns.
- JWT bearer auth. USER ID COMES FROM THE TOKEN SUBJECT — never from path/body.
  "My" endpoints (/me/home, /enrollments) carry no user id in the URL.
- Access token + refresh token.
- Core loop: POST /enrollments/{listId}/sessions (derive session, returns
  exercises WITH answers) → client grades offline → POST
  /sessions/{sessionId}/complete (carries results batch; writes attempts +
  progress + one sessions row in a transaction).
- GET /me/home is the aggregate launch call (profile + enrollments + progress +
  resume pointer). No half-session is restored from the server; resume = reopen
  last-active list and generate the next session.
- Errors: { error: { status, code, message, traceId } }. Clients branch on
  `code` (stable string), never on `message`.

**Current implementation status (as of 2026-07-28): auth is intentionally not
built yet.** `AuthController` is a stub (canned `TokenPair`, no real
register/login/refresh/logout) and there is no `security/` package content —
do not touch either while implementing other features; this is a deliberate,
user-directed sequencing decision, not an oversight. Endpoints that the API
design says should read the user id from the token instead resolve it via
`CurrentUserProvider` (`service/CurrentUserProvider.java`), a one-method seam
with a single implementation, `StubCurrentUserProvider`, that always returns
the seeded demo account id. Every controller/service needing "current user"
should depend on `CurrentUserProvider`, never a hardcoded id inline — when
real auth lands, only `StubCurrentUserProvider` gets replaced (with a
`SecurityContext`/token-subject-backed implementation); no call site changes.

**Endpoint implementation status:** Catalog, Progress, and Enrollment are
real, DB-backed. Auth is a stub. Session (`/enrollments/{listId}/sessions`,
`/sessions/{sessionId}/*`) has scaffolding only (reverted 2026-07-28 after a
full implementation was tried and rolled back — see git history if the
generation logic needs re-deriving): `SessionController` delegates to
`SessionService`, which has `ListItemRepository`/`ListProgressRepository`/
`WordFormRepository`/`LexemeRepository`/`TranslationRepository`/
`AttemptRepository`/`LearningSessionRepository`/`AccountRepository`/
`WordListRepository`/`EnrollmentService` wired into its constructor, but
every method still returns the same canned `SessionResponse`/
`CompleteResponse`/`WordProgress` values the controller used to hardcode
directly — see the `// TODO` on each method.

Enrollment specifics: `/lists/{listId}/enroll` is idempotent by
(userId, listId) — `EnrollmentService.enroll` returns `EnrollResult(enrollment,
created)` so the controller can answer 200 (existing) vs 201 (created), per
`doc/api/swagger.yaml`. `DELETE /enrollments/{listId}` deletes the `user_list`
row outright (cascades to `sessions`/`list_progress` via the DB's own FK
`ON DELETE CASCADE`); `attempt` rows are deliberately left behind as a
denormalized historical log, independent of enrollment lifecycle (see
`doc/app-info/Data/attempt.md`). The `UserList` → `Enrollment` DTO mapping
(wordsMastered, sessions summary, lastActiveAt) is shared between
`EnrollmentController` and `ProgressController` via `EnrollmentMapper`
(`data/mappers/EnrollmentMapper.java`), which itself delegates the derived
fields to `ProgressService` — don't duplicate that mapping in a third place.

## Reference files in this repo

- `openapi.yaml` — full API spec (OpenAPI 3.1), source of truth for endpoints and
  payload schemas.
- `01_schema.sql` — the PostgreSQL DDL. `02_seed.sql` — sample data (ett hus,
  att gå, jag in sv+en, plus a curated list and a user sample).
- The `.md` files (lexeme, word_form, translation, list, list_item, account,
  user_list, list_progress, sessions, attempt, etc.) are the design knowledge
  base — per-table specs with rationale.

## Working guidelines for Claude

- When implementing, honor the decisions above. If something here seems wrong,
  say so and explain — don't silently deviate.
- Prefer generating forms from `infl_class` + `tema` over hand-authoring, but the
  seed inlines forms because it can't call the generator.
- Keep UI copy and localization on the client; keep grading logic on the client;
  keep exercise generation, shuffling, and distractor selection on the server.
- form_type is open text; exercise_type is a closed enum. Respect that asymmetry.
- Don't reintroduce a sense/concept layer, per-account base_lang, or precomputed
  sessions — all were considered and deliberately dropped.
- Swedish specifics: cite nouns as "en/ett + lemma", verbs as "att + lemma";
  never surface a bare noun without its article.
- Don't implement auth/security (see "Current implementation status" above).
  Need the current user? Inject `CurrentUserProvider`, don't hardcode an id
  or add ad-hoc auth logic to get one.
