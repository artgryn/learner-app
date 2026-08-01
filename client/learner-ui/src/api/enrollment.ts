import { del, get, post } from './client';
import type { Enrollment } from './types';

/** Idempotent: enrolling twice returns the existing enrollment. User id comes from the token. */
export function enrollInList(listId: number, baseLang: string): Promise<Enrollment> {
  return post<Enrollment>(`/lists/${listId}/enroll`, { baseLang });
}

export function getEnrollments(): Promise<Enrollment[]> {
  return get<Enrollment[]>('/enrollments');
}

export function getEnrollment(listId: number): Promise<Enrollment> {
  return get<Enrollment>(`/enrollments/${listId}`);
}

export function unenroll(listId: number): Promise<void> {
  return del<void>(`/enrollments/${listId}`);
}
