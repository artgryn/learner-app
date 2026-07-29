package com.artgr.learner.controllers;

import com.artgr.learner.data.entity.ListProgress;
import com.artgr.learner.data.entity.UserList;
import com.artgr.learner.data.mappers.EnrollmentMapper;
import com.artgr.learner.data.presentation.EnrollRequest;
import com.artgr.learner.data.presentation.Enrollment;
import com.artgr.learner.data.presentation.WordProgress;
import com.artgr.learner.service.CurrentUserProvider;
import com.artgr.learner.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CurrentUserProvider currentUserProvider;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentController(EnrollmentService enrollmentService, CurrentUserProvider currentUserProvider, EnrollmentMapper enrollmentMapper) {
        this.enrollmentService = enrollmentService;
        this.currentUserProvider = currentUserProvider;
        this.enrollmentMapper = enrollmentMapper;
    }

    @PostMapping("/lists/{listId}/enroll")
    public ResponseEntity<Enrollment> enroll(@PathVariable Long listId, @RequestBody EnrollRequest request) {
        Long userId = currentUserProvider.currentUserId();
        EnrollmentService.EnrollResult result = enrollmentService.enroll(userId, listId, request.baseLang());
        HttpStatus status = result.created() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(enrollmentMapper.toDto(result.enrollment()));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<List<Enrollment>> enrollments() {
        Long userId = currentUserProvider.currentUserId();
        List<Enrollment> body = enrollmentService.enrollments(userId).stream()
                .map(enrollmentMapper::toDto)
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/enrollments/{listId}")
    public ResponseEntity<Enrollment> enrollment(@PathVariable Long listId) {
        Long userId = currentUserProvider.currentUserId();
        UserList enrollment = enrollmentService.requireEnrollment(userId, listId);
        return ResponseEntity.ok(enrollmentMapper.toDto(enrollment));
    }

    @DeleteMapping("/enrollments/{listId}")
    public ResponseEntity<Void> unenroll(@PathVariable Long listId) {
        Long userId = currentUserProvider.currentUserId();
        enrollmentService.unenroll(userId, listId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/enrollments/{listId}/progress")
    public ResponseEntity<List<WordProgress>> progress(@PathVariable Long listId) {
        Long userId = currentUserProvider.currentUserId();
        List<WordProgress> body = enrollmentService.progress(userId, listId).stream()
                .map(this::toWordProgress)
                .toList();
        return ResponseEntity.ok(body);
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
}
