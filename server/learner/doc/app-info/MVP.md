1. Swedish vocab from English
2. No AI
3. Pre-defined lists on the back-end
4. List split to the 6-10min sessions (sessions **derived on demand**, not precomputed — see [[sessions]] / [[learning_plan]])
5. Track progress for each word — keyed **(user, list, word)** in [[list_progress]]; progress not shared across lists. Overall "words known" = `COUNT(DISTINCT lexeme)`.
6. Track mistakes — via append-only [[attempt]] log (1 INSERT + 1 progress UPDATE per exercise)
7. Ensure that each word will get some amount of exercises — floor = **≥3 spaced encounters across different sessions**, ideally different exercise types (NOT 3 reps in one session)
8. Basic exercises to practice [[Exercises]]
9. Email send functionality: to create account with confirmation code and password reset.

### UI and user journey

```
Splash/Boot → [Auth: Login/Register] → Home → Catalog(browse lists) → List detail → (enroll) → Home → Session → [Intro card | Exercise]* → Session complete → Home
```

#### 1. Boot / Splash

**Purpose:** decide where to send the user — straight to Home if a valid token exists, else Auth.  
**Components:** logo/loading indicator only, styled background.  
**Data:** no call of its own; check stored token, then `GET /me/home` (if token) to warm the cache.  
**States:** `checking` → routes to Home or Login. `token-invalid` → Login. No error UI (silent fallthrough to Login).  
_Note: since auth is stubbed, this always resolves to the demo user, but build the branch so real auth drops in later._
##### API
`GET /me/home` (warm cache if token present). No auth call of its own.

---
#### 2. Login

**Purpose:** authenticate.  
**Components:** email field, password field, submit button, "go to Register" link, inline error text.  
**Data:** `POST /auth/login` → `TokenPair`; store tokens.  
**States:** `idle · submitting · error(code)` — error text branches on the error `code` (e.g. `INVALID_CREDENTIALS`), never on `message`. On success → Home.
##### API
`POST /auth/login` → `TokenPair`.

---
#### 3. Register

**Purpose:** create account.  
**Components:** email, password, submit, "go to Login" link, inline error.  
**Data:** `POST /auth/register` → `TokenPair`.  
**States:** `idle · submitting · error(code)` (e.g. `EMAIL_TAKEN`). On success → Home.

##### API
`POST /auth/register` → `TokenPair`.

---
#### 4. Home (the hub)

**Purpose:** the launch screen — show enrollments, progress, and where to resume; entry point to everything.  
**Components:**

- header (greeting/streak),
- **Continue card** (the resume pointer — biggest tap target),
- **enrollment list** (one row per enrolled list: name, target/base flag, words mastered / total, sessions done/total, last active),
- "Browse lists" button → Catalog,
- (optional) stats strip.  
    **Data:** `GET /me/home` — single aggregate call returning `user`, `enrollments[]`, `resume{listId}`.  
    **States:**
- `loading` (skeleton),
- `empty` (no enrollments → prominent "Browse lists" CTA, hide Continue),
- `loaded` (render enrollments + Continue),
- `error` (retry).
- On launch, also **reconcile**: if device has un-synced results, fire `POST /sessions/{id}/complete` before/after rendering (per the onboarding diagram).

##### API
`GET /me/home` → `user`, `enrollments[]`, `resume{listId}`.  
Reconcile step: `POST /sessions/{sessionId}/complete` if the device buffer holds un-synced results.  
Logout affordance (settings/menu): `POST /auth/logout`. ← _this endpoint had no home before; it belongs on Home's menu._

---
#### 5. Catalog / Browse lists

**Purpose:** discover curated lists for the user's language pair.  
**Components:** language-pair indicator (target/base), list of list-cards (name, size, maybe topic), tap → List detail.  
**Data:** `GET /lists?target=sv&base=en` → `ListSummary[]`. (Server returns only pairs with translation coverage.)  
**States:** `loading · loaded · empty (no lists for pair) · error`.  
_MVP: base/target are effectively fixed (en→sv), so the pair selector can be display-only for now — but keep it as a component slot for multi-language later._

###### API
`GET /lists?target=sv&base=en` → `ListSummary[]`.  
`GET /languages` → to populate the language-pair selector (target/base options). ← _the missing binding for `/languages`; it's the source for the pair picker, display-only in MVP but wired here._

---
#### 6. List detail

**Purpose:** decide whether to enroll.  
**Components:** list name, size, `allowedExercises` (what kinds of practice), enroll button (or "Continue" if already enrolled), sample words (optional).  
**Data:** `GET /lists/{listId}` → `ListDetail`; enroll via `POST /lists/{listId}/enroll` (body: `baseLang`).  
**States:**

