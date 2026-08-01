package com.artgr.learner.data.presentation;

// POST /auth/reset/confirm body. Exactly one of code (from
// /auth/reset/request) or token (from /auth/reset/link) must be supplied -
// see AuthController.resetConfirm.
public record ResetConfirmRequest(String email, String code, String token, String newPassword) {
}
