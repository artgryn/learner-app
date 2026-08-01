User account. Holds identity plus the user's **default learning pair** (used as the
[[list catalog]] filter and to pre-fill [[user_list]].`base_lang` at enroll time).
The per-enrollment `base_lang` on [[user_list]] stays the source of truth for how a
given list is actually being learned; the account pair is a default/filter, not the
learning truth.

| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `id` | `bigint` PK (identity) | number |
| `email` | `text` NOT NULL UNIQUE | string |
| `name` | `text` (nullable) | string |
| `ui_lang` | `char(2)` FK → language.code, DEFAULT 'en' | string |
| `learn_base_lang` | `char(2)` (nullable) FK → language.code | string |
| `learn_target_lang` | `char(2)` (nullable) FK → language.code | string |
| `status` | `text` NOT NULL DEFAULT 'free' ('free' / 'paid') | string |
| `created_at` | `timestamptz` NOT NULL DEFAULT now() | datetime |

**Notes**
- `name` + the learning pair are set on [[init account]] (after registration) and
  editable in [[account settings]].
- `learn_base_lang` + `learn_target_lang` = the pair the user is learning. Two
  separate columns (queried independently as the catalog filter `WHERE base=? AND
  target=?`, each FK to `language.code`). At [[list details]] enroll, they pre-fill
  [[user_list]].`base_lang`.
- `ui_lang` = app interface language ('en' only for now); stored for later.
- `status` = 'free' | 'paid' (billing tier; no billing logic yet).
- "Languages the user knows" as a set is still NOT modelled; the single default pair
  above is what MVP needs.
- Auth details TBD (see [[Technical]] — Spring Security, app-only APIs).

**One-active-list rule (MVP):** a user has at most one active [[user_list]]. Switching
lists is application-level: delete the current enrollment (cascades to
[[list_progress]] / [[sessions]]), then create the new one. Optional DB hardening: a
partial unique index on `user_list(user_id) WHERE status='active'` (commented in
`01_schema.sql`).
