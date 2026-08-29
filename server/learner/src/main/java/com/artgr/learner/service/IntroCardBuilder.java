package com.artgr.learner.service;

import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.Translation;
import com.artgr.learner.data.entity.WordForm;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.IntroduceItem;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Assembles the intro card from lexeme + word_form + translation - no new
// storage, nothing logged on view (doc/app-info/API.md "Introduce card").
@Component
public class IntroCardBuilder {

    // Paradigm display order (doc/app-info/Data/word_form.md catalogs).
    // Unknown form_types (or pos types not in the current catalog) sort
    // after known ones, in whatever order the DB returned them.
    private static final Map<String, List<String>> PARADIGM_ORDER = Map.of(
            "noun", List.of("indef_sg", "def_sg", "indef_pl", "def_pl"),
            "verb", List.of("infinitive", "present", "preteritum", "supine", "imperative", "present_participle", "past_participle"),
            "pronoun", List.of("subject", "object", "possessive_c", "possessive_n", "possessive_pl")
    );

    private final CitationFormResolver citationFormResolver;

    public IntroCardBuilder(CitationFormResolver citationFormResolver) {
        this.citationFormResolver = citationFormResolver;
    }

    public IntroduceItem.Card build(Lexeme lexeme, List<WordForm> forms, List<Translation> translations, LanguageCode baseLang) {
        String word = citationFormResolver.composedWord(lexeme, forms);
        String translation = resolveTranslation(lexeme, translations, baseLang);
        List<IntroduceItem.FormEntry> formEntries = forms.stream()
                .sorted(Comparator.comparingInt(f -> paradigmIndex(lexeme.getPos(), f.getId().getFormType())))
                .map(f -> new IntroduceItem.FormEntry(f.getId().getFormType(), f.getForm()))
                .toList();
        return new IntroduceItem.Card(word, lexeme.getLemma(), lexeme.getPos(), lexeme.getGender(), translation, formEntries);
    }

    // note carries translation nuance (doc/CLAUDE.md - e.g. "to go, to
    // walk"), a richer signal than the partner's citation; fall back to the
    // partner's stored citation (not the bare lemma) when the word has no
    // note, so the base-language side reads consistently with the target-
    // language citation (doc/app-info/Data/word_form.md - "to go", not "go").
    private String resolveTranslation(Lexeme lexeme, List<Translation> translations, LanguageCode baseLang) {
        if (lexeme.getNote() != null && !lexeme.getNote().isBlank()) {
            return lexeme.getNote();
        }
        return findTranslationPartner(lexeme.getId(), translations, baseLang.name())
                .map(partner -> partner.getCitation() != null ? partner.getCitation() : partner.getLemma())
                .orElse(null);
    }

    private int paradigmIndex(String pos, String formType) {
        List<String> order = PARADIGM_ORDER.get(pos);
        int index = order == null ? -1 : order.indexOf(formType);
        return index < 0 ? Integer.MAX_VALUE : index;
    }

    private Optional<Lexeme> findTranslationPartner(Long lexemeId, List<Translation> translations, String desiredLangCode) {
        for (Translation t : translations) {
            Lexeme other = t.getLexemeA().getId().equals(lexemeId) ? t.getLexemeB() : t.getLexemeA();
            if (other.getLanguage().getCode().equals(desiredLangCode)) {
                return Optional.of(other);
            }
        }
        return Optional.empty();
    }
}
