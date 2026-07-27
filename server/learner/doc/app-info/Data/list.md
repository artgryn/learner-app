| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `id` | `bigint` PK (identity) | number |
| `name` | `text` NOT NULL | string |
| `target_lang` | `char(2)` NOT NULL, FK → language.code | string |
| `user_id` | `bigint` (nullable) | number |
| `allowed_exercises` | `exercise_type[]` (nullable) | array/enum |

**Notes**
- `user_id` NULL = curated/global list; set = user- or AI-generated list (post-MVP feature, column reserved now).
- `allowed_exercises` NULL = all exercise types permitted; a set = only those types generated for this list. Enables themed lists (en/ett-only, verbs-only). See [[Exercises]].
- Requires a Postgres enum type:
  `CREATE TYPE exercise_type AS ENUM ('en_ett','assemble','translate','base_form','produce_form','multi_select');`
- **List-level gate only** (word-level capability gate intentionally dropped). Consequence: list coherence must be validated at **load/curation time** — every word in a list must support ≥1 of the list's allowed exercises. At generation time, skip a word that qualifies for nothing rather than failing.
- Reconsider a word-level capability gate when AI list-generation lands (no human curates coherence then).
