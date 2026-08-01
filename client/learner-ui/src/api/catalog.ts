import { get } from './client';
import type { Language, LanguagePair, ListDetail, ListSummary, ListWord } from './types';

export function getLanguages(): Promise<Language[]> {
  return get<Language[]>('/languages');
}

/** Secured counterpart to getPublicInit's languagePairs, for use inside the logged-in app. */
export function getLanguagePairs(): Promise<LanguagePair[]> {
  return get<LanguagePair[]>('/language-pairs');
}

/** Returns only (base, target) pairs with translation coverage. */
export function getLists(target: string, base: string): Promise<ListSummary[]> {
  const query = new URLSearchParams({ target, base }).toString();
  return get<ListSummary[]>(`/lists?${query}`);
}

export function getListDetail(listId: number): Promise<ListDetail> {
  return get<ListDetail>(`/lists/${listId}`);
}

/** Citation-form word + translation per lexeme in the list, ordered by curated position. */
export function getListWords(listId: number, base: string): Promise<ListWord[]> {
  const query = new URLSearchParams({ base }).toString();
  return get<ListWord[]>(`/lists/${listId}/items?${query}`);
}
