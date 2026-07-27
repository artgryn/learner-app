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
        SessionsSummary sessions,
        OffsetDateTime lastActiveAt
) {
    public record SessionsSummary(Integer done, Integer estimatedTotal) {
    }
}
