package com.artgr.learner.data.presentation;

import java.util.List;

public record MultiSelectPayload(List<String> options, List<String> correctAnswers) implements ExercisePayload {
}
