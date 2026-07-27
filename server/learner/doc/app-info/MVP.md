1. Swedish vocab from English
2. No AI
3. Pre-defined lists on the back-end
4. List split to the 6-10min sessions (sessions **derived on demand**, not precomputed — see [[sessions]] / [[learning_plan]])
5. Track progress for each word — keyed **(user, list, word)** in [[list_progress]]; progress not shared across lists. Overall "words known" = `COUNT(DISTINCT lexeme)`.
6. Track mistakes — via append-only [[attempt]] log (1 INSERT + 1 progress UPDATE per exercise)
7. Ensure that each word will get some amount of exercises — floor = **≥3 spaced encounters across different sessions**, ideally different exercise types (NOT 3 reps in one session)
8. Basic exercises to practice [[Exercises]]
