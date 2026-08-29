package com.artgr.learner.service;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.repository.AccountRepository;
import com.artgr.learner.data.repository.LearningSessionRepository;
import com.artgr.learner.data.repository.ListItemRepository;
import com.artgr.learner.data.repository.ListProgressRepository;
import com.artgr.learner.data.repository.UserListRepository;
import com.artgr.learner.exceptions.NotFoundException;
import com.artgr.learner.properties.SessionProperties;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final AccountRepository accountRepository;
    private final UserListRepository userListRepository;
    private final ListItemRepository listItemRepository;
    private final ListProgressRepository listProgressRepository;
    private final LearningSessionRepository learningSessionRepository;
    private final SessionProperties sessionProperties;

    public ProgressService(
            AccountRepository accountRepository,
            UserListRepository userListRepository,
            ListItemRepository listItemRepository,
            ListProgressRepository listProgressRepository,
            LearningSessionRepository learningSessionRepository,
            SessionProperties sessionProperties
    ) {
        this.accountRepository = accountRepository;
        this.userListRepository = userListRepository;
        this.listItemRepository = listItemRepository;
        this.listProgressRepository = listProgressRepository;
        this.learningSessionRepository = learningSessionRepository;
        this.sessionProperties = sessionProperties;
    }

    public Account requireAccount(Long userId) {
        return accountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Account " + userId + " not found"));
    }

    public List<UserList> enrollments(Long userId) {
        return userListRepository.findByUserId(userId);
    }

    public int totalWords(Long listId) {
        return (int) listItemRepository.countByListId(listId);
    }

    public int wordsMastered(Long userId, Long listId) {
        return (int) listProgressRepository.countMastered(userId, listId, sessionProperties.getMasteryThreshold());
    }

    // Word-level count (wordsMastered/totalWords) is coarse - a word only
    // counts once FULLY mastered. These give a finer, exercise-level fraction
    // for a progress bar toward "list learned" (e.g. 6 of 9 reps done, not
    // just 0 of 3 words). masteryThreshold is always read from config here,
    // never hardcoded - the MVP default is 3 but production may differ.
    public int exercisesCompleted(Long userId, Long listId) {
        return (int) listProgressRepository.sumCappedTimesPracticed(userId, listId, sessionProperties.getMasteryThreshold());
    }

    public int exercisesNeeded(Long listId) {
        return totalWords(listId) * sessionProperties.getMasteryThreshold();
    }

    public int sessionsDone(Long userId, Long listId) {
        return (int) learningSessionRepository.countByUserIdAndListId(userId, listId);
    }

    public int estimatedTotalSessions(Long listId) {
        int words = totalWords(listId);
        return (int) Math.ceil(words / (double) sessionProperties.getWordsPerSession());
    }

    public OffsetDateTime lastActiveAt(UserList enrollment) {
        OffsetDateTime lastSession = learningSessionRepository.findLastActiveAt(
                enrollment.getId().getUserId(), enrollment.getId().getListId());
        OffsetDateTime enrolledAt = enrollment.getStartedAt();
        if (lastSession == null) {
            return enrolledAt;
        }
        return lastSession.isAfter(enrolledAt) ? lastSession : enrolledAt;
    }

    public Long resumeListId(List<UserList> enrollments) {
        return enrollments.stream()
                .max(Comparator.comparing(this::lastActiveAt))
                .map(e -> e.getId().getListId())
                .orElse(null);
    }

    public int wordsKnown(Long userId) {
        return (int) listProgressRepository.countDistinctMasteredLexemes(userId, sessionProperties.getMasteryThreshold());
    }

    public int streakDays(Long userId) {
        Set<LocalDate> activeDates = learningSessionRepository.findActiveTimestamps(userId).stream()
                .map(OffsetDateTime::toLocalDate)
                .collect(Collectors.toSet());
        LocalDate today = LocalDate.now();
        LocalDate cursor = today;
        if (!activeDates.contains(cursor)) {
            cursor = today.minusDays(1);
            if (!activeDates.contains(cursor)) {
                return 0;
            }
        }
        int streak = 0;
        while (activeDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }
}
