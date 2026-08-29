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

    private static final Map<String, String> CITATION_FORM_TYPE = Map.of(
            "noun", "indef_sg",
            "verb", "infinitive",
            "pronoun", "subject",
            "number", "base",
            "adjective", "utrum"
    );

    // "full name" column of doc/app-info/Data/word_form.md's Swedish catalog - the same
    // labels ingestion/docs already use for these codes. produce_form needs this: its prompt
    // is a bare citation form (e.g. "flicka") with several sibling forms as options (flickan,
    // flickor, flickorna) - without naming which inflection is being asked for, every sibling
    // option reads as an equally defensible answer, not just the intended one.
    private static final Map<String, String> FORM_TYPE_LABELS = Map.ofEntries(
            Map.entry("indef_sg", "indefinite singular"),
            Map.entry("def_sg", "definite singular"),
            Map.entry("indef_pl", "indefinite plural"),
            Map.entry("def_pl", "definite plural"),
            Map.entry("infinitive", "infinitive"),
            Map.entry("present", "present"),
            Map.entry("preteritum", "past"),
            Map.entry("supine", "supine"),
            Map.entry("imperative", "imperative"),
            Map.entry("present_participle", "present participle"),
            Map.entry("past_participle", "past participle"),
            Map.entry("subject", "subject form"),
            Map.entry("object", "object form"),
            Map.entry("possessive_c", "possessive (en-gender)"),
            Map.entry("possessive_n", "possessive (ett-gender)"),
            Map.entry("possessive_pl", "possessive plural"),
            Map.entry("base", "base form")
    );

    // Falls back to the raw code (underscores as spaces) for a form_type not yet in the
    // catalog above, rather than throwing - form_type is open text (doc/CLAUDE.md), so new
    // codes can appear before this catalog is updated for them.
    public String formTypeLabel(String formType) {
        return FORM_TYPE_LABELS.getOrDefault(formType, formType.replace('_', ' '));
    }

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

    // The ready-to-display headword ("ett hus", "att gå", "to go"). STORED by
    // ingestion, per language (doc/app-info/Data/lexeme.md) - the server is
    // language-agnostic and composes nothing here. Falls back to the bare
    // citation form only for legacy/defective data that predates the stored
    // column.
    public String composedWord(Lexeme lexeme, List<WordForm> forms) {
        String citation = lexeme.getCitation();
        return citation != null ? citation : citationFormString(lexeme, forms);
    }

    public boolean isCitationForm(WordForm form, Lexeme lexeme) {
        String citationType = CITATION_FORM_TYPE.get(lexeme.getPos());
        return citationType != null && citationType.equals(form.getId().getFormType());
    }

    public List<WordForm> nonCitationForms(Lexeme lexeme, List<WordForm> forms) {
        return forms.stream().filter(f -> !isCitationForm(f, lexeme)).toList();
    }
}
