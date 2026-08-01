package com.artgr.learner.controllers;

import com.artgr.learner.data.entity.Account;
import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.mappers.AccountMapper;
import com.artgr.learner.data.mappers.EnrollmentMapper;
import com.artgr.learner.data.presentation.Enrollment;
import com.artgr.learner.data.presentation.HomeResponse;
import com.artgr.learner.data.presentation.Stats;
import com.artgr.learner.service.CurrentUserProvider;
import com.artgr.learner.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/me")
public class ProgressController {

    private final ProgressService progressService;
    private final CurrentUserProvider currentUserProvider;
    private final EnrollmentMapper enrollmentMapper;
    private final AccountMapper accountMapper;

    public ProgressController(ProgressService progressService, CurrentUserProvider currentUserProvider, EnrollmentMapper enrollmentMapper, AccountMapper accountMapper) {
        this.progressService = progressService;
        this.currentUserProvider = currentUserProvider;
        this.enrollmentMapper = enrollmentMapper;
        this.accountMapper = accountMapper;
    }

    @GetMapping("/home")
    public ResponseEntity<HomeResponse> home() {
        Long userId = currentUserProvider.currentUserId();
        Account account = progressService.requireAccount(userId);
        List<UserList> enrollments = progressService.enrollments(userId);
        List<Enrollment> enrollmentDtos = enrollments.stream().map(enrollmentMapper::toDto).toList();
        Long resumeListId = progressService.resumeListId(enrollments);

        return ResponseEntity.ok(new HomeResponse(
                accountMapper.toDto(account),
                enrollmentDtos,
                resumeListId == null ? null : new HomeResponse.Resume(resumeListId)
        ));
    }

    @GetMapping("/stats")
    public ResponseEntity<Stats> stats() {
        Long userId = currentUserProvider.currentUserId();
        return ResponseEntity.ok(new Stats(
                progressService.wordsKnown(userId),
                progressService.streakDays(userId)
        ));
    }
}
