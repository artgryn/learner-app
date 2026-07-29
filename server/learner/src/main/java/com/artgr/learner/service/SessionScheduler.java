package com.artgr.learner.service;

import com.artgr.learner.data.entity.Lexeme;
import com.artgr.learner.data.entity.ListItem;
import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.repository.ListItemRepository;
import com.artgr.learner.data.repository.ListProgressRepository;
import com.artgr.learner.properties.SessionProperties;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // Review words (already introduced - have a list_progress row, due now
    // or never scheduled) first, then never-started words. No mastery
    // exclusion here: a mastered word can still resurface once its due date
    // arrives - that's the point of spaced repetition, unlike the earlier
    // (reverted) attempt which excluded mastered words permanently.
    public List<Candidate> selectCandidates(Long userId, Long listId) {
        OffsetDateTime now = OffsetDateTime.now();

        List<Candidate> candidates = new ArrayList<>();
        listProgressRepository.findReviewCandidates(userId, listId, now).stream()
                .limit(sessionProperties.getReviewWordsPerSession())
                .forEach(lp -> candidates.add(new Candidate(lp.getLexeme(), false)));

        Set<Long> startedLexemeIds = listProgressRepository.findByUserIdAndListId(userId, listId).stream()
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
