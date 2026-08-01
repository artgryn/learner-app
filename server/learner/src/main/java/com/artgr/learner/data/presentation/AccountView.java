package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.AccountStatus;
import com.artgr.learner.data.enums.LanguageCode;

// GET /me response. Never carries password/security fields.
public record AccountView(
        Long id,
        String email,
        String name,
        LanguageCode uiLang,
        LanguageCode learnBaseLang,
        LanguageCode learnTargetLang,
        AccountStatus status
) {
}
