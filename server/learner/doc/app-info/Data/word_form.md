| Attribute   | SQL Type                          | General Type |
| ----------- | --------------------------------- | ------------ |
| `lexeme_id` | `bigint` NOT NULL, FK → lexeme.id | number       |
| `form_type` | `text` NOT NULL                   | string       |
| `form`      | `text` NOT NULL                   | string       |

**Primary key:** (`lexeme_id`, `form_type`)

### Why `form_type` is open TEXT, not an enum
Form inventories differ per language (Swedish ~15 values, Ukrainian ~20 including
cases and vocative, with little overlap). A DB enum would force a migration per new
language, so the column stays open `text`. The catalog below is documentation only
(knowledge base), not a DB constraint. Contrast `exercise_type`, which is a native
enum: small, closed, app-controlled.

### Citation form
The form shown when presenting a bare lexeme. Chosen by convention from `pos`, with
no stored flag:

| pos       | citation form_type |
| --------- | ------------------ |
| noun      | `indef_sg`         |
| verb      | `infinitive`       |
| pronoun   | `subject`          |
| number    | `base`             |
| adjective | `utrum`            |

The translate exercise renders this form as the prompt but tests a lexeme-level fact
(meaning), so the [[attempt]] logs `lexeme_id` with `form_type = NULL`. Escape hatch
for defective words (plural-only like *pengar*): add an optional `is_citation`
boolean later.

### form_type catalog — Swedish (target, full)

| form_type            | full name                | pos     | citation |
| -------------------- | ------------------------ | ------- | -------- |
| `indef_sg`           | Indefinite singular      | noun    | yes      |
| `def_sg`             | Definite singular        | noun    | no       |
| `indef_pl`           | Indefinite plural        | noun    | no       |
| `def_pl`             | Definite plural          | noun    | no       |
| `infinitive`         | Infinitive               | verb    | yes      |
| `present`            | Present (presens)        | verb    | no       |
| `preteritum`         | Past (preteritum)        | verb    | no       |
| `supine`             | Supine (supinum)         | verb    | no       |
| `imperative`         | Imperative               | verb    | no       |
| `present_participle` | Present participle       | verb    | no       |
| `past_participle`    | Past participle          | verb    | no       |
| `subject`            | Subject form             | pronoun | yes      |
| `object`             | Object form              | pronoun | no       |
| `possessive_c`       | Possessive, en-gender    | pronoun | no       |
| `possessive_n`       | Possessive, ett-gender   | pronoun | no       |
| `possessive_pl`      | Possessive, plural       | pronoun | no       |
| `base`               | Base form                | number  | yes      |

### form_type catalog — Swedish adjectives (future)

| form_type     | full name                    | pos | citation |
| ------------- | ---------------------------- | --- | -------- |
| `utrum`       | En-gender (common) agreement | adj | yes      |
| `neutrum`     | Ett-gender (neuter) agreement| adj | no       |
| `plural`      | Plural agreement             | adj | no       |
| `definite`    | Definite agreement           | adj | no       |
| `comparative` | Comparative degree           | adj | no       |
| `superlative` | Superlative degree           | adj | no       |

### form_type catalog — English (base / translation target, minimal, display only)

| form_type            | full name                   | pos     | citation |
| -------------------- | --------------------------- | ------- | -------- |
| `sg`                 | Singular                    | noun    | yes      |
| `pl`                 | Plural                      | noun    | no       |
| `infinitive`         | Infinitive                  | verb    | yes      |
| `present_3sg`        | 3rd-person singular present | verb    | no       |
| `preteritum`         | Past                        | verb    | no       |
| `supine`             | Past participle             | verb    | no       |
| `present_participle` | -ing form                   | verb    | no       |
| `subject`            | Subject form                | pronoun | yes      |
| `object`             | Object form                 | pronoun | no       |

### form_type catalog — Ukrainian nouns (future target)

Seven cases by two numbers. Vocative is unique versus Swedish and English.

| form_type | full name              | pos  | citation |
| --------- | ---------------------- | ---- | -------- |
| `nom_sg`  | Nominative singular    | noun | yes      |
| `gen_sg`  | Genitive singular      | noun | no       |
| `dat_sg`  | Dative singular        | noun | no       |
| `acc_sg`  | Accusative singular    | noun | no       |
| `ins_sg`  | Instrumental singular  | noun | no       |
| `loc_sg`  | Locative singular      | noun | no       |
| `voc_sg`  | Vocative singular      | noun | no       |
| `nom_pl`  | Nominative plural      | noun | no       |
| `gen_pl`  | Genitive plural        | noun | no       |
| `dat_pl`  | Dative plural          | noun | no       |
| `acc_pl`  | Accusative plural      | noun | no       |
| `ins_pl`  | Instrumental plural    | noun | no       |
| `loc_pl`  | Locative plural        | noun | no       |
| `voc_pl`  | Vocative plural        | noun | no       |

### form_type catalog — Ukrainian verbs (future target)

Aspect partner is a separate lexeme, not a form. Russian mirrors this set minus the
vocative on nouns.

| form_type       | full name                      | pos  | citation |
| --------------- | ------------------------------ | ---- | -------- |
| `infinitive`    | Infinitive                     | verb | yes      |
| `pres_1sg`      | Present 1st singular           | verb | no       |
| `pres_2sg`      | Present 2nd singular           | verb | no       |
| `pres_3sg`      | Present 3rd singular           | verb | no       |
| `pres_1pl`      | Present 1st plural             | verb | no       |
| `pres_2pl`      | Present 2nd plural             | verb | no       |
| `pres_3pl`      | Present 3rd plural             | verb | no       |
| `past_m`        | Past masculine                 | verb | no       |
| `past_f`        | Past feminine                  | verb | no       |
| `past_n`        | Past neuter                    | verb | no       |
| `past_pl`       | Past plural                    | verb | no       |
| `imperative_sg` | Imperative singular            | verb | no       |
| `imperative_pl` | Imperative plural              | verb | no       |
