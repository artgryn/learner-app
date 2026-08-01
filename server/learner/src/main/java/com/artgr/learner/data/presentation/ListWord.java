package com.artgr.learner.data.presentation;

// Catalog data only - no per-user mastery state (cross-reference
// GET /enrollments/{listId}/progress for that, keyed by lexemeId).
public record ListWord(Long lexemeId, String word, String pos, String gender, String translation) {
}
