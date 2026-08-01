import Constants from 'expo-constants';
import { Platform } from 'react-native';

import type { ErrorResponse } from './types';

/**
 * On a physical device (Expo Go), "localhost" resolves to the device itself, not the dev
 * machine running the API server. `expoConfig.hostUri` is Metro's own LAN address (e.g.
 * "192.168.1.23:8081") — same host, different port — so reuse its hostname for the API port.
 * Falls back to "localhost" on web and in any build where hostUri isn't set (only present
 * in development).
 */
function resolveApiHost(): string {
  const hostUri = Constants.expoConfig?.hostUri;
  const lanHost = hostUri?.split(':')[0];
  if (lanHost) return lanHost;

  return Platform.OS === 'android' ? '10.0.2.2' : 'localhost';
}

/** Local dev server per doc/api/swagger.yaml. */
export const API_BASE_URL = `http://${resolveApiHost()}:8080/v1`;

export class ApiError extends Error {
  status: number;
  code: string;
  traceId?: string;

  constructor(status: number, code: string, message: string, traceId?: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.code = code;
    this.traceId = traceId;
  }
}

// Auth is intentionally not wired up here — the server has no real login/register yet
// (stub `AuthController`), so requests are sent without a bearer token until that lands.
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
  });

  if (!response.ok) {
    const body: ErrorResponse | null = await response.json().catch(() => null);
    const error = body?.error;
    throw new ApiError(
      response.status,
      error?.code ?? 'UNKNOWN_ERROR',
      error?.message ?? response.statusText,
      error?.traceId
    );
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export function get<T>(path: string): Promise<T> {
  return request<T>(path);
}

export function post<T>(path: string, body?: unknown): Promise<T> {
  return request<T>(path, {
    method: 'POST',
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
}

export function patch<T>(path: string, body?: unknown): Promise<T> {
  return request<T>(path, {
    method: 'PATCH',
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
}

export function del<T>(path: string): Promise<T> {
  return request<T>(path, { method: 'DELETE' });
}
