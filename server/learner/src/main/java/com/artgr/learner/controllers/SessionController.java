package com.artgr.learner.controllers;

import com.artgr.learner.data.enums.ExerciseType;
import com.artgr.learner.data.enums.LanguageCode;
import com.artgr.learner.data.presentation.AssemblePayload;
import com.artgr.learner.data.presentation.CompleteResponse;
import com.artgr.learner.data.presentation.EnEttPayload;
import com.artgr.learner.data.presentation.Exercise;
import com.artgr.learner.data.presentation.MultiSelectPayload;
import com.artgr.learner.data.presentation.OptionsPayload;
import com.artgr.learner.data.presentation.Prompt;
import com.artgr.learner.data.presentation.ResultsBatch;
import com.artgr.learner.data.presentation.SessionResponse;
import com.artgr.learner.data.presentation.TranslatePayload;
import com.artgr.learner.data.presentation.WordProgress;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SessionController {

    @PostMapping("/enrollments/{listId}/sessions")
    public ResponseEntity<SessionResponse> createSession(@PathVariable Long listId) {
        return ResponseEntity.ok(new SessionResponse("a3f2c9d1", listId, List.of(
                new Exercise("ex_01", ExerciseType.en_ett, 1L, null,
                        new Prompt("hus", LanguageCode.sv),
                        new EnEttPayload(List.of("en", "ett"), "ett")),
                new Exercise("ex_02", ExerciseType.translate, 1L, null,
                        new Prompt("hus", LanguageCode.sv),
                        new TranslatePayload(LanguageCode.en, List.of("house", "car", "tree", "dog"), "house")),
                new Exercise("ex_03", ExerciseType.assemble, 3L, "preteritum",
                        new Prompt("to go", LanguageCode.en),
                        new AssemblePayload(List.of("k", "a", "c", "g", "i", "k", "n"), 4, "gick")),
                new Exercise("ex_04", ExerciseType.base_form, 3L, "preteritum",
                        new Prompt("gick", LanguageCode.sv),
                        new OptionsPayload(List.of("gå", "gick", "stå", "få"), "gå")),
                new Exercise("ex_05", ExerciseType.produce_form, 3L, "preteritum",
                        new Prompt("gå", LanguageCode.sv),
                        new OptionsPayload(List.of("gick", "gången", "gått", "går"), "gick")),
                new Exercise("ex_06", ExerciseType.multi_select, 3L, null,
                        new Prompt("gå", LanguageCode.sv),
                        new MultiSelectPayload(List.of("går", "gick", "gått", "house", "springer"), List.of("går", "gick", "gått")))
        )));
    }

    @PostMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<CompleteResponse> complete(@PathVariable String sessionId, @RequestBody ResultsBatch results) {
        return ResponseEntity.ok(new CompleteResponse(sessionId, List.of(
                new WordProgress(5L, 4, 4, 0, null),
                new WordProgress(3L, 2, 1, 1, null)
        )));
    }

    @PostMapping("/sessions/{sessionId}/answers")
    public ResponseEntity<List<WordProgress>> answers(@PathVariable String sessionId, @RequestBody ResultsBatch results) {
        return ResponseEntity.ok(List.of(
                new WordProgress(5L, 4, 4, 0, null)
        ));
    }
}
