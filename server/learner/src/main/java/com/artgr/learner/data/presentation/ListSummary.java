package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.LanguageCode;

public record ListSummary(Long listId, String name, LanguageCode targetLang, Integer totalWords) {
}
