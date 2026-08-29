package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.EnrollmentStatus;
import com.artgr.learner.data.enums.LanguageCode;

import java.time.OffsetDateTime;

public record Enrollment(
        Long listId,
        String name,
        LanguageCode baseLang,
        LanguageCode targetLang,
        EnrollmentStatus status,
        Integer wordsMastered,
        Integer totalWords,
        // Exercise-level (not word-level) progress toward "list learned" -
        // use these for a progress bar; wordsMastered/totalWords is coarser
        // (only counts a word once FULLY mastered). exercisesNeeded already
        // bakes in the server's masteryThreshold config - never hardcode
        // that number client-side, it may differ in production.
        Integer exercisesCompleted,
        Integer exercisesNeeded,
        SessionsSummary sessions,
        OffsetDateTime lastActiveAt
) {
    public record SessionsSummary(Integer done, Integer estimatedTotal) {
    }
}
