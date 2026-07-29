package com.artgr.learner.data.presentation;

import java.util.List;

// Shown once, the first time a word appears in a session (no list_progress
// row yet) - "encounter zero" (doc/app-info/App plan.md). No action beyond
// "Next"; never submitted back in ResultsBatch; nothing to grade.
public record IntroduceItem(String itemId, String itemType, Long lexemeId, Card card) implements SessionItem {

    public IntroduceItem(String itemId, Long lexemeId, Card card) {
        this(itemId, "introduce", lexemeId, card);
    }

    // `word` is the composed citation form WITH article/marker
    // (e.g. "ett hus", "att gå") - see SessionService citation-form rules.
    public record Card(String word, String lemma, String pos, String gender, String translation, List<FormEntry> forms) {
    }

    public record FormEntry(String formType, String form) {
    }
}
