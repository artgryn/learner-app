package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.ExerciseType;

public record Result(String exerciseId, Long lexemeId, ExerciseType exerciseType, String formType, boolean isCorrect, Integer elapsedMs) {
}
