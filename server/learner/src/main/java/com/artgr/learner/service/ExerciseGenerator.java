package com.artgr.learner.service;

import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.Translation;
import com.artgr.learner.data.entity.WordForm;
import com.artgr.learner.data.enums.ExerciseType;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.AssemblePayload;
import com.artgr.learner.data.presentation.EnEttPayload;
import com.artgr.learner.data.presentation.ExercisePayload;
import com.artgr.learner.data.presentation.MultiSelectPayload;
import com.artgr.learner.data.presentation.OptionsPayload;
import com.artgr.learner.data.presentation.Prompt;
import com.artgr.learner.data.presentation.TranslatePayload;
import com.artgr.learner.data.repository.LexemeRepository;
import com.artgr.learner.data.repository.WordFormRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

// Owns exercise-type capability rules, exercise building, and distractor
// selection. Kept separate from SessionScheduler (word selection) and
// IntroCardBuilder so each is unit-testable on its own.
@Component
public class ExerciseGenerator {

    // Swedish-only for now (the only MVP target language) - decoy letters
    // for assemble. Ties into the LanguageCode business-logic seam; extend
    // per-language when a second target language is added.
    private static final String SWEDISH_ALPHABET = "abcdefghijklmnopqrstuvwxyzåäö";

    private final LexemeRepository lexemeRepository;
    private final WordFormRepository wordFormRepository;
    private final CitationFormResolver citationFormResolver;
    private final Random random;

    public ExerciseGenerator(
            LexemeRepository lexemeRepository,
            WordFormRepository wordFormRepository,
            CitationFormResolver citationFormResolver,
            Random random
    ) {
        this.lexemeRepository = lexemeRepository;
        this.wordFormRepository = wordFormRepository;
        this.citationFormResolver = citationFormResolver;
        this.random = random;
    }

    // Which types a word supports is a data precondition (doc/CLAUDE.md).
    // assemble additionally requires a translation, since its prompt cue IS
    // the base-language translation (no fallback). multi_select additionally
    // requires same-pos distractor lexemes to exist, matching the explicit
    // "enough same-pos distractor words exist" rule.
    public Set<ExerciseType> eligibleTypes(Lexeme lexeme, List<WordForm> forms, List<Translation> translations, LanguageCode baseLang) {
        Set<ExerciseType> types = EnumSet.noneOf(ExerciseType.class);
        Optional<Lexeme> translationPartner = findTranslationPartner(lexeme.getId(), translations, baseLang.name());

        if ("noun".equals(lexeme.getPos()) && lexeme.getGender() != null) {
            types.add(ExerciseType.en_ett);
        }
        // translate's distractors are the PARTNER's (base-language) same-pos
        // pool (buildDistractorCitations(partner,...)) - guard on the
        // partner having same-pos siblings, same "no any-pos fallback" rule
        // as LexemeRepository.findDistractorCandidates: a word without
        // enough same-pos siblings isn't eligible for a choice exercise that
        // needs them, rather than degrading to a 1-option "choice".
        if (translationPartner.filter(this::hasSamePosDistractors).isPresent()) {
            types.add(ExerciseType.translate);
        }
        if (!forms.isEmpty() && translationPartner.isPresent()) {
            types.add(ExerciseType.assemble);
        }
        // base_form's distractors are the lexeme's OWN same-pos pool
        // (buildDistractorCitations(lexeme,...)) - same guard reasoning.
        if (!citationFormResolver.nonCitationForms(lexeme, forms).isEmpty() && hasSamePosDistractors(lexeme)) {
            types.add(ExerciseType.base_form);
        }
        // produce_form's distractors are sibling forms of THIS word, not
        // other lexemes (see buildProduceForm) - so its own guard is "enough
        // distinct forms to offer a real distractor", not same-pos siblings.
        if (!citationFormResolver.nonCitationForms(lexeme, forms).isEmpty() && distinctFormStrings(forms).size() >= 2) {
            types.add(ExerciseType.produce_form);
        }
        if (distinctFormStrings(forms).size() >= 2 && hasSamePosDistractors(lexeme)) {
            types.add(ExerciseType.multi_select);
        }
        return types;
    }

