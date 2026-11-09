package com.mymitraa.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.LeadController;
import com.mymitraa.taskflow.api_domain.Lead;
import com.mymitraa.taskflow.api_repository.LeadRepository;
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

@WebMvcTest(LeadController.class)
@ActiveProfiles("test")
@Import(WebSecurityConfiguration.class)
public class LeadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeadRepository leadRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "salesexec", roles = {"SALES_EXECUTIVE"})
    @DisplayName("LEAD-01: Sales Executive can list leads and filter by stage")
    void testGetLeads() throws Exception {
        Lead l1 = new Lead();
        l1.setId("lead-1");
        l1.setContactName("John Smith");
        l1.setStage("Proposal");

        when(leadRepository.findAll()).thenReturn(Arrays.asList(l1));
        when(leadRepository.findByStage("Proposal")).thenReturn(Arrays.asList(l1));

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].contactName", is("John Smith")));

        mockMvc.perform(get("/api/leads?stage=Proposal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @WithMockUser(username = "salesexec", roles = {"SALES_EXECUTIVE"})
    @DisplayName("LEAD-02: Sales Executive can create a lead with auto-generated lead code")
    void testCreateLead() throws Exception {
        Lead input = new Lead();
        input.setContactName("Diana Prince");
        input.setCompanyName("Themyscira Global");
        input.setEmail("diana@themyscira.com");

        when(leadRepository.save(any(Lead.class))).thenAnswer(i -> {
            Lead l = i.getArgument(0);
            l.setId("mongo-lead-99");
            return l;
        });

        mockMvc.perform(post("/api/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data.leadCode", org.hamcrest.Matchers.startsWith("LEAD-")))
                .andExpect(jsonPath("$.data.contactName", is("Diana Prince")));
    }

    @Test
    @WithMockUser(username = "salesmgr", roles = {"SALES_MANAGER"})
    @DisplayName("LEAD-03: Sales Manager can update an existing lead")
    void testUpdateLead() throws Exception {
        Lead existing = new Lead();
        existing.setId("lead-update-1");
        existing.setContactName("Bruce Wayne");
        existing.setStage("Negotiation");

        when(leadRepository.findById("lead-update-1")).thenReturn(Optional.of(existing));
        when(leadRepository.save(any(Lead.class))).thenAnswer(i -> i.getArgument(0));

        Lead updates = new Lead();
        updates.setStage("Closed Won");

        mockMvc.perform(put("/api/leads/lead-update-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode", is(200)))
                .andExpect(jsonPath("$.data.stage", is("Closed Won")));
    }
}
