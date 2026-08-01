package com.artgr.learner.data.presentation;

import java.util.List;

// user is the same AccountView shape as GET /me (doc/api/swagger.yaml) -
// additive vs. the old {id, email}-only shape.
public record HomeResponse(AccountView user, List<Enrollment> enrollments, Resume resume) {

    public record Resume(Long listId) {
    }
}
