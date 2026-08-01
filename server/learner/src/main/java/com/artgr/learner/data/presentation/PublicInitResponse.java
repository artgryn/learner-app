package com.artgr.learner.data.presentation;

import java.util.List;

// GET /public/init response - generic, unauthenticated bootstrap payload for
// pre-login screens. An object, not a bare array, so more public data can be
// added later (alongside languagePairs) without breaking existing clients.
public record PublicInitResponse(List<LanguagePair> languagePairs) {
}
