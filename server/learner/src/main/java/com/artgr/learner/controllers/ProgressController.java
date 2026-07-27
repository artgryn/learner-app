package com.artgr.learner.controllers;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.enums.EnrollmentStatus;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.Enrollment;
import com.artgr.learner.data.presentation.HomeResponse;
import com.artgr.learner.data.presentation.Stats;
import com.artgr.learner.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
public class ProgressController {

    // Placeholder until JWT auth lands (open/unauthenticated APIs for now,
    // per earlier decision) - "current user" is hardcoded to the seeded
    // demo account instead of derived from a bearer token subject.
    private static final Long CURRENT_USER_ID = 1L;

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/home")
    public ResponseEntity<HomeResponse> home() {
        Account account = progressService.requireAccount(CURRENT_USER_ID);
        List<UserList> enrollments = progressService.enrollments(CURRENT_USER_ID);
        List<Enrollment> enrollmentDtos = enrollments.stream().map(this::toEnrollment).toList();
        Long resumeListId = progressService.resumeListId(enrollments);

        return ResponseEntity.ok(new HomeResponse(
                new HomeResponse.User(account.getId(), account.getEmail()),
                enrollmentDtos,
                resumeListId == null ? null : new HomeResponse.Resume(resumeListId)
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Stats> stats() {
        return ResponseEntity.ok(new Stats(
                progressService.wordsKnown(CURRENT_USER_ID),
                progressService.streakDays(CURRENT_USER_ID)
        ));
    }

    private Enrollment toEnrollment(UserList enrollment) {
        Long listId = enrollment.getId().getListId();
        return new Enrollment(
                listId,
                enrollment.getList().getName(),
                LanguageCode.valueOf(enrollment.getBaseLang().getCode()),
                LanguageCode.valueOf(enrollment.getList().getTargetLang().getCode()),
                EnrollmentStatus.valueOf(enrollment.getStatus()),
                progressService.wordsMastered(CURRENT_USER_ID, listId),
                progressService.totalWords(listId),
                new Enrollment.SessionsSummary(
                        progressService.sessionsDone(CURRENT_USER_ID, listId),
                        progressService.estimatedTotalSessions(listId)
                ),
                progressService.lastActiveAt(enrollment)
        );
    }
}
