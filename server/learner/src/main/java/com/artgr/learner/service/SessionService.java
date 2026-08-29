package com.artgr.learner.service;

import com.artgr.learner.data.entity.Attempt;
import com.artgr.learner.data.entity.LearningSession;
import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.entity.ListProgressId;
import com.artgr.learner.data.entity.Translation;
import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.entity.WordForm;
import com.artgr.learner.data.entity.WordList;
import com.artgr.learner.data.enums.ExerciseType;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.CompleteResponse;
import com.artgr.learner.data.presentation.ExerciseItem;
import com.artgr.learner.data.presentation.IntroduceItem;
import com.artgr.learner.data.presentation.Result;
import com.artgr.learner.data.presentation.ResultsBatch;
import com.artgr.learner.data.presentation.SessionItem;
import com.artgr.learner.data.presentation.SessionResponse;
import com.artgr.learner.data.presentation.WordProgress;
import com.artgr.learner.data.repository.AccountRepository;
import com.artgr.learner.data.repository.AttemptRepository;
import com.artgr.learner.data.repository.LearningSessionRepository;
import com.artgr.learner.data.repository.LexemeRepository;
import com.artgr.learner.data.repository.ListProgressRepository;
import com.artgr.learner.data.repository.TranslationRepository;
import com.artgr.learner.data.repository.WordFormRepository;
import com.artgr.learner.data.repository.WordListRepository;
import com.artgr.learner.exceptions.NotFoundException;
import com.artgr.learner.properties.SessionProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// "Sessions are derived on demand, never precomputed. Only COMPLETED sessions
// are logged" (doc/CLAUDE.md) - so between derive and complete there is
// deliberately no DB row. pendingSessions is the in-memory stand-in: it is
// NOT part of any DB transaction (won't roll back with test @Transactional,
// won't survive a restart) - that's intentional, matching "no half-session
// is restored from the server". sessionId is an opaque token, never looked
// up against the sessions table (which has no UUID column - schema frozen).
@Service
public class SessionService {

    // `due` is informational only - NOT an availability gate. The app must
    // never block a user from practicing with a cooldown/due date; nothing
    // reads these values to exclude a word from a session (see
    // SessionScheduler.selectCandidates, which selects by mastery status,
    // not by due date). Kept as a seam for a later real spaced-repetition
    // algorithm (doc/app-info/Data/learning_plan.md) and as a display hint.
    private static final Duration DUE_INTERVAL_CORRECT = Duration.ofDays(1);
    private static final Duration DUE_INTERVAL_INCORRECT = Duration.ofMinutes(10);

    private final EnrollmentService enrollmentService;
    private final SessionScheduler sessionScheduler;
    private final ExerciseGenerator exerciseGenerator;
    private final IntroCardBuilder introCardBuilder;
    private final WordFormRepository wordFormRepository;
    private final TranslationRepository translationRepository;
    private final ListProgressRepository listProgressRepository;
    private final AttemptRepository attemptRepository;
    private final LearningSessionRepository learningSessionRepository;
    private final AccountRepository accountRepository;
    private final WordListRepository wordListRepository;
    private final LexemeRepository lexemeRepository;
    private final SessionProperties sessionProperties;

    private final Map<String, PendingSession> pendingSessions = new ConcurrentHashMap<>();

    public SessionService(
            EnrollmentService enrollmentService,
            SessionScheduler sessionScheduler,
            ExerciseGenerator exerciseGenerator,
            IntroCardBuilder introCardBuilder,
            WordFormRepository wordFormRepository,
            TranslationRepository translationRepository,
            ListProgressRepository listProgressRepository,
            AttemptRepository attemptRepository,
            LearningSessionRepository learningSessionRepository,
            AccountRepository accountRepository,
            WordListRepository wordListRepository,
            LexemeRepository lexemeRepository,
            SessionProperties sessionProperties
    ) {
        this.enrollmentService = enrollmentService;
        this.sessionScheduler = sessionScheduler;
        this.exerciseGenerator = exerciseGenerator;
        this.introCardBuilder = introCardBuilder;
        this.wordFormRepository = wordFormRepository;
        this.translationRepository = translationRepository;
        this.listProgressRepository = listProgressRepository;
        this.attemptRepository = attemptRepository;
        this.learningSessionRepository = learningSessionRepository;
        this.accountRepository = accountRepository;
        this.wordListRepository = wordListRepository;
        this.lexemeRepository = lexemeRepository;
        this.sessionProperties = sessionProperties;
    }

    // ===================== derive =====================

    @Transactional(readOnly = true)
    public SessionResponse deriveSession(Long userId, Long listId) {
        UserList enrollment = enrollmentService.requireEnrollment(userId, listId);
        WordList list = enrollment.getList();
        LanguageCode targetLang = LanguageCode.valueOf(list.getTargetLang().getCode());
        LanguageCode baseLang = LanguageCode.valueOf(enrollment.getBaseLang().getCode());
        Set<ExerciseType> allowedTypes = list.getAllowedExercises();

        List<WordPlan> plans = new ArrayList<>();
        for (SessionScheduler.Candidate candidate : sessionScheduler.selectCandidates(userId, listId)) {
            Lexeme lexeme = candidate.lexeme();
            List<WordForm> forms = wordFormRepository.findByLexemeId(lexeme.getId());
            List<Translation> translations = translationRepository.findByLexemeId(lexeme.getId());

            List<ExerciseGenerator.GeneratedExercise> queue = exerciseGenerator.buildExerciseQueue(
                    lexeme, forms, translations, allowedTypes, targetLang, baseLang,
                    sessionProperties.getExercisesPerWordPerSession(), candidate.isNew());
            if (queue.isEmpty()) {
                // Word qualifies for nothing under this list's allowed
                // types - skip rather than fail (doc/CLAUDE.md).
                continue;
            }

            IntroduceItem.Card card = candidate.isNew()
                    ? introCardBuilder.build(lexeme, forms, translations, baseLang)
                    : null;
            plans.add(new WordPlan(lexeme.getId(), card, new ArrayDeque<>(queue)));
        }

        List<SessionItem> items = interleave(plans);

        String sessionId = UUID.randomUUID().toString();
        pendingSessions.put(sessionId, new PendingSession(userId, listId, OffsetDateTime.now()));
        return new SessionResponse(sessionId, listId, items);
    }

