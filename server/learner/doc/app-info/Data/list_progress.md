Per-word learning progress. **Keyed (user, list, lexeme)** — progress is NOT shared
across lists (a word in two lists is learned twice). Holds the word-level practice
counter (Option A). One row **per (user, list, word)**, created **lazily** on first
practice, **UPSERTed once per attempt**.

| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `user_id` | `bigint` NOT NULL | number |
| `list_id` | `bigint` NOT NULL | number |
| `lexeme_id` | `bigint` NOT NULL, FK → lexeme.id | number |
| `times_practiced` | `int` NOT NULL DEFAULT 0 | number |
| `correct` | `int` NOT NULL DEFAULT 0 | number |
| `wrong` | `int` NOT NULL DEFAULT 0 | number |
| `due` | `timestamptz` (nullable) | datetime |

**Primary key:** (`user_id`, `list_id`, `lexeme_id`)
**Foreign key:** (`user_id`, `list_id`) → [[user_list]] (enrollment) — cannot exist without it.

**Notes**
- Write pattern per exercise: **1 INSERT into [[attempt]] + 1 UPSERT here**.
- `times_practiced` counts **word-level** encounters (Option A): incremented whichever
  form was drilled. *Per-form* counts are **derivable from [[attempt]].form_type**,
  not stored here. (Switching to per-form scheduling = Option B = add `form_type`
  to this PK; the attempt log already carries the data to migrate.)
- No row = word not started (meaningful; never pre-created at enrollment).
- Per-list % = this table ∩ [[list_item]]. Overall goal = `COUNT(DISTINCT lexeme_id)`.