    // Up to maxCount exercises for one word, preferring variety (distinct
    // types, no repeats) - the algorithm doesn't consult attempt history, so
    // "variety" here means within this session's queue for the word.
    public List<GeneratedExercise> buildExerciseQueue(
            Lexeme lexeme, List<WordForm> forms, List<Translation> translations,
            Set<ExerciseType> allowedTypes, LanguageCode targetLang, LanguageCode baseLang, int maxCount, boolean isNew
    ) {
        Set<ExerciseType> eligible = eligibleTypes(lexeme, forms, translations, baseLang);
        if (allowedTypes != null) {
            eligible = eligible.stream().filter(allowedTypes::contains).collect(Collectors.toSet());
        }
        if (eligible.isEmpty()) {
            return List.of();
        }

        // A newly-introduced word's intro card displays its FULL paradigm;
        // produce_form's distractors are always sibling forms of that same
        // paradigm (see buildProduceForm). Reordering isn't enough - ANY
        // produce_form exercise this session, anywhere in the queue, has its
        // whole answer key sitting a few items above it in the same
        // response. Exclude it entirely for this session; it's fair game
        // from the word's next session onward, once the card is no longer
        // part of what's on screen. `translate` is a guaranteed fallback for
        // every word (data invariant - every lexeme has a translation whose
        // partner has a same-pos sibling, see 02_seed.sql), so this should
        // never empty the pool; the size()>1 check is a defensive fallback
        // only, not the primary mechanism - it's better to give a word its
        // one remaining exercise type than none at all.
        Set<ExerciseType> queueable = eligible;
        if (isNew && eligible.size() > 1 && eligible.contains(ExerciseType.produce_form)) {
            queueable = EnumSet.copyOf(eligible);
            queueable.remove(ExerciseType.produce_form);
        }

        List<ExerciseType> pool = new ArrayList<>(queueable);
        Collections.shuffle(pool, random);

        List<GeneratedExercise> queue = new ArrayList<>();
        for (ExerciseType type : pool) {
            if (queue.size() >= maxCount) {
                break;
            }
            queue.add(build(type, lexeme, forms, translations, targetLang, baseLang));
        }
        return queue;
    }

    private GeneratedExercise build(ExerciseType type, Lexeme lexeme, List<WordForm> forms, List<Translation> translations, LanguageCode targetLang, LanguageCode baseLang) {
        return switch (type) {
            case en_ett -> buildEnEtt(lexeme, forms, targetLang);
            case translate -> buildTranslate(lexeme, forms, translations, targetLang, baseLang);
            case assemble -> buildAssemble(lexeme, forms, translations, baseLang);
            case base_form -> buildBaseForm(lexeme, forms, targetLang);
            case produce_form -> buildProduceForm(lexeme, forms, targetLang);
            case multi_select -> buildMultiSelect(lexeme, forms, targetLang);
        };
    }

    private GeneratedExercise buildEnEtt(Lexeme lexeme, List<WordForm> forms, LanguageCode targetLang) {
        // Bare citation form only - composing the article here would give
        // away the answer to the exercise that's testing the article itself.
        List<String> options = new ArrayList<>(List.of("en", "ett"));
        Collections.shuffle(options, random);
        return new GeneratedExercise(ExerciseType.en_ett, lexeme.getId(), null,
                new Prompt(citationFormResolver.citationFormString(lexeme, forms), targetLang),
                new EnEttPayload(options, lexeme.getGender()));
    }

    private GeneratedExercise buildTranslate(Lexeme lexeme, List<WordForm> forms, List<Translation> translations, LanguageCode targetLang, LanguageCode baseLang) {
        Lexeme partner = findTranslationPartner(lexeme.getId(), translations, baseLang.name()).orElseThrow();
        List<String> options = new ArrayList<>(buildDistractorCitations(partner, 3));
        String correctAnswer = composedCitation(partner);
        options.add(correctAnswer);
        Collections.shuffle(options, random);
        return new GeneratedExercise(ExerciseType.translate, lexeme.getId(), null,
                new Prompt(citationFormResolver.citationFormString(lexeme, forms), targetLang),
                new TranslatePayload(baseLang, options, correctAnswer));
    }

    private GeneratedExercise buildAssemble(Lexeme lexeme, List<WordForm> forms, List<Translation> translations, LanguageCode baseLang) {
        // The prompt is always the LEXEME-level translation (e.g. "go"), which
        // names the CITATION form, not any particular inflection - a
        // non-citation target (e.g. "gick") would show a mismatched clue
        // (this used to be pronoun-only, since case forms translate to
        // genuinely different English words like I/my/me, but the same
        // mismatch applies to every pos, just less jarringly). Assemble
        // always targets the citation/base form; other exercise types
        // (produce_form, multi_select) still provide form variety.
        List<WordForm> pool = forms.stream().filter(f -> citationFormResolver.isCitationForm(f, lexeme)).toList();
        if (pool.isEmpty()) {
            pool = forms;
        }
        WordForm target = pool.get(random.nextInt(pool.size()));
        Lexeme partner = findTranslationPartner(lexeme.getId(), translations, baseLang.name()).orElseThrow();
        Prompt prompt = new Prompt(partner.getLemma(), baseLang);

        String answer = target.getForm();
        List<String> letters = new ArrayList<>();
        answer.chars().forEach(c -> letters.add(String.valueOf((char) c)));
        int decoyCount = Math.max(1, (int) Math.round(answer.length() * 0.5));
        for (int i = 0; i < decoyCount; i++) {
            letters.add(String.valueOf(SWEDISH_ALPHABET.charAt(random.nextInt(SWEDISH_ALPHABET.length()))));
        }
        Collections.shuffle(letters, random);

        return new GeneratedExercise(ExerciseType.assemble, lexeme.getId(), target.getId().getFormType(),
                prompt, new AssemblePayload(letters, answer.length(), answer));
    }

