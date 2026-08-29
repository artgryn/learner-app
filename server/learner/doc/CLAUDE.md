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

## Auth-adjacent infrastructure (built, even though core auth is still stubbed)

Everything below is implemented, despite login/refresh/token verification
themselves staying stubbed (see "Current implementation status"). Don't treat
account/email/reset as unbuilt — only the JWT/password-verification core is.

- **Account CRUD** (`AccountController` → `AccountService`, `/me`): `GET /me`,
  `PATCH /me` (name, ui_lang, learning pair), `DELETE /me`. `POST
  /auth/register` sets email only; name + learning pair are set by a follow-up
  `PATCH /me` (`init_account`), not at registration.
- **Language pairs from config, not DB**: `GET /language-pairs`
  (`CatalogController`) returns supported (base, target) pairs from
  `LanguagePairsProperties` (`app.language-pairs` in `application.yml`). A
  TEMPORARY stand-in for coverage-derived pairs (the real constraint is
  translation coverage in the data) — fine for MVP. Do not add a pairs table.
- **Password reset / email confirmation** (`AuthController`): `POST
  /auth/reset/request` (4-digit code via `VerificationCodeStore`, no DB
  table), `POST /auth/reset/confirm` (code or signed token — no password is
  actually stored, auth stays stubbed), `POST /auth/reset/link` (stateless
  `ResetLinkTokenService`-signed link + a fraud-notification email).
  Registration uses the same 4-digit-code mechanism.
- **Email subsystem**: `EmailService` interface with two implementations
  switched by `app.mail.enabled` — `SmtpEmailService` (real SMTP) when true,
  `LoggingEmailService` (logs instead of sending) by default/false. Not a
  controller; called from `AuthController` flows only.
- **Single-active-list rule**: enforced in `EnrollmentService.enroll` —
  `deleteOtherActiveEnrollment` removes any other active `user_list` row for
  the user before creating/reactivating one (cascades list_progress/sessions).
  Application-level, not a DB constraint.

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
- `account(id, email, name, ui_lang DEFAULT 'en', learn_base_lang,
  learn_target_lang, status DEFAULT 'free', created_at)`. The learning pair
  (learn_base_lang + learn_target_lang, two separate FK→language columns) is the
  user's DEFAULT: it filters the catalog and pre-fills user_list.base_lang at enroll.
  It is NOT the learning truth — user_list.base_lang is (per-enrollment).
- `user_list(user_id, list_id, base_lang, status, started_at, completed_at)`,
  PK(user_id, list_id). `base_lang` = taught-from language, PER ENROLLMENT (pre-filled
  from the account default pair). One active list per user (MVP, app-enforced).
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
- A word gets an intro card when `SessionScheduler.selectCandidates` picked it as
  NEW (no `list_progress` row yet). Its first real exercise creates that row.
- **The ORDER of items encodes the pedagogy** and is a server-side scheduling
  decision the client must not reorder — `SessionService.interleave` does
  ROUND-ROBIN: one exercise per active word per round, so the same word's
  exercises are never adjacent. A new word's intro card is emitted together with
  (immediately before) its first exercise, in whichever round that word is first
  visited — not necessarily round 1, since words are picked review-first, then new
  (`SessionScheduler.selectCandidates`). Reference flow for 3 words all starting in
  round 1: w1-intro -> w1-t1 -> w2-intro -> w2-t1 -> w3-t1 -> w1-t2 -> w2-t2 ...
  (round continues until every word's exercise queue is drained; a word with fewer
  exercises just drops out of later rounds).
- Item identifier is `itemId`. Results submitted in /complete reference `itemId`
  and come only from exercise items (intro cards are never submitted).

## Exercise generation rules

- Exercises are server-generated and fully built. Client renders + reports only.
- Each exercise item = envelope (itemId, itemType=exercise, exerciseType,
  lexemeId, formType) + prompt {text, lang} + exercise {type-specific payload
  + correct answer}.
- `lang` lives in `prompt` (and `optionsLang` in the payload when the answer side
  differs) — NEVER at root, because a translation exercise spans two languages.
- Instruction labels are constant-per-type and localized CLIENT-side — never sent
  in the payload. Payload carries instance data only.
- Shuffling (letters, options) is ALWAYS server-side so answer position leaks
  nothing.
- assemble: letters are longer than the answer (~1.5x), shuffled, may contain
  duplicates; treat as an ordered array (positional). Decoy letters from the
  target-language alphabet (include å/ä/ö for Swedish). The target is ALWAYS
  the word's citation/base form (e.g. `gå`, never `gick`) — the prompt is the
  LEXEME-level translation, which names the citation form, so a non-citation
  target would show a mismatched clue. Applies to every pos, not just
  pronouns (pronoun case forms translate to genuinely different English
  words — I/my/me — which is what originally motivated this; other pos have
  the same mismatch, just less jarringly). No form variety for assemble as a
  result — produce_form/multi_select are what vary the tested form across a
  word's encounters (`ExerciseGenerator.buildAssemble`).
- Distractors for choice types: same target-language, same pos, near freq_rank,
  excluding the answer. Mixed-pos distractors are too easy — avoid.
- Which types a word supports is a data precondition (en_ett needs pos=noun +
  gender; assemble/produce_form need a word_form). Generated set = list's
  allowed_exercises ∩ what the word supports. Skip a word that qualifies for
  nothing rather than failing.
