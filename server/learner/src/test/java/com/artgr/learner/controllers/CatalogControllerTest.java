package com.artgr.learner.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void languagesReturnsSeededLanguages() throws Exception {
        mockMvc.perform(get("/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.code=='sv')]").exists())
                .andExpect(jsonPath("$[?(@.code=='en')]").exists());
    }

    @Test
    void listsAreFilteredByTranslationCoverage() throws Exception {
        mockMvc.perform(get("/lists").param("target", "sv").param("base", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/lists").param("target", "sv").param("base", "ru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listsRequiresBothParams() throws Exception {
        mockMvc.perform(get("/lists").param("target", "sv"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
    }

    @Test
    void listDetailReturns404ForUnknownList() throws Exception {
        mockMvc.perform(get("/lists/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void listDetailReturnsAllowedExercisesForThemedList() throws Exception {
        mockMvc.perform(get("/lists/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWords").value(1))
                .andExpect(jsonPath("$.allowedExercises.length()").value(2));
    }

    @Test
    void listDetailReturnsNullAllowedExercisesForUnrestrictedList() throws Exception {
        mockMvc.perform(get("/lists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWords").value(3))
                .andExpect(jsonPath("$.allowedExercises").value(nullValue()));
    }
}
