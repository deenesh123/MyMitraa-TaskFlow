package com.mymitraa.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.AuditLogController;
import com.mymitraa.taskflow.api_domain.AuditLog;
import com.mymitraa.taskflow.api_repository.AuditLogRepository;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditLogController.class)
@ActiveProfiles("test")
@Import(WebSecurityConfiguration.class)
public class AuditLogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogRepository auditLogRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;


    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("AUDIT-01: Admin can retrieve audit logs with filters")
    void testGetAuditLogs() throws Exception {
        AuditLog log1 = new AuditLog();
        log1.setId("audit-1");
        log1.setAction("USER_ROLE_UPDATED");
        log1.setActorName("Admin User");
        log1.setModule("User Management");
        log1.setOutcome("SUCCESS");

        when(auditLogRepository.findAll()).thenReturn(Arrays.asList(log1));
        when(auditLogRepository.findByModule("User Management")).thenReturn(Arrays.asList(log1));

        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].action", is("USER_ROLE_UPDATED")));

        mockMvc.perform(get("/api/audit-logs?module=User Management"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "employee", roles = {"EMPLOYEE"})
    @DisplayName("AUDIT-02: Regular employee is forbidden from viewing audit logs (HTTP 403)")
    void testEmployeeForbiddenFromAuditLogs() throws Exception {
        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "system_actor")
    @DisplayName("AUDIT-03: System or user event can record audit entry with timestamp")
    void testRecordAuditLog() throws Exception {
        AuditLog entry = new AuditLog();
        entry.setAction("PROJECT_ARCHIVED");
        entry.setActorName("Lead PM");
        entry.setModule("Projects");
        entry.setOutcome("SUCCESS");

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(i -> {
            AuditLog l = i.getArgument(0);
            l.setId("mongo-audit-55");
            return l;
        });

        mockMvc.perform(post("/api/audit-logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data.timestamp", notNullValue()))
                .andExpect(jsonPath("$.data.action", is("PROJECT_ARCHIVED")));
    }
}