    // Round-robin: one exercise per active word per round, so the same
    // word's exercises are never adjacent. A new word's intro card is
    // emitted together with (immediately before) its first exercise.
    private List<SessionItem> interleave(List<WordPlan> plans) {
        List<SessionItem> items = new ArrayList<>();
        int index = 1;
        boolean progressed = true;
        while (progressed) {
            progressed = false;
            for (WordPlan plan : plans) {
                if (plan.exercises.isEmpty()) {
                    continue;
                }
                if (plan.card != null && !plan.introEmitted) {
                    items.add(new IntroduceItem("it_%02d".formatted(index), plan.lexemeId, plan.card));
                    index++;
                    plan.introEmitted = true;
                }
                ExerciseGenerator.GeneratedExercise generated = plan.exercises.poll();
                items.add(new ExerciseItem(
                        "it_%02d".formatted(index),
                        generated.exerciseType(),
                        generated.lexemeId(),
                        generated.formType(),
                        generated.prompt(),
                        generated.exercise()
                ));
                index++;
                progressed = true;
            }
        }
        return items;
    }

    // ===================== complete =====================

    @Transactional
    public CompleteResponse completeSession(String sessionId, ResultsBatch resultsBatch) {
        PendingSession pending = pendingSessions.remove(sessionId);
        if (pending == null) {
            throw new NotFoundException("Session " + sessionId + " not found");
        }

        LearningSession session = new LearningSession();
        session.setUserId(pending.userId());
        session.setListId(pending.listId());
        session.setStartedAt(pending.startedAt());
        session.setEndedAt(OffsetDateTime.now());
        LearningSession savedSession = learningSessionRepository.save(session);

        Set<Long> touchedLexemeIds = new LinkedHashSet<>();
        for (Result result : resultsBatch.results()) {
            Attempt attempt = new Attempt();
            attempt.setAccount(accountRepository.getReferenceById(pending.userId()));
            attempt.setList(wordListRepository.getReferenceById(pending.listId()));
            attempt.setLexeme(lexemeRepository.getReferenceById(result.lexemeId()));
            attempt.setSession(savedSession);
            attempt.setExerciseType(result.exerciseType());
            attempt.setFormType(result.formType());
            attempt.setCorrect(result.isCorrect());
            attempt.setElapsedMs(result.elapsedMs());
            attemptRepository.save(attempt);

            upsertProgress(pending.userId(), pending.listId(), result.lexemeId(), result.isCorrect());
            touchedLexemeIds.add(result.lexemeId());
        }

        // Auto-completion check - every word mastered flips the enrollment
        // to 'completed' (doc/app-info/Data/user_list.md). Runs after
        // progress is written so it sees this session's results.
        enrollmentService.completeIfMastered(pending.userId(), pending.listId());

        return new CompleteResponse(sessionId, progressFor(pending.userId(), pending.listId(), touchedLexemeIds));
    }

    private void upsertProgress(Long userId, Long listId, Long lexemeId, boolean correct) {
        ListProgressId id = new ListProgressId(userId, listId, lexemeId);
        ListProgress progress = listProgressRepository.findById(id).orElseGet(() -> {
            ListProgress created = new ListProgress();
            created.setId(id);
            created.setLexeme(lexemeRepository.getReferenceById(lexemeId));
            return created;
        });
        progress.setTimesPracticed(progress.getTimesPracticed() + 1);
        OffsetDateTime now = OffsetDateTime.now();
        if (correct) {
            progress.setCorrect(progress.getCorrect() + 1);
            progress.setDue(now.plus(DUE_INTERVAL_CORRECT));
        } else {
            progress.setWrong(progress.getWrong() + 1);
            progress.setDue(now.plus(DUE_INTERVAL_INCORRECT));
        }
        listProgressRepository.save(progress);
    }

    private List<WordProgress> progressFor(Long userId, Long listId, Set<Long> lexemeIds) {
        return lexemeIds.stream()
                .map(lexemeId -> listProgressRepository.findById(new ListProgressId(userId, listId, lexemeId)))
                .flatMap(Optional::stream)
                .map(this::toWordProgress)
                .toList();
    }

    private WordProgress toWordProgress(ListProgress progress) {
        return new WordProgress(
                progress.getId().getLexemeId(),
                progress.getTimesPracticed(),
                progress.getCorrect(),
                progress.getWrong(),
                progress.getDue()
        );
    }

    private record PendingSession(Long userId, Long listId, OffsetDateTime startedAt) {
    }

    private static final class WordPlan {
        private final Long lexemeId;
        private final IntroduceItem.Card card;
        private final Deque<ExerciseGenerator.GeneratedExercise> exercises;
        private boolean introEmitted;

        private WordPlan(Long lexemeId, IntroduceItem.Card card, Deque<ExerciseGenerator.GeneratedExercise> exercises) {
            this.lexemeId = lexemeId;
            this.card = card;
            this.exercises = exercises;
        }
    }
}
