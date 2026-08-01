package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.LanguageCode;

// PATCH /me body - partial update, null field = leave unchanged. email
// (identity) and status (billing tier) are deliberately not here.
public record AccountUpdate(
        String name,
        LanguageCode uiLang,
        LanguageCode learnBaseLang,
        LanguageCode learnTargetLang
) {
}
