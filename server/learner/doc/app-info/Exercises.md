1. En/Ett word for Swedish [[MVP]]
2. Assemble word from the list of letters [[MVP]]
3. Guess base form of the word by "word-form" [[MVP]]
4. Guess "word-form" of the base word
5. Select correct option in the sentence — NOTE: needs an **example-sentences table** (not yet in [[Data structure]]); the blank points at a specific [[word_form]], distractors = sibling forms. NOT MVP — defer until the table exists.
6. Select correct translation(both directions) [[MVP]]
7. Select correct "word-combo" - en/ett? [[MVP]]
8. Multi-select: select all correct words forms for the base word [[MVP]]

### Generation notes
- Exercises are **server-generated, fully-built** (question + options + answer). Client only renders and reports the result. No exercise logic on the client.
- Which types a word can do is a **data precondition** (e.g. en/ett requires `pos=noun` + `gender` not null; assemble/produce_form require a [[word_form]]).
- A [[list]] may restrict types via `allowed_exercises`. Generated set = list's allowed types ∩ what each word supports.
- Exercise names align with the `exercise_type` enum: `en_ett, assemble, translate, base_form, produce_form, multi_select` (+ sentence-cloze later).
