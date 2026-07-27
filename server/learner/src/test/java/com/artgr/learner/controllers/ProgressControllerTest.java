package com.artgr.learner.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeReturnsSeededDemoUserWithComputedProgress() throws Exception {
        mockMvc.perform(get("/me/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("demo@example.com"))
                .andExpect(jsonPath("$.enrollments.length()").value(1))
                .andExpect(jsonPath("$.enrollments[0].listId").value(1))
                .andExpect(jsonPath("$.enrollments[0].baseLang").value("en"))
                .andExpect(jsonPath("$.enrollments[0].targetLang").value("sv"))
                .andExpect(jsonPath("$.enrollments[0].status").value("active"))
                .andExpect(jsonPath("$.enrollments[0].totalWords").value(3))
                .andExpect(jsonPath("$.enrollments[0].wordsMastered").value(1))
                .andExpect(jsonPath("$.enrollments[0].sessions.done").value(1))
                .andExpect(jsonPath("$.resume.listId").value(1));
    }

    @Test
    void statsCountsDistinctMasteredWordsAndTodaysStreak() throws Exception {
        mockMvc.perform(get("/me/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wordsKnown").value(1))
                .andExpect(jsonPath("$.streakDays").value(1));
    }
}
