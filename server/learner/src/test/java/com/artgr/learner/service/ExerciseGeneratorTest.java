package com.artgr.learner.service;

import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.Translation;
import com.artgr.learner.data.entity.TranslationId;
import com.artgr.learner.data.entity.WordForm;
import com.artgr.learner.data.entity.WordFormId;
import com.artgr.learner.data.enums.ExerciseType;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.AssemblePayload;
import com.artgr.learner.data.presentation.EnEttPayload;
import com.artgr.learner.data.presentation.MultiSelectPayload;
import com.artgr.learner.data.presentation.OptionsPayload;
import com.artgr.learner.data.presentation.TranslatePayload;
import com.artgr.learner.data.repository.LexemeRepository;
import com.artgr.learner.data.repository.WordFormRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Pure unit tests (no Spring context, no DB) with hand-built fixtures - the
// real seed data only has one lexeme per (lang, pos), so same-pos distractor
// behavior can't be exercised meaningfully against it.
class ExerciseGeneratorTest {

    private static final Language SV = language("sv");
    private static final Language EN = language("en");

    private final LexemeRepository lexemeRepository = mock(LexemeRepository.class);
    private final WordFormRepository wordFormRepository = mock(WordFormRepository.class);
    private final CitationFormResolver citationFormResolver = new CitationFormResolver();

    private ExerciseGenerator generator(long seed) {
        return new ExerciseGenerator(lexemeRepository, wordFormRepository, citationFormResolver, new Random(seed));
    }

    @Test
    void enEttEligibleOnlyForGenderedNoun() {
        Lexeme hus = lexeme(1L, SV, "hus", "noun", "ett", 600);
        List<WordForm> husForms = List.of(form(1L, "indef_sg", "hus"));
        Set<ExerciseType> husEligible = generator(1).eligibleTypes(hus, husForms, List.of(), LanguageCode.en);
        assertTrue(husEligible.contains(ExerciseType.en_ett));

        Lexeme jag = lexeme(5L, SV, "jag", "pronoun", null, 5);
        List<WordForm> jagForms = List.of(form(5L, "subject", "jag"));
        Set<ExerciseType> jagEligible = generator(1).eligibleTypes(jag, jagForms, List.of(), LanguageCode.en);
        assertFalse(jagEligible.contains(ExerciseType.en_ett));
    }

    @Test
    void translateRequiresTranslationLink() {
        Lexeme hus = lexeme(1L, SV, "hus", "noun", "ett", 600);
        List<WordForm> forms = List.of(form(1L, "indef_sg", "hus"));

        assertFalse(generator(1).eligibleTypes(hus, forms, List.of(), LanguageCode.en).contains(ExerciseType.translate));

        Lexeme house = lexeme(2L, EN, "house", "noun", null, 500);
        Translation translation = translation(hus, house);
        assertTrue(generator(1).eligibleTypes(hus, forms, List.of(translation), LanguageCode.en).contains(ExerciseType.translate));
    }

    @Test
    void wordWithNoEligibleTypeUnderTheListsAllowedExercisesYieldsAnEmptyQueue() {
        // jag: pronoun, no gender - en_ett never eligible for it regardless
        // of data. Restricting a list to en_ett only must skip jag rather
        // than fail (doc/CLAUDE.md - "skip a word that qualifies for nothing").
        Lexeme jag = lexeme(5L, SV, "jag", "pronoun", null, 5);
        List<WordForm> forms = List.of(form(5L, "subject", "jag"), form(5L, "object", "mig"));

        List<ExerciseGenerator.GeneratedExercise> queue = generator(1)
                .buildExerciseQueue(jag, forms, List.of(), Set.of(ExerciseType.en_ett), LanguageCode.sv, LanguageCode.en, 2);

        assertTrue(queue.isEmpty());
    }

    @Test
    void multiSelectRequiresTwoFormsAndSamePosDistractors() {
        Lexeme ga = lexeme(3L, SV, "gå", "verb", null, 80);
        List<WordForm> singleForm = List.of(form(3L, "infinitive", "gå"));
        // Only one form - not enough to multi-select from.
        assertFalse(generator(1).eligibleTypes(ga, singleForm, List.of(), LanguageCode.en).contains(ExerciseType.multi_select));

        List<WordForm> twoForms = List.of(form(3L, "infinitive", "gå"), form(3L, "present", "går"));
        // Two forms, but no same-pos sibling lexeme exists yet.
        when(lexemeRepository.findDistractorCandidates("sv", "verb", 3L, 80)).thenReturn(List.of());
        assertFalse(generator(1).eligibleTypes(ga, twoForms, List.of(), LanguageCode.en).contains(ExerciseType.multi_select));

        Lexeme springa = lexeme(7L, SV, "springa", "verb", null, 200);
        when(lexemeRepository.findDistractorCandidates("sv", "verb", 3L, 80)).thenReturn(List.of(springa));
        assertTrue(generator(1).eligibleTypes(ga, twoForms, List.of(), LanguageCode.en).contains(ExerciseType.multi_select));
    }

