Append-only log. One row per answered exercise. Never updated.

| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `id` | `bigint` PK (identity) | number |
| `user_id` | `bigint` NOT NULL, FK → account.id | number |
| `list_id` | `bigint` NOT NULL, FK → list.id | number |
| `lexeme_id` | `bigint` NOT NULL, FK → lexeme.id | number |
| `session_id` | `bigint` (nullable), FK → sessions.id | number |
| `exercise_type` | `exercise_type` NOT NULL | enum |
| `form_type` | `text` (nullable) | string |
| `is_correct` | `boolean` NOT NULL | boolean |
| `elapsed_ms` | `int` (nullable) | number |
| `created_at` | `timestamptz` NOT NULL DEFAULT now() | datetime |

**Index:** (`user_id`, `created_at`)

**Notes**
- Per exercise the server does: **1 INSERT here + 1 UPDATE on [[list_progress]]**. The session row is NOT updated per exercise.
- `form_type` records which form was tested (e.g. `preteritum`). Under **Option A** progress is counted on the lexeme, but logging form-level here keeps per-form scheduling (Option B) possible later without data loss.
- Source of truth for stats (mistake patterns, per-form difficulty, future FSRS re-fit). [[list_progress]] is a maintained summary derived from these.
- **Pruning:** on list completion, raw attempts for that list may be deleted (keep the completion record in [[user_list]]). Cheap growth (~11 MB/user/year); partition by month / prune later if needed.
