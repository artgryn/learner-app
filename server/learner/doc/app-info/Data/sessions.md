**Completed-session log.** One row written when a session FINISHES. Not a plan;
holds no array of lexeme ids. Store the past, estimate the future.

| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `id` | `bigint` PK (identity) | number |
| `user_id` | `bigint` NOT NULL | number |
| `list_id` | `bigint` NOT NULL | number |
| `started_at` | `timestamptz` NOT NULL | datetime |
| `ended_at` | `timestamptz` (nullable) | datetime |

**Foreign key:** (`user_id`, `list_id`) → [[user_list]] (enrollment) — a session
cannot exist without the enrollment it belongs to.

**Notes**
- Sessions are **derived on demand** ("what's due + new words"), NOT precomputed.
  This table records only what actually happened.
- "3 of 8 done": *done* = COUNT rows here for (user, list); *total* =
  `ceil(list_size / words_per_session)` (estimate, self-corrects).
- Words that were in a session come from [[attempt]] rows referencing `session_id`.
- Optional for MVP: drop this table and log [[attempt]] with `session_id` NULL if
  session history isn't yet a feature.
