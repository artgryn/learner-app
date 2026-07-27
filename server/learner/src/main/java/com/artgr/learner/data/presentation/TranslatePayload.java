package com.artgr.learner.data.presentation;

import com.artgr.learner.data.enums.LanguageCode;

import java.util.List;

public record TranslatePayload(LanguageCode optionsLang, List<String> options, String correctAnswer) implements ExercisePayload {
}
