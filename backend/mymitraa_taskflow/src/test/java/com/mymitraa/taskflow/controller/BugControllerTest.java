package com.mymitraa.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.BugController;
import com.mymitraa.taskflow.api_domain.Bug;
import com.mymitraa.taskflow.api_repository.BugRepository;
import com.mymitraa.taskflow.security.WebSecurityConfiguration;
import org.springframework.context.annotation.Import;
import com.mymitraa.taskflow.security.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BugController.class)
@ActiveProfiles("test")
@Import(WebSecurityConfiguration.class)
public class BugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BugRepository bugRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;


    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "tester", roles = {"QA_TESTER"})
    @DisplayName("BUG-01: QA Tester can retrieve bugs and filter by severity")
    void testGetBugs() throws Exception {
        Bug b1 = new Bug();
        b1.setId("bug-1");
        b1.setTitle("Login button unclickable on mobile viewport");
        b1.setSeverity("High");
        b1.setStatus("Open");

        when(bugRepository.findAll()).thenReturn(Arrays.asList(b1));
        when(bugRepository.findBySeverity("High")).thenReturn(Arrays.asList(b1));

        mockMvc.perform(get("/api/bugs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", containsString("Login button")));

        mockMvc.perform(get("/api/bugs?severity=High"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "tester", roles = {"QA_TESTER"})
    @DisplayName("BUG-02: QA Tester can report a new bug with timestamp and code")
    void testCreateBug() throws Exception {
        Bug input = new Bug();
        input.setTitle("Null pointer on PDF document export");
        input.setProject("Enterprise TaskFlow");
        input.setSeverity("Critical");

        when(bugRepository.save(any(Bug.class))).thenAnswer(i -> {
            Bug b = i.getArgument(0);
            b.setId("mongo-bug-123");
            return b;
        });

        mockMvc.perform(post("/api/bugs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data.bugCode", org.hamcrest.Matchers.startsWith("BUG-")))
                .andExpect(jsonPath("$.data.createdAt", notNullValue()));
    }

    @Test
    @WithMockUser(username = "developer", roles = {"DEVELOPER"})
    @DisplayName("BUG-03: Developer can update bug status to In Progress or Resolved")
    void testDeveloperUpdateBugStatus() throws Exception {
        Bug existing = new Bug();
        existing.setId("bug-dev-1");
        existing.setTitle("Performance bottleneck in query");
        existing.setStatus("Open");

        when(bugRepository.findById("bug-dev-1")).thenReturn(Optional.of(existing));
        when(bugRepository.save(any(Bug.class))).thenAnswer(i -> i.getArgument(0));

        Bug updates = new Bug();
        updates.setStatus("In Progress");

        mockMvc.perform(put("/api/bugs/bug-dev-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data.status", is("In Progress")));
    }
}
