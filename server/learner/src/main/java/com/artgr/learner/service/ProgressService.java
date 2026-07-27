package com.artgr.learner.service;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.repository.AccountRepository;
import com.artgr.learner.data.repository.LearningSessionRepository;
import com.artgr.learner.data.repository.ListItemRepository;
import com.artgr.learner.data.repository.ListProgressRepository;
import com.artgr.learner.data.repository.UserListRepository;
import com.artgr.learner.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    // "≥3 spaced encounters across different sessions" is the only mastery
    // signal documented (doc/app-info/MVP.md); used here as the threshold.
    // Not an explicit product decision - revisit if that changes.
    private static final int MASTERY_THRESHOLD = 3;

    // No fixed session size is specified anywhere in doc/app-info/ - sessions
    // are "6-10 min", not a word count. Placeholder until real session
    // derivation (learning_plan) exists to compute this properly.
    private static final int WORDS_PER_SESSION = 5;

    private final AccountRepository accountRepository;
    private final UserListRepository userListRepository;
    private final ListItemRepository listItemRepository;
    private final ListProgressRepository listProgressRepository;
    private final LearningSessionRepository learningSessionRepository;

    public ProgressService(
            AccountRepository accountRepository,
            UserListRepository userListRepository,
            ListItemRepository listItemRepository,
            ListProgressRepository listProgressRepository,
            LearningSessionRepository learningSessionRepository
    ) {
        this.accountRepository = accountRepository;
        this.userListRepository = userListRepository;
        this.listItemRepository = listItemRepository;
        this.listProgressRepository = listProgressRepository;
        this.learningSessionRepository = learningSessionRepository;
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
        return (int) listProgressRepository.countMastered(userId, listId, MASTERY_THRESHOLD);
    }

    public int sessionsDone(Long userId, Long listId) {
        return (int) learningSessionRepository.countByUserIdAndListId(userId, listId);
    }

    public int estimatedTotalSessions(Long listId) {
        int words = totalWords(listId);
        return (int) Math.ceil(words / (double) WORDS_PER_SESSION);
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
        return (int) listProgressRepository.countDistinctMasteredLexemes(userId, MASTERY_THRESHOLD);
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
