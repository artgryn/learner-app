package com.artgr.learner.service;

import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.ListItem;
import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.repository.ListItemRepository;
import com.artgr.learner.data.repository.ListProgressRepository;
import com.artgr.learner.properties.SessionProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Selects which words go into a session. Kept separate from exercise
// generation so word-selection/interleaving logic is unit-testable without
// touching the DB.
@Component
public class SessionScheduler {

    private final ListItemRepository listItemRepository;
    private final ListProgressRepository listProgressRepository;
    private final SessionProperties sessionProperties;

    public SessionScheduler(
            ListItemRepository listItemRepository,
            ListProgressRepository listProgressRepository,
            SessionProperties sessionProperties
    ) {
        this.listItemRepository = listItemRepository;
        this.listProgressRepository = listProgressRepository;
        this.sessionProperties = sessionProperties;
    }

    // Review words (already introduced - have a list_progress row) first,
    // then never-started words. Deliberately NOT time-gated: the app must
    // never block practice with a due-date cooldown, so every in-progress
    // word is always an eligible candidate, not just ones "due now"
    // (`due` is still written per word in SessionService.upsertProgress, but
    // is informational only - nothing reads it to exclude a word here).
    // Not-yet-mastered words are prioritized ahead of already-mastered ones
    // within the review budget, so a small list doesn't waste review slots
    // recycling learned words while one still needing work sits idle - a
    // mastered word can still resurface for review once there's spare budget,
    // that's still the point of spaced repetition, just not time-gated.
    public List<Candidate> selectCandidates(Long userId, Long listId) {
        List<ListProgress> progressRows = listProgressRepository.findByUserIdAndListId(userId, listId);
        int masteryThreshold = sessionProperties.getMasteryThreshold();

        List<ListProgress> notMastered = new ArrayList<>();
        List<ListProgress> mastered = new ArrayList<>();
        for (ListProgress lp : progressRows) {
            (lp.getTimesPracticed() >= masteryThreshold ? mastered : notMastered).add(lp);
        }

        List<Candidate> candidates = new ArrayList<>();
        Stream.concat(notMastered.stream(), mastered.stream())
                .limit(sessionProperties.getReviewWordsPerSession())
                .forEach(lp -> candidates.add(new Candidate(lp.getLexeme(), false)));

        Set<Long> startedLexemeIds = progressRows.stream()
                .map(lp -> lp.getId().getLexemeId())
                .collect(Collectors.toSet());

        int newBudget = sessionProperties.getNewWordsPerSession();
        int newCount = 0;
        for (ListItem item : listItemRepository.findByListIdOrderByPosition(listId)) {
            if (newCount >= newBudget) {
                break;
            }
            Long lexemeId = item.getLexeme().getId();
            if (!startedLexemeIds.contains(lexemeId)) {
                candidates.add(new Candidate(item.getLexeme(), true));
                newCount++;
            }
        }
        return candidates;
    }

    public record Candidate(Lexeme lexeme, boolean isNew) {
    }
}
