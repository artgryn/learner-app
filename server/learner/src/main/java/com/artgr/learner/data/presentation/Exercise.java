package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.ExerciseType;

public record Exercise(
        String exerciseId,
        ExerciseType exerciseType,
        Long lexemeId,
        String formType,
        Prompt prompt,
        ExercisePayload exercise
) {
}
