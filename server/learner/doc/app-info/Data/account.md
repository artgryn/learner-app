User account. **WIP.** `base_lang` REMOVED — the taught-from language is
per-enrollment (see [[user_list]].base_lang), not a single account scalar
(a user may know several languages and learn from different ones per list).

| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `id` | `bigint` PK (identity) | number |
| `email` | `text` NOT NULL UNIQUE | string |
| `created_at` | `timestamptz` NOT NULL DEFAULT now() | datetime |

**Notes**
- Auth details TBD (see [[Technical]] — Spring Security, app-only APIs).
- "Languages the user knows" is a set; if ever needed, a `user_language(user_id, lang)`
  M:N table — but MVP does not require storing it. What drives behaviour is the
  enrollment's `base_lang` + the list's `target_lang`.
