package com.artgr.learner.data.presentation;

import java.util.List;

public record SessionResponse(String sessionId, Long listId, List<SessionItem> items) {
}
