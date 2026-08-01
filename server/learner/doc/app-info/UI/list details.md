**Purpose:** decide whether to enrol list and see all words in this list. If list in progress show what words learned. If list not in progress show indicators: enroll, already complete, in progress. If user wants to enroll list and there is other list in progress show warning.


**Components:** list name, size, enroll button, all words as a list, indicators, go back option.  
**Data:** `GET /lists/{listId}` → `ListDetail` (catalog info only - no per-user enrollment state). Cross-reference the returned `listId` against `GET /enrollments` (or reuse `GET /me/home`'s `enrollments[]`) to know THIS list's status for the current user - `not-enrolled` / `active` / `completed`; `ListDetail` itself carries none of that. Word list: `GET /lists/{listId}/items?base=<baseLang>` → `ListWord[]` (citation-form `word` + `translation` per lexeme, catalog data only - no per-word mastery; cross-reference `GET /enrollments/{listId}/progress`, keyed by `lexemeId`, to mark which are learned). Enroll (or restart) via `POST /lists/{listId}/enroll` (body: `baseLang`).  
**States:**

- `not-enrolled` (show Enroll),
- `enrolling` (spinner),
- `enrolled-active` (button becomes "Start learning" → Session; idempotent enroll means re-tapping is safe),
- `enrolled-completed` (button becomes "Restart" - re-enrolling a completed list REACTIVATES it and resumes with existing progress/mastery; it does NOT reset anything),
- `error`.  
    On enroll success → back to Home or straight into a Session.

**The warning isn't just UX polish - the server actually enforces one-active-list.** If a DIFFERENT list is currently active, `POST /lists/{listId}/enroll` DELETES that other enrollment (and its progress) as part of the same call, with no server-side confirmation step. The client's warning dialog is the only guard, so it must be shown before this call whenever the target list isn't already the active one. Completed lists are exempt - they're never auto-deleted by someone else enrolling.

##### API
`GET /lists/{listId}` → `ListDetail`.  
`GET /lists/{listId}/items?base=<baseLang>` → `ListWord[]` (word content for the list).  
`GET /enrollments/{listId}/progress` → per-lexeme mastery, to mark learned words when enrolled.  
`GET /enrollments` (or `GET /me/home`) → this list's current status, for the indicators above.  
`POST /lists/{listId}/enroll` (body `baseLang`) → 200 (already active, idempotent) / 201 (newly created, OR reactivated from a completed list).