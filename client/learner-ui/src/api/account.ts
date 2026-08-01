import { del, get, patch } from './client';
import type { AccountUpdate, AccountView } from './types';

export function getMe(): Promise<AccountView> {
  return get<AccountView>('/me');
}

/** Partial update: name / uiLang / learnBaseLang / learnTargetLang. email/status aren't settable here. */
export function updateMe(update: AccountUpdate): Promise<AccountView> {
  return patch<AccountView>('/me', update);
}

/** Cascades to the user's user_list / list_progress / sessions / attempt rows. */
export function deleteMe(): Promise<void> {
  return del<void>('/me');
}
