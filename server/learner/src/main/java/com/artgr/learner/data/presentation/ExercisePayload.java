package com.artgr.learner.data.presentation;

// Mirrors the `exercise` oneOf in swagger.yaml - sealed so every exerciseType
// payload shape is enumerated here.
public sealed interface ExercisePayload
        permits EnEttPayload, TranslatePayload, AssemblePayload, OptionsPayload, MultiSelectPayload {
}
