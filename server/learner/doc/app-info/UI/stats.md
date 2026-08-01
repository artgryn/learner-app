**Note:** optional for MVP
**Purpose:** cross-list totals (distinct words known, streak).  
**Components:** headline number (distinct words known), streak.  
**Data:** `GET /me/stats`.  
**States:** `loading · loaded · error`. Can be a section on Home rather than its own view.

##### API
`GET /me/stats` → `wordsKnown`, `streakDays`.