- List coherence (every word supports ≥1 of the list's allowed_exercises) is
  validated at data-load time, not enforced by schema.
- `produce_form` is EXCLUDED from a brand-new word's exercise queue (its own
  session, `isNew=true`) whenever another eligible type exists: the intro card
  just shown for that word displays its full paradigm, and produce_form's
  distractors are sibling forms of that same paradigm — so its answer key would
  be sitting a few items above it in the same response. Safety valve: if
  produce_form is the word's ONLY eligible type, it's queued anyway (a word must
  never end up with zero exercises). Fair game from the word's next session
  onward, once the card is no longer part of what's on screen
  (`ExerciseGenerator.buildExerciseQueue`).
- `produce_form`'s prompt names the target inflection, e.g. `"flicka (definite
  singular)"` not bare `"flicka"` — its options are sibling forms of the SAME
  word (flickan/flickor/flickorna), every one a grammatically valid word, so the
  bare citation form alone doesn't say which sibling is wanted and more than one
  option reads as defensibly correct. The label comes from a hardcoded
  form_type→"full name" map in `CitationFormResolver` (Swedish-only for now,
  mirroring the catalog in `doc/app-info/Data/word_form.md`); `formType` in the
  envelope still carries the raw code for logging/analytics.

## API (see doc/api/swagger.yaml)

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

**Current implementation status: real auth (JWT/password verification) is
intentionally not built yet — but the surrounding account/email/reset
infrastructure is (see "Auth-adjacent infrastructure" above).** `AuthController`
always returns a canned `TokenPair`; `login`/`refresh`/`logout` verify nothing
(no password check, no token validation). `register`/`reset/*` DO real work
(persist the account row, generate verification codes/signed links, send
email) — only token issuance and verification are stubbed. There is no
`security/` package content — do not add one while implementing other
features; this is a deliberate, user-directed sequencing decision, not an
oversight. Endpoints that the API
design says should read the user id from the token instead resolve it via
`CurrentUserProvider` (`service/CurrentUserProvider.java`), a one-method seam
with a single implementation, `StubCurrentUserProvider`, that always returns
the seeded demo account id. Every controller/service needing "current user"
should depend on `CurrentUserProvider`, never a hardcoded id inline — when
real auth lands, only `StubCurrentUserProvider` gets replaced (with a
`SecurityContext`/token-subject-backed implementation); no call site changes.

**Endpoint implementation status:** Catalog, Progress, Enrollment, and Session
are all real, DB-backed. Auth is a stub (see above). `SessionController`
delegates to `SessionService`:
- `deriveSession` (`POST /enrollments/{listId}/sessions`): `SessionScheduler.
  selectCandidates` picks review words (not-yet-mastered first, then mastered,
  capped at `app.session.review-words-per-session`) plus never-started words
  (capped at `app.session.new-words-per-session`); `ExerciseGenerator.
  buildExerciseQueue` builds each word's queue (up to `app.session.
  exercises-per-word-per-session`); `IntroCardBuilder` builds the intro card for
  new words; `interleave` round-robins them into the final `items` array (see
  "Session items and interleaving" above). The derived session is held in an
  in-memory `pendingSessions` map keyed by a random `sessionId` — deliberately
  NOT a DB row (`sessions` table only logs completed sessions) and NOT
  transactional (won't survive a restart; that's intentional).
- `completeSession` (`POST /sessions/{sessionId}/complete`): pops the pending
  session (404 if `sessionId` is unknown or already consumed — NOT idempotent,
  a second call for the same session 404s rather than replaying), writes one
  `attempt` row + upserts `list_progress` per result, writes one `sessions` row,
  then runs `EnrollmentService.completeIfMastered`.
- Mastery/session-size knobs all live in `SessionProperties`
  (`app.session.*` in `application.yml`) — `wordsPerSession`,
  `newWordsPerSession`, `exercisesPerWordPerSession`, `reviewWordsPerSession`,
  `masteryThreshold` — tunable placeholders, not fixed product decisions.

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

- `doc/api/swagger.yaml` — full API spec (OpenAPI 3.1), source of truth for
  endpoints and payload schemas.
- `src/main/resources/db/01_schema.sql` — the PostgreSQL DDL.
- `src/main/resources/db/seed_base.sql` — foundational, idempotent reference
  data (languages + base account) the app needs to function, independent of
  any learning content. `seed_first50.sql` — 50 sv<->en concept pairs across
  three curated lists (POS-aligned, both languages full targets). Run
  `seed_base.sql` first, then a content seed.
- `src/main/resources/db/clear_user_activity.sql` / `wipe_except_basic.sql` /
  `wipe_all.sql` — three widening levels of reset: user activity only /
  activity + catalog (keep language + account) / everything. See each file's
  header comment for the exact table list.
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
- Swedish specifics (for INGESTION, which builds lexeme.citation): nouns cited as
  "en/ett + lemma", verbs as "att + lemma". The server never does this - it reads
  the stored lexeme.citation. Never surface a bare noun without its article.
- Citation headwords are STORED in lexeme.citation and READ by the server, which is
  LANGUAGE-AGNOSTIC and composes nothing. Ingestion builds them per language
  (sv: 'ett hus'/'att ga'; en: 'to go'/bare noun) and stores them. Do NOT add
  att/to/en/ett composition logic to the server - read lexeme.citation.
- Every lexeme is FIRST-CLASS in every language: full infl_class + tema + word_form
  paradigm. English is a full learning target (can be learned from uk/ru), NOT
  meaning-only. Translations are POS-ALIGNED (noun<->noun, verb<->verb, adj<->adj) -
  the translate exercise links same part of speech on both sides.
- Don't implement auth/security (see "Current implementation status" above).
  Need the current user? Inject `CurrentUserProvider`, don't hardcode an id
  or add ad-hoc auth logic to get one.
