package com.artgr.learner.service;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.entity.Language;
import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.entity.UserListId;
import com.artgr.learner.data.entity.WordList;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.repository.AccountRepository;
import com.artgr.learner.data.repository.LanguageRepository;
import com.artgr.learner.data.repository.ListProgressRepository;
import com.artgr.learner.data.repository.UserListRepository;
import com.artgr.learner.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class  EnrollmentService {

    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_COMPLETED = "completed";

    private final UserListRepository userListRepository;
    private final AccountRepository accountRepository;
    private final LanguageRepository languageRepository;
    private final ListProgressRepository listProgressRepository;
    private final CatalogService catalogService;
    private final ProgressService progressService;

    public EnrollmentService(
            UserListRepository userListRepository,
            AccountRepository accountRepository,
            LanguageRepository languageRepository,
            ListProgressRepository listProgressRepository,
            CatalogService catalogService,
            ProgressService progressService
    ) {
        this.userListRepository = userListRepository;
        this.accountRepository = accountRepository;
        this.languageRepository = languageRepository;
        this.listProgressRepository = listProgressRepository;
        this.catalogService = catalogService;
        this.progressService = progressService;
    }

    // "Enrolling twice returns the existing enrollment" (doc/api/swagger.yaml)
    // - idempotent by (userId, listId). EnrollResult.created tells the
    // caller whether to answer 200 (existing) or 201 (created). A user has
    // at most ONE active enrollment (MVP, doc/app-info/Data/user_list.md) -
    // switching to a different list deletes the current active one first;
    // @Transactional so that delete+insert is atomic.
    //
    // Enrolling a list that is already 'completed' REACTIVATES the SAME row
    // (status -> active, completed_at -> null) instead of creating a new one
    // - "restart" means resume/review with existing list_progress intact, not
    // wipe-and-redo (doc/app-info/Data/user_list.md). Still subject to the
    // one-active-list collision rule below.
    @Transactional
    public EnrollResult enroll(Long userId, Long listId, LanguageCode baseLang) {
        WordList list = catalogService.requireList(listId);

        Optional<UserList> existing = userListRepository.findByUserIdAndListId(userId, listId);
        if (existing.isPresent()) {
            UserList enrollment = existing.get();
            if (!STATUS_COMPLETED.equals(enrollment.getStatus())) {
                return new EnrollResult(enrollment, false);
            }
            deleteOtherActiveEnrollment(userId, listId);
            enrollment.setStatus(STATUS_ACTIVE);
            enrollment.setCompletedAt(null);
            userListRepository.save(enrollment);
            UserList reactivated = userListRepository.findByUserIdAndListId(userId, listId).orElseThrow();
            return new EnrollResult(reactivated, false);
        }

        deleteOtherActiveEnrollment(userId, listId);

        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Account " + userId + " not found"));
        Language language = languageRepository.findById(baseLang.name())
                .orElseThrow(() -> new NotFoundException("Language " + baseLang + " not found"));

        UserList enrollment = new UserList();
        enrollment.setId(new UserListId(userId, listId));
        enrollment.setAccount(account);
        enrollment.setList(list);
        enrollment.setBaseLang(language);
        userListRepository.save(enrollment);

        // Re-fetch join-fetched (see UserListRepository) so the associations
        // the caller maps to a DTO are initialized, not lazy proxies.
        UserList saved = userListRepository.findByUserIdAndListId(userId, listId).orElseThrow();
        return new EnrollResult(saved, true);
    }

    // DB FK ON DELETE CASCADE (01_schema.sql) cleans up the old enrollment's
    // list_progress/sessions - do not hand-delete those here. attempt rows
    // are left behind (denormalized historical log, see attempt.md).
    //
    // flush() is required, not optional: Hibernate's flush action queue
    // always orders entity INSERTs before DELETEs within a transaction,
    // regardless of the order repository calls happen in code. Without this
    // flush, the new enrollment's INSERT would hit the DB before this
    // DELETE, transiently violating the one_active_enrollment partial unique
    // index (user_list(user_id) WHERE status='active') even though the net
    // result after commit would have been correct.
    private void deleteOtherActiveEnrollment(Long userId, Long listId) {
        userListRepository.findByUserId(userId).stream()
                .filter(ul -> STATUS_ACTIVE.equals(ul.getStatus()) && !ul.getId().getListId().equals(listId))
                .forEach(userListRepository::delete);
        userListRepository.flush();
    }

    // Auto-completion: called after a session finishes writing results
    // (SessionService.completeSession). Every word in the list has reached
    // masteryThreshold -> flip the enrollment to 'completed'. Idempotent (a
    // no-op if already completed, or if the enrollment was deleted/switched
    // away mid-flow). Deliberately does NOT touch list_progress/sessions/
    // attempt - those are the history and must survive completion
    // (doc/app-info/Data/user_list.md).
    public void completeIfMastered(Long userId, Long listId) {
        UserList enrollment = userListRepository.findByUserIdAndListId(userId, listId).orElse(null);
        if (enrollment == null || STATUS_COMPLETED.equals(enrollment.getStatus())) {
            return;
        }
        int total = progressService.totalWords(listId);
        if (total > 0 && progressService.wordsMastered(userId, listId) >= total) {
            enrollment.setStatus(STATUS_COMPLETED);
            enrollment.setCompletedAt(OffsetDateTime.now());
            userListRepository.save(enrollment);
        }
    }

    public List<UserList> enrollments(Long userId) {
        return userListRepository.findByUserId(userId);
    }

    public UserList requireEnrollment(Long userId, Long listId) {
        return userListRepository.findByUserIdAndListId(userId, listId)
                .orElseThrow(() -> new NotFoundException("Enrollment for list " + listId + " not found"));
    }

    public void unenroll(Long userId, Long listId) {
        UserListId id = new UserListId(userId, listId);
        if (!userListRepository.existsById(id)) {
            throw new NotFoundException("Enrollment for list " + listId + " not found");
        }
        // Cascades to sessions/list_progress (FK ON DELETE CASCADE, 01_schema.sql).
        // attempt rows are intentionally left behind - denormalized historical
        // log, independent of enrollment lifecycle (see doc/app-info/Data/attempt.md).
        userListRepository.deleteById(id);
    }

    public List<ListProgress> progress(Long userId, Long listId) {
        return listProgressRepository.findByUserIdAndListId(userId, listId);
    }

    public record EnrollResult(UserList enrollment, boolean created) {
    }
}
