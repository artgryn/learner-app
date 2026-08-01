package com.artgr.learner.data.presentation;

// POST /auth/reset/request body.
public record ResetCodeRequest(String email) {
}