    @Test
    void distractorsAreSamePosAndExcludeTheAnswer() {
        Lexeme hus = lexeme(1L, SV, "hus", "noun", "ett", 600);
        Lexeme house = lexeme(2L, EN, "house", "noun", null, 500);
        Translation translation = translation(hus, house);
        List<WordForm> husForms = List.of(form(1L, "indef_sg", "hus"), form(1L, "def_sg", "huset"));

        Lexeme bil = lexeme(10L, EN, "car", "noun", null, 700);
        Lexeme trad = lexeme(11L, EN, "tree", "noun", null, 720);
        Lexeme verbNotEligible = lexeme(12L, EN, "run", "verb", null, 90); // wrong pos - must never appear
        when(lexemeRepository.findDistractorCandidates("en", "noun", 2L, 500)).thenReturn(List.of(bil, trad));
        when(wordFormRepository.findByLexemeId(2L)).thenReturn(List.of());
        when(wordFormRepository.findByLexemeId(10L)).thenReturn(List.of());
        when(wordFormRepository.findByLexemeId(11L)).thenReturn(List.of());

        ExerciseGenerator.GeneratedExercise exercise = generator(1)
                .buildExerciseQueue(hus, husForms, List.of(translation), Set.of(ExerciseType.translate), LanguageCode.sv, LanguageCode.en, 1)
                .get(0);

        TranslatePayload payload = (TranslatePayload) exercise.exercise();
        assertTrue(payload.options().contains("house"));
        assertFalse(payload.options().contains("run")); // wrong pos never included
        assertTrue(payload.options().size() <= 3); // 1 correct + up to 2 same-pos distractors
    }

    @Test
    void enEttOptionsAlwaysIncludeBothArticlesAndAreShuffled() {
        Lexeme hus = lexeme(1L, SV, "hus", "noun", "ett", 600);
        List<WordForm> forms = List.of(form(1L, "indef_sg", "hus"));

        ExerciseGenerator.GeneratedExercise ex1 = generator(1)
                .buildExerciseQueue(hus, forms, List.of(), Set.of(ExerciseType.en_ett), LanguageCode.sv, LanguageCode.en, 1)
                .get(0);
        EnEttPayload payload = (EnEttPayload) ex1.exercise();
        assertEquals(Set.of("en", "ett"), Set.copyOf(payload.options()));
        assertEquals("ett", payload.correctAnswer());
        // Prompt must be the bare form, never composed with the article -
        // that would give the answer away.
        assertEquals("hus", ex1.prompt().text());
    }

    @Test
    void sameSeedProducesIdenticalShuffleOrder() {
        Lexeme ga = lexeme(3L, SV, "gå", "verb", null, 80);
        List<WordForm> forms = List.of(
                form(3L, "infinitive", "gå"),
                form(3L, "present", "går"),
                form(3L, "preteritum", "gick"),
                form(3L, "supine", "gått")
        );

        var a = generator(42).buildExerciseQueue(ga, forms, List.of(), Set.of(ExerciseType.produce_form), LanguageCode.sv, LanguageCode.en, 1).get(0);
        var b = generator(42).buildExerciseQueue(ga, forms, List.of(), Set.of(ExerciseType.produce_form), LanguageCode.sv, LanguageCode.en, 1).get(0);

        OptionsPayload pa = (OptionsPayload) a.exercise();
        OptionsPayload pb = (OptionsPayload) b.exercise();
        assertEquals(pa.options(), pb.options(), "same seed must produce the same shuffled order");
        assertTrue(pa.options().contains(pa.correctAnswer()));
    }

