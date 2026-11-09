package com.mymitraa.taskflow.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.*;
import com.mymitraa.taskflow.api_domain.Project;
import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.*;
import com.mymitraa.taskflow.security.JwtAuthenticationFilter;
import com.mymitraa.taskflow.security.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, RoleController.class, ProjectController.class, LeadController.class, BugController.class, AuditLogController.class})
public class RoleSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ProjectRepository projectRepository;

    @MockBean
    private LeadRepository leadRepository;

    @MockBean
    private BugRepository bugRepository;

    @MockBean
    private AuditLogRepository auditLogRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================
    // 1. Super Admin
    // ==========================================
    @Test
    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @DisplayName("RBAC-01: Super Admin can access all administrative and privileged endpoints")
    void testSuperAdminPrivilegedAccess() throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(auditLogRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // 2. Admin
    // ==========================================
    @Test
    @WithMockUser(username = "adminuser", roles = {"ADMIN"})
    @DisplayName("RBAC-02: Admin can access user management but is restricted from modifying Super Admin accounts")
    void testAdminAccessAndBoundaries() throws Exception {
        User superAdminUser = new User();
        superAdminUser.setId("superadmin-1");
        superAdminUser.setRole("Super Admin");

        when(userRepository.findById("superadmin-1")).thenReturn(Optional.of(superAdminUser));

        // Admin attempting to delete Super Admin account -> HTTP 403 Forbidden
        mockMvc.perform(delete("/api/users/delete/superadmin-1"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 3. Project Manager
    // ==========================================
    @Test
    @WithMockUser(username = "pmuser", roles = {"PROJECT_MANAGER"})
    @DisplayName("RBAC-03: Project Manager can manage projects but is forbidden from user management and audit logs")
    void testProjectManagerPermissions() throws Exception {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 4. Developer
    // ==========================================
    @Test
    @WithMockUser(username = "devuser", roles = {"DEVELOPER"})
    @DisplayName("RBAC-04: Developer can view projects and bugs but cannot create projects or view audit logs")
    void testDeveloperPermissions() throws Exception {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());
        when(bugRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/bugs"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Project())))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 5. Sales Executive
    // ==========================================
    @Test
    @WithMockUser(username = "salesexec", roles = {"SALES_EXECUTIVE"})
    @DisplayName("RBAC-05: Sales Executive can view/create leads but cannot delete leads")
    void testSalesExecutivePermissions() throws Exception {
        when(leadRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isOk());

        // Deleting leads requires Super Admin, Admin, or Sales Manager
        mockMvc.perform(delete("/api/leads/lead-123"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 6. Sales Manager
    // ==========================================
    @Test
    @WithMockUser(username = "salesmgr", roles = {"SALES_MANAGER"})
    @DisplayName("RBAC-06: Sales Manager can manage and delete leads but cannot access user management")
    void testSalesManagerPermissions() throws Exception {
        when(leadRepository.existsById("lead-123")).thenReturn(true);

        mockMvc.perform(delete("/api/leads/lead-123"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 7. QA Tester
    // ==========================================
    @Test
    @WithMockUser(username = "qatester", roles = {"QA_TESTER"})
    @DisplayName("RBAC-07: QA Tester can view/create/delete bugs but cannot access leads or user administration")
    void testQaTesterPermissions() throws Exception {
        when(bugRepository.findAll()).thenReturn(Collections.emptyList());
        when(bugRepository.existsById("bug-123")).thenReturn(true);

        mockMvc.perform(get("/api/bugs"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/bugs/bug-123"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 8. HR Manager
    // ==========================================
    @Test
    @WithMockUser(username = "hrmanager", roles = {"HR_MANAGER"})
    @DisplayName("RBAC-08: HR Manager can view employee users but cannot access role permission modification")
    void testHrManagerPermissions() throws Exception {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 9. Employee
    // ==========================================
    @Test
    @WithMockUser(username = "employee1", roles = {"EMPLOYEE"})
    @DisplayName("RBAC-09: Employee cannot access administrative user, role, or audit log endpoints")
    void testEmployeeAccessRestrictions() throws Exception {
        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/audit-logs"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // 10. Client
    // ==========================================
    @Test
    @WithMockUser(username = "clientacct", roles = {"CLIENT"})
    @DisplayName("RBAC-10: Client can view assigned projects but is restricted from internal modules")
    void testClientAccessRestrictions() throws Exception {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/leads"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/bugs"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // Unauthenticated access
    // ==========================================
    @Test
    @DisplayName("RBAC-11: Unauthenticated request to protected endpoint is rejected (HTTP 401)")
    void testUnauthenticatedAccessRejected() throws Exception {
        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isUnauthorized());
    }
}
