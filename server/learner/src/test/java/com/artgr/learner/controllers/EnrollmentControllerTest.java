package com.artgr.learner.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getEnrollmentsReturnsSeededEnrollment() throws Exception {
        mockMvc.perform(get("/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].listId").value(1))
                .andExpect(jsonPath("$[0].wordsMastered").value(1))
                .andExpect(jsonPath("$[0].totalWords").value(3));
    }

    @Test
    void getEnrollmentReturns404ForUnknownList() throws Exception {
        mockMvc.perform(get("/enrollments/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void enrollIsIdempotentForAlreadyEnrolledList() throws Exception {
        mockMvc.perform(post("/lists/1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"baseLang\":\"en\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listId").value(1));
    }

    @Test
    void enrollCreatesNewEnrollmentForUnenrolledList() throws Exception {
        mockMvc.perform(post("/lists/2/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"baseLang\":\"en\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listId").value(2))
                .andExpect(jsonPath("$.totalWords").value(1));

        mockMvc.perform(get("/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void enrollReturns404ForUnknownList() throws Exception {
        mockMvc.perform(post("/lists/9999/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"baseLang\":\"en\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unenrollDeletesEnrollmentThenNotFound() throws Exception {
        mockMvc.perform(delete("/enrollments/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/enrollments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unenrollReturns404WhenNotEnrolled() throws Exception {
        mockMvc.perform(delete("/enrollments/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void progressReturnsSeededListProgress() throws Exception {
        mockMvc.perform(get("/enrollments/1/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
