package com.artgr.learner.data.presentation;

import java.util.List;

// Single-answer choice payload (base_form, produce_form).
public record OptionsPayload(List<String> options, String correctAnswer) implements ExercisePayload {
}
