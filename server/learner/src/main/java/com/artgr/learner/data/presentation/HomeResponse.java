package com.artgr.learner.data.presentation;

import java.util.List;

public record HomeResponse(User user, List<Enrollment> enrollments, Resume resume) {

    public record User(Long id, String email) {
    }

    public record Resume(Long listId) {
    }
}
