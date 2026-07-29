package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.ExerciseType;

public record ExerciseItem(
        String itemId,
        String itemType,
        ExerciseType exerciseType,
        Long lexemeId,
        String formType,
        Prompt prompt,
        ExercisePayload exercise
) implements SessionItem {

    public ExerciseItem(String itemId, ExerciseType exerciseType, Long lexemeId, String formType, Prompt prompt, ExercisePayload exercise) {
        this(itemId, "exercise", exerciseType, lexemeId, formType, prompt, exercise);
    }
}