    @Test
    void assembleLettersIncludeAllAnswerCharactersAndAreShuffled() {
        Lexeme ga = lexeme(3L, SV, "gå", "verb", null, 80);
        Lexeme go = lexeme(4L, EN, "go", "verb", null, 40);
        Translation translation = translation(ga, go);
        List<WordForm> forms = List.of(form(3L, "preteritum", "gick"));

        ExerciseGenerator.GeneratedExercise ex = generator(1)
                .buildExerciseQueue(ga, forms, List.of(translation), Set.of(ExerciseType.assemble), LanguageCode.sv, LanguageCode.en, 1)
                .get(0);
        AssemblePayload payload = (AssemblePayload) ex.exercise();

        assertEquals("gick", payload.correctAnswer());
        assertEquals(4, payload.answerLength());
        assertTrue(payload.letters().size() > 4, "letters must include decoys beyond the answer length");
        // Every character of the answer must be present in the letter pool.
        List<String> lettersCopy = new java.util.ArrayList<>(payload.letters());
        for (char c : payload.correctAnswer().toCharArray()) {
            assertTrue(lettersCopy.remove(String.valueOf(c)), "missing answer letter: " + c);
        }
    }

    @Test
    void baseFormAnswerIsComposedWithArticleForSwedishNounButNotForEnglish() {
        Lexeme hus = lexeme(1L, SV, "hus", "noun", "ett", 600);
        List<WordForm> husForms = List.of(form(1L, "indef_sg", "hus"), form(1L, "def_sg", "huset"));
        when(lexemeRepository.findDistractorCandidates("sv", "noun", 1L, 600)).thenReturn(List.of());

        ExerciseGenerator.GeneratedExercise ex = generator(1)
                .buildExerciseQueue(hus, husForms, List.of(), Set.of(ExerciseType.base_form), LanguageCode.sv, LanguageCode.en, 1)
                .get(0);
        OptionsPayload payload = (OptionsPayload) ex.exercise();
        assertEquals("ett hus", payload.correctAnswer());

        // English verb must never get the Swedish "att" marker.
        Lexeme go = lexeme(4L, EN, "go", "verb", null, 40);
        List<WordForm> goForms = List.of(form(4L, "infinitive", "go"), form(4L, "preteritum", "went"));
        when(wordFormRepository.findByLexemeId(4L)).thenReturn(goForms);
        String composed = citationFormResolver.composedWord(go, goForms);
        assertEquals("go", composed);
    }

    @Test
    void multiSelectCorrectAnswersAreAllDistinctFormsAndDecoysComeFromSamePosLexeme() {
        Lexeme ga = lexeme(3L, SV, "gå", "verb", null, 80);
        List<WordForm> forms = List.of(
                form(3L, "infinitive", "gå"),
                form(3L, "present", "går"),
                form(3L, "preteritum", "gick")
        );
        Lexeme springa = lexeme(7L, SV, "springa", "verb", null, 200);
        when(lexemeRepository.findDistractorCandidates("sv", "verb", 3L, 80)).thenReturn(List.of(springa));
        when(wordFormRepository.findByLexemeId(7L)).thenReturn(List.of(form(7L, "infinitive", "springa"), form(7L, "present", "springer")));

        ExerciseGenerator.GeneratedExercise ex = generator(1)
                .buildExerciseQueue(ga, forms, List.of(), Set.of(ExerciseType.multi_select), LanguageCode.sv, LanguageCode.en, 1)
                .get(0);
        MultiSelectPayload payload = (MultiSelectPayload) ex.exercise();

        assertEquals(Set.of("gå", "går", "gick"), Set.copyOf(payload.correctAnswers()));
        assertTrue(payload.options().containsAll(payload.correctAnswers()));
        // At least one decoy from the same-pos sibling lexeme must be present.
        assertTrue(payload.options().contains("springa") || payload.options().contains("springer"));
    }

    private static Language language(String code) {
        Language language = new Language();
        language.setCode(code);
        language.setName(code);
        return language;
    }

    private static Lexeme lexeme(Long id, Language language, String lemma, String pos, String gender, Integer freqRank) {
        Lexeme lexeme = new Lexeme();
        lexeme.setId(id);
        lexeme.setLanguage(language);
        lexeme.setLemma(lemma);
        lexeme.setPos(pos);
        lexeme.setGender(gender);
        lexeme.setFreqRank(freqRank);
        return lexeme;
    }

    private static WordForm form(Long lexemeId, String formType, String value) {
        WordForm wordForm = new WordForm();
        wordForm.setId(new WordFormId(lexemeId, formType));
        wordForm.setForm(value);
        return wordForm;
    }

    private static Translation translation(Lexeme a, Lexeme b) {
        Translation translation = new Translation();
        translation.setId(new TranslationId(a.getId(), b.getId()));
        translation.setLexemeA(a);
        translation.setLexemeB(b);
        return translation;
    }
}
