package com.artgr.learner.data.presentation;

import java.util.List;

public record EnEttPayload(List<String> options, String correctAnswer) implements ExercisePayload {
}
