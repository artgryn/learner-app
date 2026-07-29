package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.ExerciseType;

// itemId references an ExerciseItem's itemId - IntroduceItem is never
// submitted here (nothing to grade).
public record Result(String itemId, Long lexemeId, ExerciseType exerciseType, String formType, boolean isCorrect, Integer elapsedMs) {
}
