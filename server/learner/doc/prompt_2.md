# Task: account, language-pairs, password reset, and email (server side)

Implement the server-side work that the UI (init_account, account_settings,
login-register-reset) now requires. Follow `CLAUDE.md` ("Pending server work"),
`doc/api/swagger.yaml`, `API.md`, and the per-table `.md` specs as the source of
truth. Do NOT relitigate decisions recorded there; if this prompt conflicts with the
schema or docs, STOP and ask.

Stack: Spring Boot 4+, Java 25, PostgreSQL. Auth is still STUBBED — do not build real
JWT/security here (see below); get the current user via `CurrentUserProvider`.

## Scope

IN scope:
1. Account CRUD (`GET /me`, `PATCH /me`, `DELETE /me`).
2. Language pairs endpoint (`GET /language-pairs`) reading from `application.yml`.
3. Password reset + registration confirmation flow (endpoints + cache-backed codes).
4. Email subsystem: an async `EmailService` with HTML templates.
5. Single-active-list enforcement in the enroll flow.

OUT of scope:
- Real auth/JWT/security. Keep using `CurrentUserProvider` /
  `StubCurrentUserProvider` (returns the seeded demo account). When real auth lands,
  only that stub is replaced. Do NOT add token/security logic here.
- Billing/payment (the `status` field exists but has no logic yet).
- Any change to the language-data tables (lexeme/word_form/translation/list/
  list_item) — frozen.

## DB state (already applied — do NOT re-migrate)

`account` already has these columns in `01_schema.sql` (this migration is done;
just use them):
`id, email, name, ui_lang (char(2) FK language, default 'en'),
learn_base_lang (char(2) FK language, nullable),
learn_target_lang (char(2) FK language, nullable),
status (text NOT NULL default 'free'), created_at`.

No other schema change is needed for this work. Reset codes live in CACHE, not the DB.
Reset links are stateless signed tokens, not the DB. Language pairs live in config,
not the DB. If you think you need a new table, STOP and ask first.

## 1. Account CRUD

Endpoints (user resolved from `CurrentUserProvider`, never from path/body):
- `GET /me` -> account view: id, email, name, uiLang, learnBaseLang, learnTargetLang,
  status. (Do NOT return password/security fields.)
