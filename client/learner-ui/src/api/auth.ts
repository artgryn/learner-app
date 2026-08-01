import { post } from './client';
import type {
  LoginRequest,
  RefreshRequest,
  RegisterRequest,
  ResetCodeRequest,
  ResetConfirmRequest,
  ResetLinkRequest,
  TokenPair,
} from './types';

/** Persists a real account (hashed password) and emails a confirmation code, async — access isn't gated on it. */
export function register(request: RegisterRequest): Promise<TokenPair> {
  return post<TokenPair>('/auth/register', request);
}

/** Stub server-side: always succeeds, does not check the password yet. */
export function login(request: LoginRequest): Promise<TokenPair> {
  return post<TokenPair>('/auth/login', request);
}

/** Stub server-side: always succeeds, does not validate the refresh token yet. */
export function refresh(request: RefreshRequest): Promise<TokenPair> {
  return post<TokenPair>('/auth/refresh', request);
}

export function logout(): Promise<void> {
  return post<void>('/auth/logout');
}

/** Always resolves regardless of whether the email matches an account — never leak existence. */
export function requestPasswordReset(request: ResetCodeRequest): Promise<void> {
  return post<void>('/auth/reset/request', request);
}

export function confirmPasswordReset(request: ResetConfirmRequest): Promise<void> {
  return post<void>('/auth/reset/confirm', request);
}

/** Code-less alternative to requestPasswordReset — emails a signed link + a separate fraud-notice email. */
export function requestPasswordResetLink(request: ResetLinkRequest): Promise<void> {
  return post<void>('/auth/reset/link', request);
}