- `not-enrolled` (show Enroll),
- `enrolling` (spinner),
- `enrolled` (button becomes "Start learning" → Session; idempotent enroll means re-tapping is safe),
- `error`.  
    On enroll success → back to Home or straight into a Session.

##### API
`GET /lists/{listId}` → `ListDetail`.  
`POST /lists/{listId}/enroll` (body `baseLang`) → 200 existing / 201 created.

---
#### 7. Session container (the core loop orchestrator)

**Purpose:** fetch the session and drive the ordered `items[]` — this is the controller view, not a screen the user "sees" as itself.  
**Components:** progress bar (item i of N), a slot that renders the current item by `itemType`, exit/pause affordance.  
**Data:** `POST /enrollments/{listId}/sessions` → `{ sessionId, items[] }`. Holds items + a local results buffer in device storage.  
**States:**

- `loading` (deriving session),
- `active` (rendering current item),
- `empty` (nothing due + no new words → "You're caught up" → Home),
- `error`.  
    **Logic:** walks `items` in order; on each, switches to view 8 or 9; buffers results locally; **no network per item**.

##### API
`POST /enrollments/{listId}/sessions` → `{ sessionId, items[] }`.

---
#### 8. Intro card (itemType: introduce)

**Purpose:** first exposure — teach the word before testing it.  
**Components:**

- composed `word` (e.g. "ett hus" — article included),
- `translation`,
- **forms table** (the paradigm — `forms[]`, client localizes the `formType` labels),
- **Next** button only (no answer, no grading).  
    **Data:** rendered entirely from the item's `card` (already in the session response — no call).  
    **States:** `shown` → Next advances. No loading/error (data's in hand). Writes nothing.

##### API
No API — rendered from the item's `card` already in the session response.

---
#### 9. Exercise (itemType: exercise) — polymorphic by exerciseType

**Purpose:** one graded task.  
**Components (shared shell):** prompt area (`prompt.text`), instruction label (client-localized from `exerciseType`), answer area (varies), feedback flash (correct/wrong, shown after local grading), auto-advance or Next.  
**Answer-area variants** (one component per type):

- `en_ett` — two buttons (en / ett),
- `translate` — option buttons (`options`, in `optionsLang`),
- `assemble` — letter tiles (`letters`, positional) + answer slots (`answerLength`),
- `base_form` / `produce_form` — option buttons,
- `multi_select` — multi-toggle options + confirm.  
    **Data:** rendered from the item's `exercise` payload; **graded locally** against `correctAnswer`/`correctAnswers`; result appended to the local buffer.  
    **States:** `presented · answered(correct|wrong, flash) · advancing`. No network.

##### API
No API — rendered from the item's `exercise` payload; graded locally.  
(Optional, deferred) `POST /sessions/{sessionId}/answers` for mid-session sync — **not MVP** (stubbed 501).

---
#### 10. Session complete

**Purpose:** close the session, sync results, show a summary.  
**Components:** summary (items done, correct/total, words touched), continue/home buttons.  
**Data:** `POST /sessions/{sessionId}/complete` with the buffered `results[]` → writes attempts + progress + the sessions row; returns updated progress.  
**States:**

- `submitting` (sending results),
- `done` (summary),
- `sync-failed` (keep results in device storage, offer retry — this is the offline-resilience path; the results aren't lost).  
    On done → Home (which now reflects the new progress).

##### API
`POST /sessions/{sessionId}/complete` with buffered `results[]` → writes attempts + progress + sessions row, returns updated progress.

---
#### 11. Enrollment detail / Progress (per list)

**Purpose:** see progress within one list, and resume.  
**Components:** header (name, %, sessions done/total), per-word progress list (word, times practiced, correct/wrong, due), "Continue" → Session, un-enroll.  
**Data:** `GET /enrollments/{listId}` (summary) + `GET /enrollments/{listId}/progress` (per-word); un-enroll `DELETE /enrollments/{listId}`.  
**States:** `loading · loaded · error`. Words with no progress row render as "not started."

##### API
`GET /enrollments/{listId}` → summary.  
`GET /enrollments/{listId}/progress` → per-word `WordProgress[]`.  
`DELETE /enrollments/{listId}` → un-enroll.

---
#### 12. Stats (overall) — optional for MVP

**Purpose:** cross-list totals (distinct words known, streak).  
**Components:** headline number (distinct words known), streak.  
**Data:** `GET /me/stats`.  
**States:** `loading · loaded · error`. Can be a section on Home rather than its own view.

##### API
`GET /me/stats` → `wordsKnown`, `streakDays`.

---
##### Shared components
- error popup - used to show generic errors, mainly in a case of server error or if server not available
- logo and icons
- background
- buttons
- bottom menu island with home, lists, account views