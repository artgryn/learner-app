import { get } from './client';
import type { PublicInitResponse } from './types';

/** Unauthenticated bootstrap payload — safe to call before/without a session (e.g. init_account's pair picker). */
export function getPublicInit(): Promise<PublicInitResponse> {
  return get<PublicInitResponse>('/public/init');
}
