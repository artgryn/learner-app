### Basic app info
Application project name: Znayka, Learner, Woordy, Balakun - in discussion

App consists of the server side and client side for mobile [[Technical]]

### Objectives
Every day communication in most languages are require 1000 words to cover 75% of daily life. Around 3000 words to cover 95% of the daily communication situations and to fill comfortable. One of the key steps during any lang learning is to learn vocabulary in such volume that can cover most of the language constructions. In some languages one of the obstacle in vocab learning is the fact that one word is a few words in reality, examples:
- irregular verbs in English(one word inflects in 3 different words), 
- "en" or "ett" word in Swedish can be inflected in different ways. So you need to learn not a word only but is it "en" or "ett" and how word inflected in different cases
As a result each word may be in reality multiple words.

> Modeling note: "one word = several forms" is stored as **one [[lexeme]] + multiple [[word_form]] rows** (store-vs-derive), never as separate words. The learning unit is the lexeme (Option A); its forms drive exercise variety.

### Primary task
Learn vocabulary for the language. With main focus on remembering words and forms of these words that can be used  in real language [[Supported languages]].

Learning based on the showing cards with word with translation and sub-card with different word forms depending of time, form, role in the sentence etc. And user(student) have to remember it and do tasks that intend to memorise word and all forms for that word.

### Learning process [[MVP]]
1. Select [[Supported languages]]: I want to learn Swedish - I know English 
2. Select predefined list: by topic or ask AI to create list for you or suggest
3. Lists are predefined and stored in the DB 
4. Do exercises [[Exercises]] to memorise words
5. Track progress
6. Small learning sessions

> Process notes:
> - "Assign a list" = one [[user_list]] row (a reference), not a copy of its words.
> - Exercises are **server-generated, fully-built**; client renders + reports only.
> - "Minimum 3 exercises per word" = **3 spaced encounters across different sessions**, ideally different exercise types — not 3 reps in one sitting (spacing effect).
> - Sessions are **derived on demand** ("what's due + new words"); only completed sessions are logged ([[sessions]]).
> - A new word is preceded by an **introduce card** (word + translation + all forms, no action, Next only) — "encounter zero." Intro cards and exercises are one ordered, interleaved flow (`items`); intros are just-in-time and never graded/logged. Intro appears when a word has no [[list_progress]] row yet.

### Tasks to remember words [[Exercises]]
1. Assemble word from the list of letters [[MVP]]
2. Guess base form of the word by "word-form" [[MVP]]
3. Select correct translation(both directions) [[MVP]]
4. Select correct "word-combo" - en/ett? [[MVP]]

### Client side 
- login
- create or select lang and list
- show progress
- current exercises
- minimal local storage 

### Server side
- API
- AUTH
- log progress
- [[Data structure]]
- Logic to resolve current progress
- Split list into small sessions (derived, not precomputed)
- Track that each word had at least some minimum amount of exercises
- Generate fully-built exercises (respecting list `allowed_exercises` ∩ word capability)

### Roadmap
1. Create basic DB schema
2. Create API design
3. Create basic logic how to
	1. assign list to the user
	2. split [[list]] into sessions
	3. create [[Exercises]] for the session
	4. track progress and calculate repetitions
4. Implement API
5. Ingest initial data for testing
	- **Validate list coherence at load time**: every word supports ≥1 of its list's `allowed_exercises`
