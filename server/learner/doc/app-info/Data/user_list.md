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
- This (user, list) key is the **parent** of [[sessions]] and [[list_progress]] —
  both FK their (user_id, list_id) here, so neither can exist without enrollment.
- Per-word progress rows in [[list_progress]] are created lazily as words are studied.
- On completion: flip `status`, set `completed_at`; raw [[attempt]] rows may be pruned.
