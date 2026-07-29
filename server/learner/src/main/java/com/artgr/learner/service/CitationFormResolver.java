package com.artgr.learner.service;

import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.WordForm;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Shared by ExerciseGenerator (base_form answers, en_ett/translate/
// produce_form/multi_select prompts) and IntroCardBuilder (card.word) - both
// need the identical citation-form-by-pos convention and Swedish
// article/att composition (doc/CLAUDE.md "Swedish specifics").
@Component
public class CitationFormResolver {

    private static final String SWEDISH_TARGET_LANG = "sv";

    private static final Map<String, String> CITATION_FORM_TYPE = Map.of(
            "noun", "indef_sg",
            "verb", "infinitive",
            "pronoun", "subject",
            "number", "base",
            "adjective", "utrum"
    );

    // Bare citation form (no article/marker) - used wherever showing the
    // article would give away an answer (e.g. en_ett's own prompt).
    public String citationFormString(Lexeme lexeme, List<WordForm> forms) {
        String citationType = CITATION_FORM_TYPE.get(lexeme.getPos());
        if (citationType != null) {
            Optional<String> form = forms.stream()
                    .filter(f -> f.getId().getFormType().equals(citationType))
                    .map(WordForm::getForm)
                    .findFirst();
            if (form.isPresent()) {
                return form.get();
            }
        }
        return lexeme.getLemma();
    }

    // Composed citation WITH article/marker ("cite nouns as en/ett + lemma,
    // verbs as att + lemma; never surface a bare noun without its article").
    // Swedish-only by design - guarded by language, not just pos, so a
    // base-language (English) verb never gets an "att" prefix.
    public String composedWord(Lexeme lexeme, List<WordForm> forms) {
        String base = citationFormString(lexeme, forms);
        if (!SWEDISH_TARGET_LANG.equals(lexeme.getLanguage().getCode())) {
            return base;
        }
        if ("noun".equals(lexeme.getPos()) && lexeme.getGender() != null) {
            return lexeme.getGender() + " " + base;
        }
        if ("verb".equals(lexeme.getPos())) {
            return "att " + base;
        }
        return base;
    }

    public boolean isCitationForm(WordForm form, Lexeme lexeme) {
        String citationType = CITATION_FORM_TYPE.get(lexeme.getPos());
        return citationType != null && citationType.equals(form.getId().getFormType());
    }

    public List<WordForm> nonCitationForms(Lexeme lexeme, List<WordForm> forms) {
        return forms.stream().filter(f -> !isCitationForm(f, lexeme)).toList();
    }
}
