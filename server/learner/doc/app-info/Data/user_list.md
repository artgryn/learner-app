Enrollment / assignment of a curated [[list]] to a user. One row per (user, list).
"Assigning" a list = insert ONE row here — NOT copying the list's words.
Carries `base_lang`: the language the user is taught FROM for this enrollment.

| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `user_id` | `bigint` NOT NULL, FK → account.id | number |
| `list_id` | `bigint` NOT NULL, FK → list.id | number |
| `base_lang` | `char(2)` NOT NULL, FK → language.code | string |
| `status` | `text` NOT NULL ('active' / 'completed') | string |
| `started_at` | `timestamptz` NOT NULL DEFAULT now() | datetime |
| `completed_at` | `timestamptz` (nullable) | datetime |

**Primary key:** (`user_id`, `list_id`)

**Notes**
- Language pair for this enrollment = `base_lang` (from) + [[list]].`target_lang` (to);
  mirrors the [[Supported languages]] matrix, which is an enrollment-level fact.
- `base_lang` is **pre-filled from the account's default pair** ([[account]].`learn_base_lang`)
  at enroll time, but this per-enrollment value is the source of truth for how the
  list is learned; the account pair is only a default/filter.
- This (user, list) key is the **parent** of [[sessions]] and [[list_progress]] —
  both FK their (user_id, list_id) here, so neither can exist without enrollment.
- Per-word progress rows in [[list_progress]] are created lazily as words are studied.
- **Completion is automatic, server-computed, not a client action.** Checked at the
  end of `POST /sessions/{sessionId}/complete` (`EnrollmentService.completeIfMastered`):
  when every word's `list_progress.times_practiced` has reached the mastery threshold
  (i.e. `wordsMastered == totalWords`), the row flips `status` -> `completed` and sets
  `completed_at`. **`list_progress` is deliberately left untouched** — only raw
  [[attempt]] rows may ever be pruned on completion, never the progress summary.
- **Restarting a completed list reuses the SAME row** — `POST /lists/{listId}/enroll`
  on an already-`completed` list reactivates it (`status` -> `active`,
  `completed_at` -> `null`) instead of creating a second row for that (user, list).
  "Restart" means resume/review with existing mastery intact (the point of spaced
  repetition — completed words should keep resurfacing for review), NOT a
  wipe-and-redo. A true from-scratch reset (explicitly deleting `list_progress`) is a
  distinct, more destructive action, not implemented, and not what plain enroll does.
- **One active list per user (MVP):** enforced in application logic AND at the DB
  level via the partial unique index `user_list(user_id) WHERE status='active'`
  (`01_schema.sql`, now enabled — not just commented-out optional hardening).
  Enrolling a DIFFERENT list — whether newly, or as a restart of a completed one —
  DELETES whatever enrollment is currently `active` (cascades to [[list_progress]] /
  [[sessions]]), so it loses THAT list's progress — the UI warns before doing it.
  Completed enrollments are never touched by this rule; only one row can ever be
  `active` at a time, but any number can be `completed`.
- **Implementation gotcha (Hibernate flush ordering):** `EnrollmentService`'s
  delete-then-insert/update pattern requires an explicit `userListRepository.flush()`
  between the delete and the following save. Hibernate's flush action queue always
  orders entity INSERTs/UPDATEs before DELETEs *regardless of code call order*, so
  without the explicit flush, the new/reactivated row's write would hit Postgres
  before the old active row's delete, transiently violating the partial unique index
  even though the end state would have been correct. Discovered by actually enabling
  the index and reproducing the failure — see `EnrollmentService.deleteOtherActiveEnrollment`.
