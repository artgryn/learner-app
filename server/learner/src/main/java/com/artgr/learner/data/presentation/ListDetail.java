package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.ExerciseType;
import com.artgr.learner.data.enums.LanguageCode;

import java.util.List;

public record ListDetail(
        Long listId,
        String name,
        LanguageCode targetLang,
        Integer totalWords,
        List<ExerciseType> allowedExercises
) {
}
