import { get } from './client';
import type { HomeResponse, Stats, WordProgress } from './types';

export function getListProgress(listId: number): Promise<WordProgress[]> {
  return get<WordProgress[]>(`/enrollments/${listId}/progress`);
}

/** Profile + enrollments + progress + resume pointer, in one round trip. */
export function getHome(): Promise<HomeResponse> {
  return get<HomeResponse>('/me/home');
}

export function getStats(): Promise<Stats> {
  return get<Stats>('/me/stats');
}
