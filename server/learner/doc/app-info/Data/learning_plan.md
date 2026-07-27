**No table for MVP — placeholder.**

The "learning plan" (how a [[list]] splits into [[sessions]]) is **computed on demand**,
not stored:
- Next session = query over [[list_progress]] (`due` words) + new words from the
  enrolled [[list]] not yet started, sliced to a 6–10 min budget.
- Total sessions = `ceil(list_size / words_per_session)` (estimate).
- Spacing/scheduling logic (FSRS or simpler) decides *when* a word resurfaces.

Revisit only if scheduling becomes expensive enough to cache. Do not precompute upcoming sessions.
