package com.mymitraa.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.ProjectController;
import com.mymitraa.taskflow.api_domain.Project;
import com.mymitraa.taskflow.api_repository.ProjectRepository;
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

@WebMvcTest(ProjectController.class)
@ActiveProfiles("test")
@Import(WebSecurityConfiguration.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "pm", roles = {"PROJECT_MANAGER"})
    @DisplayName("PRJ-01: Project Manager can retrieve all projects and filter by status")
    void testGetAllProjects() throws Exception {
        Project p1 = new Project();
        p1.setId("p-1");
        p1.setProjectName("Core Banking Migration");
        p1.setStatus("In Progress");

        when(projectRepository.findAll()).thenReturn(Arrays.asList(p1));
        when(projectRepository.findByStatus("In Progress")).thenReturn(Arrays.asList(p1));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].projectName", is("Core Banking Migration")));

        mockMvc.perform(get("/api/projects?status=In Progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "pm", roles = {"PROJECT_MANAGER"})
    @DisplayName("PRJ-02: Project Manager can create project with auto-generated project ID if missing")
    void testCreateProject() throws Exception {
        Project input = new Project();
        input.setProjectName("ERP Portal");
        input.setCustomerName("Acme Corp");

        when(projectRepository.save(any(Project.class))).thenAnswer(i -> {
            Project p = i.getArgument(0);
            p.setId("mongo-p-1");
            return p;
        });

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data.projectName", is("ERP Portal")))
                .andExpect(jsonPath("$.data.projectId", org.hamcrest.Matchers.startsWith("PRJ-")));
    }

    @Test
    @WithMockUser(username = "pm", roles = {"PROJECT_MANAGER"})
    @DisplayName("PRJ-03: Project Manager cannot delete projects (HTTP 403 Forbidden)")
    void testProjectManagerCannotDeleteProject() throws Exception {
        mockMvc.perform(delete("/api/projects/p-1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PRJ-04: Admin can delete existing project")
    void testAdminCanDeleteProject() throws Exception {
        Project p = new Project();
        p.setId("p-to-delete");
        when(projectRepository.findById("p-to-delete")).thenReturn(Optional.of(p));

        mockMvc.perform(delete("/api/projects/p-to-delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data", is(true)));

        verify(projectRepository).deleteById("p-to-delete");
    }
}
