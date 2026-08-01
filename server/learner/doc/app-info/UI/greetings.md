**Purpose:** close the session, sync results, show a summary.
**Components:** summary (items done, correct/total, words touched), continue to start new [[exercise]]/[[home]] buttons.  
**Data:** `POST /sessions/{sessionId}/complete` with the buffered `results[]` → writes attempts + progress + the sessions row; returns updated progress. This call may ALSO auto-complete the list server-side (every word now mastered flips the enrollment to `completed`) - the response here does NOT flag that; the client only finds out via the next `GET /enrollments/{listId}` or `GET /me/home`. Worth deciding whether this summary screen should show a "list complete!" state - if so it needs its own follow-up read after `/complete`, since `CompleteResponse` doesn't carry a `listCompleted` flag today.  
**States:**

- `submitting` (sending results),
- `done` (summary),
- `sync-failed` (keep results in device storage, offer retry — this is the offline-resilience path; the results aren't lost).  
    On done → Home (which now reflects the new progress, including a flipped-to-completed list if this was the last word mastered).

##### API
`POST /sessions/{sessionId}/complete` with buffered `results[]` → writes attempts + progress + sessions row, returns updated progress.