- `PATCH /me` -> partial update of: name, uiLang, learnBaseLang, learnTargetLang.
  - Validate uiLang / learnBaseLang / learnTargetLang against `language.code`
    (FK will enforce, but validate for a clean 400 with a stable error `code`).
  - Validate that (learnBaseLang, learnTargetLang) is a SUPPORTED pair (see #2) —
    reject unsupported pairs with a stable error code (e.g. `UNSUPPORTED_PAIR`).
  - `email` is NOT editable here (identity); `status` is NOT editable by the user.
- `DELETE /me` -> delete the account row. FK `ON DELETE CASCADE` removes the user's
  user_list / list_progress / sessions / attempt. Return 204.

`init_account` (post-registration) calls `PATCH /me` to set `name` + the learning
pair. There is no separate init endpoint.

Controller thin -> `AccountService` -> `AccountRepository`. Reuse existing DTO/mapper
patterns (see `EnrollmentMapper` for the established style).

## 2. Language pairs from config

- `GET /language-pairs` -> list of supported pairs, each as
  `{ base: {code,name}, target: {code,name} }` (resolve names from the `language`
  table or a config-provided name; prefer the DB `language` rows for names).
- Source the pairs from `application.yml`, e.g.:
  ```
  app:
    language-pairs:
      - base: en
        target: sv
  ```
  Bind with `@ConfigurationProperties(prefix = "app")`.
- This endpoint feeds init_account and the catalog language filter, and is the
  validation source for `PATCH /me`'s pair check.
- **Document in code that config is a TEMPORARY stand-in for coverage-derived pairs**
  (the real long-term constraint is translation coverage in the data). Do NOT add a
  pairs table. Do NOT try to derive coverage now.

## 3. Password reset + registration confirmation

Codes are 4-digit numeric, stored in a CACHE keyed by (purpose, email/userId) with a
TTL. **1h max TTL for reset codes.** Use Spring's cache abstraction / Redis if
configured, else an in-memory `Cache` bean — but structure it behind a
`VerificationCodeStore` interface so the backing store is swappable. NO DB table for
codes.

Endpoints:
- `POST /auth/register` (already exists as a stub) -> on real impl, create the account
  and trigger a registration confirmation **code** email (async). Account may be
  created in an unconfirmed state; do NOT block MVP on enforcing confirmation unless
  the existing flow already does — if unsure, ask.
- `POST /auth/reset/request` -> body `{ email }`. If the account exists, generate a
  4-digit code, store in cache (TTL <= 1h), send the reset-code email (async).
  ALWAYS return 200/204 regardless of whether the email exists (do not leak account
  existence). 
- `POST /auth/reset/confirm` -> body `{ email, code, newPassword }`. Verify the code
  from cache (exists + not expired + matches), set the new password, invalidate the
  code. Wrong/expired code -> stable error code (e.g. `INVALID_OR_EXPIRED_CODE`).
- `POST /auth/reset/link` -> code-less path. Generate a STATELESS SIGNED token
  (short-lived, e.g. JWS with expiry), email a reset link containing it, AND send a
  separate FRAUD-SIGNAL notification email ("if this wasn't you, click here /
  contact support"). Verifying the link is a token-signature + expiry check — no
  storage.

Password hashing: use the project's existing password encoder if one exists; if none
exists yet (because auth is stubbed), add a `PasswordEncoder` bean (BCrypt) used ONLY
for storing the reset result — do NOT build login/token verification here. If this
crosses into auth territory that's meant to stay stubbed, STOP and ask.

## 4. Email subsystem (service, NOT a controller)

Email is a side-effect triggered by auth/account flows — there is NO public `/email`
endpoint.
- New `EmailService` with methods like `sendRegistrationCode`, `sendResetCode`,
  `sendResetLink`, `sendFraudNotification`.
- **Send ASYNCHRONOUSLY** (`@Async` + an executor, or a mail-sending task) so the
  request thread never blocks on SMTP. Failures are logged, retried if trivial, and
  NOT surfaced to the user mid-request (the reset-request response returns before the
  email is confirmed sent).
- Assemble bodies from **HTML templates in `src/main/resources/templates/email/`**
  (e.g. Thymeleaf). One template per message: registration code, reset code, reset
  link, fraud notification. Templates take model variables (code, link, name).
- Sender address + SMTP host/port/credentials in `application.yml` under a mail
  config block; use `spring.mail.*` + `JavaMailSender`. Do NOT hardcode credentials.
- Provide a no-op / logging `EmailService` profile for local dev (so tests and local
  runs don't need a real SMTP server).

## 5. Single-active-list enforcement

In the enroll flow (`POST /lists/{listId}/enroll`):
- A user has at most ONE active `user_list`. If enrolling a DIFFERENT list while one
  is active, the client has already warned the user; the server DELETES the current
  active enrollment (cascading to list_progress/sessions via DB FKs) and creates the
  new one.
- Enroll stays idempotent for the SAME list (return existing, 200 vs 201 for created).
- `GET /me/home` / `GET /enrollments` continue to return an array holding 0-or-1
  active enrollment (shape unchanged).
- Do this in `EnrollmentService`; keep the DB cascade doing the child cleanup (do not
  hand-delete children). Optionally rely on / add the commented partial unique index
  `user_list(user_id) WHERE status='active'` as a safety net (ask before enabling it
  if it risks breaking existing seed/tests).

## Config additions (`application.yml`)

- `app.language-pairs` (list of {base,target}).
- `spring.mail.*` (host, port, username, password, from-address).
- Verification code settings: `app.reset.code-ttl` (<= 1h), code length (4).
- A dev profile with a logging/no-op mailer.

## Error handling

Use the existing error envelope `{ error: { status, code, message, traceId } }`.
Clients branch on `code`. New stable codes to introduce (at least):
`UNSUPPORTED_PAIR`, `INVALID_OR_EXPIRED_CODE`, `EMAIL_TAKEN` (register),
`ACCOUNT_NOT_FOUND` (internal; reset-request must NOT leak this to the client).

## Swagger

Update `doc/api/swagger.yaml` to add: `GET /me`, `PATCH /me`, `DELETE /me`,
`GET /language-pairs`, `POST /auth/reset/request`, `POST /auth/reset/confirm`,
`POST /auth/reset/link`, plus the `AccountView`, `AccountUpdate`, `LanguagePair`,
and reset request/confirm schemas. Keep it OpenAPI 3.1 and consistent with the
existing style (bearer security, error responses). If a change would alter an
existing contract, flag it rather than silently changing it.

## Tests

- `PATCH /me` rejects an unsupported pair (`UNSUPPORTED_PAIR`) and an invalid lang
  code; accepts a supported one; cannot change email/status.
- `DELETE /me` cascades (no orphaned user_list/progress/sessions/attempt).
- `GET /language-pairs` returns exactly what `application.yml` declares.
- Reset: request stores a code with TTL; confirm succeeds with the right code, fails
  on wrong/expired (`INVALID_OR_EXPIRED_CODE`); request never reveals whether the
  email exists.
- Email: `EmailService` is invoked asynchronously and does not block the request;
  the dev/no-op mailer is used in tests (no real SMTP).
- Enroll: switching to a new active list removes the old enrollment + its children;
  re-enrolling the same list is idempotent.

## Rules of engagement

- Implement against the shapes above; match `swagger.yaml` DTOs exactly.
- Do NOT build real auth/JWT/security; do NOT add DB tables for codes, links, or
  pairs; do NOT touch the language-data tables.
- Email is a service, not an endpoint; sending is async.
- If any decision is ambiguous or seems to conflict with the schema/docs/CLAUDE.md,
  STOP and ask rather than guessing.
