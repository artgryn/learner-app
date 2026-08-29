| Attribute | SQL Type | General Type |
|-----------|----------|--------------|
| `id` | `bigint` PK (identity) | number |
| `lang` | `char(2)` NOT NULL, FK → language.code | string |
| `lemma` | `text` NOT NULL | string |
| `citation` | `text` (nullable) | string |
| `pos` | `text` NOT NULL | string |
| `gender` | `text` (nullable) | string |
| `infl_class` | `text` (nullable) | string |
| `tema` | `jsonb` NOT NULL DEFAULT `'{}'` | object/JSON |
| `note` | `text` (nullable) | string |
| `freq_rank` | `int` (nullable) | number |

**Unique:** (`lang`, `lemma`, `pos`)

### `citation` — stored display headword (server is language-agnostic)
The ready-to-display headword: `ett hus`, `att gå`, `to go`, `sun`. **Composed by
ingestion per language and STORED here; the server reads it and never composes
articles/markers itself.** This keeps server code language-agnostic — all
language-specific string assembly lives in data/ingestion, like `form_type` being
open text rather than a per-language enum. Retires the old "compose citation from
pos+lang server-side" rule and the proposed `is_citation` flag (ingestion just stores
the correct string, including for defective words like *pengar*).

### `tema` vs [[word_form]] — they look similar, they are not
Both hold inflected forms; the difference is *why each exists* (store-vs-derive):

- **`tema`** = the **irreducible irregular parts** you must store because no rule
  produces them. It is the **input** to form generation.
  - Regular word → `tema` is **empty `'{}'`** (rules alone produce every form).
  - Irregular word → `tema` holds only the unpredictable parts
    (e.g. `gå`: `{present:går, preteritum:gick, supine:gått, ...}`).
- **[[word_form]]** = the **complete, expanded paradigm** — every form, always,
  as addressable rows. It is the **output**, and the thing the app *queries*
  (exercises, base-form lookup, coverage).

Relationship: **`infl_class` + `tema` → generator → [[word_form]] rows.**

Test that proves they are not redundant: *delete [[word_form]] entirely — can you
rebuild it?* Yes, from `infl_class` + `tema`. So `tema` is the source; word_form is
the regenerable expansion. A regular word has **empty tema but full word_form** —
that gap is the point (otherwise regular words would store every form twice).

> If you hand-author every form instead of generating, `tema` becomes optional
> documentation (empty tema = "this word is regular"). Keep it if forms are
> generated in bulk from class.

**Other notes**
- `note` carries translation nuance (e.g. `gå` → `'to go, to walk'`).
- One lexeme = one word in one language; translation attaches at the **lexeme**, not the form.
- **Every lexeme is first-class in every language.** English lexemes carry full `infl_class` + `tema` + [[word_form]] paradigms, same as Swedish, because English is a learning target too (e.g. learned from Ukrainian/Russian). No language is meaning-only. Translations are POS-aligned (noun<->noun, verb<->verb, adj<->adj).
