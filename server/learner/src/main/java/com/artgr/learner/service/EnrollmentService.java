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

import java.util.List;
import java.util.Optional;

@Service
public class  EnrollmentService {

    private final UserListRepository userListRepository;
    private final AccountRepository accountRepository;
    private final LanguageRepository languageRepository;
    private final ListProgressRepository listProgressRepository;
    private final CatalogService catalogService;

    public EnrollmentService(
            UserListRepository userListRepository,
            AccountRepository accountRepository,
            LanguageRepository languageRepository,
            ListProgressRepository listProgressRepository,
            CatalogService catalogService
    ) {
        this.userListRepository = userListRepository;
        this.accountRepository = accountRepository;
        this.languageRepository = languageRepository;
        this.listProgressRepository = listProgressRepository;
        this.catalogService = catalogService;
    }

    // "Enrolling twice returns the existing enrollment" (doc/api/swagger.yaml)
    // - idempotent by (userId, listId). EnrollResult.created tells the
    // caller whether to answer 200 (existing) or 201 (created).
    public EnrollResult enroll(Long userId, Long listId, LanguageCode baseLang) {
        WordList list = catalogService.requireList(listId);

        Optional<UserList> existing = userListRepository.findByUserIdAndListId(userId, listId);
        if (existing.isPresent()) {
            return new EnrollResult(existing.get(), false);
        }

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
