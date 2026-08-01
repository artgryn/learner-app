import { post } from './client';
import type { CompleteResponse, ResultsBatch, SessionResponse, WordProgress } from './types';

/** A session is created, not fetched — there is no GET. */
export function createSession(listId: number): Promise<SessionResponse> {
  return post<SessionResponse>(`/enrollments/${listId}/sessions`);
}

/** Closes the session: writes attempts + progress + one sessions row in one transaction. */
export function completeSession(sessionId: string, batch: ResultsBatch): Promise<CompleteResponse> {
  return post<CompleteResponse>(`/sessions/${sessionId}/complete`, batch);
}

/** Optional incremental result sync during a long session. */
export function submitAnswers(sessionId: string, batch: ResultsBatch): Promise<WordProgress[]> {
  return post<WordProgress[]>(`/sessions/${sessionId}/answers`, batch);
}
