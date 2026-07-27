package com.artgr.learner.data.presentation;

import java.util.List;

public record AssemblePayload(List<String> letters, Integer answerLength, String correctAnswer) implements ExercisePayload {
}
