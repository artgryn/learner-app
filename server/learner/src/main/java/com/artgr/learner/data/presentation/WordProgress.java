package com.artgr.learner.data.presentation;

import java.time.OffsetDateTime;

public record WordProgress(Long lexemeId, Integer timesPracticed, Integer correct, Integer wrong, OffsetDateTime due) {
}
