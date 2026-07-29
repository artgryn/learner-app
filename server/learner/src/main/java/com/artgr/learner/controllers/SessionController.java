package com.artgr.learner.controllers;

import com.artgr.learner.data.presentation.CompleteResponse;
import com.artgr.learner.data.presentation.ResultsBatch;
import com.artgr.learner.data.presentation.SessionResponse;
import com.artgr.learner.service.CurrentUserProvider;
import com.artgr.learner.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionController {

    private final SessionService sessionService;
    private final CurrentUserProvider currentUserProvider;

    public SessionController(SessionService sessionService, CurrentUserProvider currentUserProvider) {
        this.sessionService = sessionService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping("/enrollments/{listId}/sessions")
    public ResponseEntity<SessionResponse> createSession(@PathVariable Long listId) {
        Long userId = currentUserProvider.currentUserId();
        return ResponseEntity.ok(sessionService.deriveSession(userId, listId));
    }

    @PostMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<CompleteResponse> complete(@PathVariable String sessionId, @RequestBody ResultsBatch results) {
        return ResponseEntity.ok(sessionService.completeSession(sessionId, results));
    }

    // Out of scope for now: attempts reference a `sessions` row that doesn't
    // exist until /complete (see SessionService.completeSession), so there's
    // nowhere to attach an attempt.session_id for a mid-session incremental
    // sync yet. Revisit once that's designed.
    @PostMapping("/sessions/{sessionId}/answers")
    public ResponseEntity<Void> answers(@PathVariable String sessionId, @RequestBody ResultsBatch results) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
