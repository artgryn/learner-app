**Purpose:** fetch the session and drive the ordered `items[]` — this is the controller view, not a screen the user "sees" as itself.  this view will have multiple sub-views depending on the `itemType`. At the end navigates to the [[greetings]]

**Components:** progress bar (item i of N)[[shared-components]], a slot that renders the current item by `itemType`, exit(interrupt the session, session should be restarted), no bottom navigation menu.  

**Sub-views:**
- intro card - (itemType: introduce). first exposure — teach the word before testing it. Word, translation and all word forms. used to remmeber the word.
- Exercise cards - [[exercise]]

**Data:** `POST /enrollments/{listId}/sessions` → `{ sessionId, items[] }`. Holds items + a local results buffer in device storage.  

**Navigation:** [[home]], [[greetings]]

**States:**

- `loading` (deriving session),
- `active` (rendering current item),
- `empty` (nothing due + no new words → "You're caught up" → Home),
- `error`.  
    **Logic:** walks `items` in order; on each, switches to view 8 or 9; buffers results locally; **no network per item**.

##### API
`POST /enrollments/{listId}/sessions` → `{ sessionId, items[] }`.



## intro card
Intro card (itemType: introduce)

**Purpose:** first exposure — teach the word before testing it.  
**Components:**

- composed `word` (e.g. "ett hus" — article included),
- `translation`,
- **forms table** (the paradigm — `forms[]`, client localizes the `formType` labels),
- **Next** button only (no answer, no grading).  
    **Data:** rendered entirely from the item's `card` (already in the session response — no call).  
    **States:** `shown` → Next advances. No loading/error (data's in hand). Writes nothing.

##### API
No API — rendered from the item's `card` already in the session response.


## Exercise (itemType: exercise) — polymorphic by exerciseType

**Purpose:** one graded task.  
**Components (shared shell):** prompt area (`prompt.text`), instruction label (client-localized from `exerciseType`), answer area (varies), feedback flash (correct/wrong, shown after local grading), auto-advance or Next.  
**Answer-area variants** (one component per type):

- `en_ett` — two buttons (en / ett),
- `translate` — option buttons (`options`, in `optionsLang`),
- `assemble` — letter tiles (`letters`, positional) + answer slots (`answerLength`),
- `base_form` / `produce_form` — option buttons,
- `multi_select` — multi-toggle options + confirm.  
    **Data:** rendered from the item's `exercise` payload; **graded locally** against `correctAnswer`/`correctAnswers`; result appended to the local buffer.  
    **States:** `presented · answered(correct|wrong, flash) · advancing`. No network.

##### API
No API — rendered from the item's `exercise` payload; graded locally.  
(Optional, deferred) `POST /sessions/{sessionId}/answers` for mid-session sync — **not MVP** (stubbed 501).