    private GeneratedExercise buildBaseForm(Lexeme lexeme, List<WordForm> forms, LanguageCode targetLang) {
        WordForm target = pickRandom(citationFormResolver.nonCitationForms(lexeme, forms));
        String correctAnswer = citationFormResolver.composedWord(lexeme, forms);
        List<String> options = new ArrayList<>(buildDistractorCitations(lexeme, 3));
        options.add(correctAnswer);
        Collections.shuffle(options, random);
        return new GeneratedExercise(ExerciseType.base_form, lexeme.getId(), target.getId().getFormType(),
                new Prompt(target.getForm(), targetLang),
                new OptionsPayload(options, correctAnswer));
    }

    private GeneratedExercise buildProduceForm(Lexeme lexeme, List<WordForm> forms, LanguageCode targetLang) {
        WordForm target = pickRandom(citationFormResolver.nonCitationForms(lexeme, forms));
        // Distractors are sibling forms of the SAME word (not other
        // lexemes) - testing confusion within one paradigm, per the
        // produce_form example in doc/api/swagger.yaml.
        List<String> options = forms.stream()
                .map(WordForm::getForm)
                .filter(f -> !f.equals(target.getForm()))
                .distinct()
                .limit(3)
                .collect(Collectors.toCollection(ArrayList::new));
        options.add(target.getForm());
        Collections.shuffle(options, random);
        // The bare citation form alone ("flicka") doesn't say which sibling form is wanted -
        // every option is a grammatically valid inflection of it, so without naming the target
        // (e.g. "definite singular") more than one option reads as defensibly correct.
        String promptText = citationFormResolver.citationFormString(lexeme, forms)
                + " (" + citationFormResolver.formTypeLabel(target.getId().getFormType()) + ")";
        return new GeneratedExercise(ExerciseType.produce_form, lexeme.getId(), target.getId().getFormType(),
                new Prompt(promptText, targetLang),
                new OptionsPayload(options, target.getForm()));
    }

    private GeneratedExercise buildMultiSelect(Lexeme lexeme, List<WordForm> forms, LanguageCode targetLang) {
        List<String> correctAnswers = new ArrayList<>(distinctFormStrings(forms));
        List<String> decoys = samePosDistractorLexemes(lexeme).stream()
                .flatMap(other -> wordFormRepository.findByLexemeId(other.getId()).stream())
                .map(WordForm::getForm)
                .filter(f -> !correctAnswers.contains(f))
                .distinct()
                .limit(2)
                .toList();
        List<String> options = new ArrayList<>(correctAnswers);
        options.addAll(decoys);
        Collections.shuffle(options, random);
        return new GeneratedExercise(ExerciseType.multi_select, lexeme.getId(), null,
                new Prompt(citationFormResolver.citationFormString(lexeme, forms), targetLang),
                new MultiSelectPayload(options, correctAnswers));
    }

    private Set<String> distinctFormStrings(List<WordForm> forms) {
        return forms.stream().map(WordForm::getForm).collect(Collectors.toSet());
    }

    // ---- distractors ----

    private List<Lexeme> samePosDistractorLexemes(Lexeme lexeme) {
        int freqRank = lexeme.getFreqRank() == null ? 0 : lexeme.getFreqRank();
        return lexemeRepository.findDistractorCandidates(lexeme.getLanguage().getCode(), lexeme.getPos(), lexeme.getId(), freqRank);
    }

    private boolean hasSamePosDistractors(Lexeme lexeme) {
        return !samePosDistractorLexemes(lexeme).isEmpty();
    }

    private String composedCitation(Lexeme lexeme) {
        return citationFormResolver.composedWord(lexeme, wordFormRepository.findByLexemeId(lexeme.getId()));
    }

    // Each distractor's OWN citation form, composed with its own article if
    // applicable - otherwise only the correct answer would carry an
    // article, giving the answer away.
    private List<String> buildDistractorCitations(Lexeme answerLexeme, int desiredCount) {
        String answerCitation = composedCitation(answerLexeme);
        return samePosDistractorLexemes(answerLexeme).stream()
                .map(this::composedCitation)
                .filter(c -> !c.equals(answerCitation))
                .distinct()
                .limit(desiredCount)
                .toList();
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

    private WordForm pickRandom(List<WordForm> forms) {
        return forms.get(random.nextInt(forms.size()));
    }

    // itemId is deliberately not part of this record - assigned by the
    // caller (SessionService) once it knows the final interleaved position
    // in the session's items array.
    public record GeneratedExercise(ExerciseType exerciseType, Long lexemeId, String formType, Prompt prompt, ExercisePayload exercise) {
    }
}
