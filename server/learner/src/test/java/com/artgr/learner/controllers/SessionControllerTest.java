package com.artgr.learner.controllers;

import com.artgr.learner.data.presentation.CompleteResponse;
import com.artgr.learner.data.presentation.ExerciseItem;
import com.artgr.learner.data.presentation.Result;
import com.artgr.learner.data.presentation.ResultsBatch;
import com.artgr.learner.data.presentation.SessionItem;
import com.artgr.learner.data.presentation.SessionResponse;
import com.artgr.learner.data.repository.LearningSessionRepository;
import com.artgr.learner.exceptions.NotFoundException;
import com.artgr.learner.service.SessionService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// The ExerciseItem/ExercisePayload DTOs are serialize-only (sealed interface,
// no deserialization support), so MockMvc responses are read as JsonNode.
// The /complete round-trip test calls SessionService directly, where no
// (de)serialization is involved.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private LearningSessionRepository learningSessionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deriveSessionReturns404ForUnknownEnrollment() throws Exception {
        mockMvc.perform(post("/enrollments/9999/sessions"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deriveSessionGivesExactlyOneCardForTheNewWordBeforeItsFirstExercise() throws Exception {
        String body = mockMvc.perform(post("/enrollments/1/sessions"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode items = objectMapper.readTree(body).get("items");

        int introCount = 0;
        int introIndex = -1;
        int firstHusExerciseIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            JsonNode item = items.get(i);
            boolean isIntro = "introduce".equals(item.get("itemType").asText());
            long lexemeId = item.get("lexemeId").asLong();
            if (isIntro) {
                introCount++;
                assertEquals(1L, lexemeId, "only hus (never started) should get a card");
                introIndex = i;
            } else if (lexemeId == 1L && firstHusExerciseIndex < 0) {
                firstHusExerciseIndex = i;
            }
        }

        assertEquals(1, introCount, "exactly one card - jag and gå are already started (review), hus is new");
        assertTrue(introIndex >= 0 && firstHusExerciseIndex > introIndex, "card must precede hus's first exercise");
    }

    @Test
    void deriveSessionNeverPlacesTheSameWordsExercisesAdjacent() throws Exception {
        String body = mockMvc.perform(post("/enrollments/1/sessions"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode items = objectMapper.readTree(body).get("items");

        Long previousExerciseLexemeId = null;
        for (JsonNode item : items) {
            if (!"exercise".equals(item.get("itemType").asText())) {
                continue;
            }
            long lexemeId = item.get("lexemeId").asLong();
            if (previousExerciseLexemeId != null) {
                assertFalse(previousExerciseLexemeId == lexemeId, "two exercises for the same word must never be adjacent");
            }
            previousExerciseLexemeId = lexemeId;
        }
    }

    @Test
    void deriveSessionRespectsListAllowedExercises() throws Exception {
        mockMvc.perform(post("/lists/2/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"baseLang\":\"en\"}"))
                .andExpect(status().isCreated());

        String body = mockMvc.perform(post("/enrollments/2/sessions"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode items = objectMapper.readTree(body).get("items");
        Set<String> allowed = Set.of("en_ett", "translate");
        boolean sawIntro = false;
        for (JsonNode item : items) {
            if ("introduce".equals(item.get("itemType").asText())) {
                sawIntro = true;
                continue;
            }
            assertTrue(allowed.contains(item.get("exerciseType").asText()),
                    "list 2 only allows en_ett/translate");
        }
        assertTrue(sawIntro, "hus is new to list 2 as well");
    }

    @Test
    void completeSessionWritesOneSessionsRowAndUpsertsProgressForEveryTouchedWord() {
        long sessionsBefore = learningSessionRepository.countByUserIdAndListId(1L, 1L);

        SessionResponse session = sessionService.deriveSession(1L, 1L);
        List<ExerciseItem> exercises = onlyExercises(session);
        assertFalse(exercises.isEmpty());

        List<Result> results = exercises.stream()
                .map(e -> new Result(e.itemId(), e.lexemeId(), e.exerciseType(), e.formType(), true, 1500))
                .toList();

        CompleteResponse response = sessionService.completeSession(session.sessionId(), new ResultsBatch(results));

        assertEquals(session.sessionId(), response.sessionId());
        Set<Long> touchedLexemeIds = new HashSet<>();
        exercises.forEach(e -> touchedLexemeIds.add(e.lexemeId()));
        assertEquals(touchedLexemeIds.size(), response.progress().size());
        response.progress().forEach(p -> assertTrue(p.correct() > 0));

        assertEquals(sessionsBefore + 1, learningSessionRepository.countByUserIdAndListId(1L, 1L));
    }

    @Test
    void completeSessionThrowsForUnknownSession() {
        assertThrows(NotFoundException.class,
                () -> sessionService.completeSession("unknown-session-id", new ResultsBatch(List.of())));
    }

    @Test
    void answersEndpointIsNotImplementedYet() throws Exception {
        mockMvc.perform(post("/sessions/whatever/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"results\":[]}"))
                .andExpect(status().isNotImplemented());
    }

    private List<ExerciseItem> onlyExercises(SessionResponse session) {
        List<ExerciseItem> exercises = new ArrayList<>();
        for (SessionItem item : session.items()) {
            if (item instanceof ExerciseItem exercise) {
                exercises.add(exercise);
            }
        }
        return exercises;
    }
}
