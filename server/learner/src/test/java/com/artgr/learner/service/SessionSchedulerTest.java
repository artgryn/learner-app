package com.artgr.learner.service;

import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.ListItem;
import com.artgr.learner.data.entity.ListItemId;
import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.entity.ListProgressId;
import com.artgr.learner.data.repository.ListItemRepository;
import com.artgr.learner.data.repository.ListProgressRepository;
import com.artgr.learner.properties.SessionProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Pure unit test (no Spring context, no DB) - controls exactly which words
// look "in progress" vs "new" via mocked repositories.
class SessionSchedulerTest {

    private final ListItemRepository listItemRepository = mock(ListItemRepository.class);
    private final ListProgressRepository listProgressRepository = mock(ListProgressRepository.class);

    private SessionScheduler scheduler(int newPerSession, int reviewPerSession) {
        return scheduler(newPerSession, reviewPerSession, 3);
    }

    private SessionScheduler scheduler(int newPerSession, int reviewPerSession, int masteryThreshold) {
        SessionProperties properties = new SessionProperties();
        properties.setNewWordsPerSession(newPerSession);
        properties.setReviewWordsPerSession(reviewPerSession);
        properties.setMasteryThreshold(masteryThreshold);
        return new SessionScheduler(listItemRepository, listProgressRepository, properties);
    }

    private static Language sv() {
        Language language = new Language();
        language.setCode("sv");
        language.setName("Swedish");
        return language;
    }

    private static Lexeme lexeme(long id, String lemma) {
        Lexeme lexeme = new Lexeme();
        lexeme.setId(id);
        lexeme.setLanguage(sv());
        lexeme.setLemma(lemma);
        lexeme.setPos("noun");
        return lexeme;
    }

    private static ListItem item(long listId, Lexeme lexeme, int position) {
        ListItem item = new ListItem();
        item.setId(new ListItemId(listId, lexeme.getId()));
        item.setLexeme(lexeme);
        item.setPosition(position);
        return item;
    }

    private static ListProgress progress(long userId, long listId, Lexeme lexeme, int timesPracticed) {
        ListProgress progress = new ListProgress();
        progress.setId(new ListProgressId(userId, listId, lexeme.getId()));
        progress.setLexeme(lexeme);
        progress.setTimesPracticed(timesPracticed);
        return progress;
    }

    @Test
    void reviewWordsAreMarkedNotNewAndNewWordsAreMarkedNew() {
        Lexeme reviewWord = lexeme(1L, "hus");
        Lexeme newWord = lexeme(2L, "bil");

        when(listProgressRepository.findByUserIdAndListId(1L, 10L)).thenReturn(
                List.of(progress(1L, 10L, reviewWord, 1)));
        when(listItemRepository.findByListIdOrderByPosition(10L)).thenReturn(
                List.of(item(10L, reviewWord, 1), item(10L, newWord, 2)));

        List<SessionScheduler.Candidate> candidates = scheduler(5, 5).selectCandidates(1L, 10L);

        assertEquals(2, candidates.size());
        assertEquals(1L, candidates.get(0).lexeme().getId());
        assertTrue(!candidates.get(0).isNew(), "already-started word must not get a card");
        assertEquals(2L, candidates.get(1).lexeme().getId());
        assertTrue(candidates.get(1).isNew(), "never-started word must be marked new");
    }

    @Test
    void newWordBudgetIsRespected() {
        Lexeme w1 = lexeme(1L, "a");
        Lexeme w2 = lexeme(2L, "b");
        Lexeme w3 = lexeme(3L, "c");

        when(listProgressRepository.findByUserIdAndListId(1L, 10L)).thenReturn(List.of());
        when(listItemRepository.findByListIdOrderByPosition(10L)).thenReturn(
                List.of(item(10L, w1, 1), item(10L, w2, 2), item(10L, w3, 3)));

        List<SessionScheduler.Candidate> candidates = scheduler(2, 5).selectCandidates(1L, 10L);

        assertEquals(2, candidates.size(), "newWordsPerSession=2 must cap the number of new words");
        assertEquals(1L, candidates.get(0).lexeme().getId());
        assertEquals(2L, candidates.get(1).lexeme().getId());
    }

    @Test
    void reviewBudgetIsRespectedViaRepositoryLimitAndInMemoryLimit() {
        Lexeme w1 = lexeme(1L, "a");
        Lexeme w2 = lexeme(2L, "b");
        Lexeme w3 = lexeme(3L, "c");

        when(listProgressRepository.findByUserIdAndListId(1L, 10L)).thenReturn(
                List.of(progress(1L, 10L, w1, 1), progress(1L, 10L, w2, 1), progress(1L, 10L, w3, 1)));
        when(listItemRepository.findByListIdOrderByPosition(10L)).thenReturn(List.of());

        List<SessionScheduler.Candidate> candidates = scheduler(5, 2).selectCandidates(1L, 10L);

        assertEquals(2, candidates.size(), "reviewWordsPerSession=2 must cap the number of review words");
    }

    @Test
    void reviewCandidatesAreNeverTimeGated() {
        // Practiced "recently" in wall-clock terms is irrelevant - the only
        // things that matter are mastery status and the review budget. There
        // is no `due`/cooldown concept for SessionScheduler to consult at all.
        Lexeme w1 = lexeme(1L, "a");

        when(listProgressRepository.findByUserIdAndListId(1L, 10L)).thenReturn(
                List.of(progress(1L, 10L, w1, 1)));
        when(listItemRepository.findByListIdOrderByPosition(10L)).thenReturn(List.of());

        List<SessionScheduler.Candidate> candidates = scheduler(5, 5).selectCandidates(1L, 10L);

        assertEquals(1, candidates.size(), "an in-progress word is always eligible, immediately, every call");
    }

    @Test
    void notYetMasteredWordsAreReviewedBeforeAlreadyMasteredOnes() {
        Lexeme mastered = lexeme(1L, "done");
        Lexeme learning = lexeme(2L, "wip");

        // mastered first in repository order, on purpose - the scheduler must
        // reorder, not just pass the DB order through.
        when(listProgressRepository.findByUserIdAndListId(1L, 10L)).thenReturn(
                List.of(progress(1L, 10L, mastered, 3), progress(1L, 10L, learning, 1)));
        when(listItemRepository.findByListIdOrderByPosition(10L)).thenReturn(List.of());

        List<SessionScheduler.Candidate> candidates = scheduler(0, 1, 3).selectCandidates(1L, 10L);

        assertEquals(1, candidates.size(), "reviewWordsPerSession=1 leaves room for only one");
        assertEquals(2L, candidates.get(0).lexeme().getId(),
                "the not-yet-mastered word must win the single review slot over the mastered one");
    }
}
