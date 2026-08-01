package com.artgr.learner.service;

// Cache-backed store for short-lived verification codes (password reset,
// registration confirmation) - NOT a DB table (doc/prompt_2.md). Behind an
// interface so the backing store (in-memory now, Redis later) is swappable
// without touching callers.
public interface VerificationCodeStore {

    // Generates and stores a new code for (purpose, key), replacing any
    // existing one for that pair.
    String generate(String purpose, String key);

    // True (and invalidates the code) if one exists for (purpose, key), is
    // unexpired, and matches. False otherwise - including "no such code" and
    // "expired", which callers must not distinguish (see
    // INVALID_OR_EXPIRED_CODE in doc/api/swagger.yaml).
    boolean verifyAndConsume(String purpose, String key, String code);
}
