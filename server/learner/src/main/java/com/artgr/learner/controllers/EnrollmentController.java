package com.artgr.learner.controllers;

import com.artgr.learner.data.enums.EnrollmentStatus;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.EnrollRequest;
import com.artgr.learner.data.presentation.Enrollment;
import com.artgr.learner.data.presentation.WordProgress;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
public class EnrollmentController {

    @PostMapping("/lists/{listId}/enroll")
    public ResponseEntity<Enrollment> enroll(@PathVariable Long listId, @RequestBody EnrollRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dummyEnrollment(listId));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<List<Enrollment>> enrollments() {
        return ResponseEntity.ok(List.of(dummyEnrollment(1L)));
    }

    @GetMapping("/enrollments/{listId}")
    public ResponseEntity<Enrollment> enrollment(@PathVariable Long listId) {
        return ResponseEntity.ok(dummyEnrollment(listId));
    }

    @DeleteMapping("/enrollments/{listId}")
    public ResponseEntity<Void> unenroll(@PathVariable Long listId) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/enrollments/{listId}/progress")
    public ResponseEntity<List<WordProgress>> progress(@PathVariable Long listId) {
        return ResponseEntity.ok(List.of(
                new WordProgress(5L, 3, 3, 0, null),
                new WordProgress(3L, 1, 0, 1, null)
        ));
    }

    private Enrollment dummyEnrollment(Long listId) {
        return new Enrollment(
                listId,
                "Swedish Basics",
                LanguageCode.en,
                LanguageCode.sv,
                EnrollmentStatus.active,
                1,
                3,
                new Enrollment.SessionsSummary(1, 8),
                OffsetDateTime.parse("2026-07-26T18:40:00Z")
        );
    }
}
