package com.mymitraa.taskflow.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.AuthController;
import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.UserRepository;
import com.mymitraa.taskflow.security.JwtAuthenticationFilter;
import com.mymitraa.taskflow.security.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RegistrationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest(name = "REG-SEC: Privilege escalation prevention for role {0}")
    @ValueSource(strings = {
        "Super Admin",
        "Admin",
        "Project Manager",
        "Sales Manager",
        "HR Manager",
        "QA Tester",
        "Developer",
        "ROLE_SUPER_ADMIN",
        "ROLE_ADMIN",
        "ROOT",
        "SYSTEM_EXECUTIVE",
        "CUSTOM_PRIVILEGED_ROLE"
    })
    @DisplayName("Public registration strictly enforces Employee role regardless of client role payload")
    void testPublicRegistrationStrictlyEnforcesEmployeeRole(String requestedPrivilegedRole) throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedSecurePassword987654321");
        
        // Mock save to return the saved user
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId("new-user-id-" + System.currentTimeMillis());
            return u;
        });

        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Attacker Jane");
        payload.put("email", "jane.attacker@example.com");
        payload.put("password", "SecurePassword@123");
        payload.put("role", requestedPrivilegedRole);
        payload.put("team", "Technology");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("REGISTRATION_SUCCESS")))
                // Critical assertion: returned user MUST have Employee role, NOT the requested privileged role
                .andExpect(jsonPath("$.data.role", is("Employee")))
                .andExpect(jsonPath("$.data.password").doesNotExist());

        // Verify captured entity persisted to MongoDB repository strictly has role "Employee"
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User persistedUser = userCaptor.getValue();
        assertThat("Persisted user role must always be Employee", persistedUser.getRole(), is("Employee"));
        assertThat("Password in DB must be hashed", persistedUser.getPassword(), org.hamcrest.Matchers.startsWith("$2a$"));
    }

    @Test
    @DisplayName("REG-SEC: Short password (< 6 chars) rejected with 400 Bad Request")
    void testShortPasswordRejected() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Bob User");
        payload.put("email", "bob@example.com");
        payload.put("password", "12345"); // 5 characters

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", containsString("at least 6 characters")));
    }

    @Test
    @DisplayName("REG-SEC: Duplicate email registration rejected with 400 Bad Request")
    void testDuplicateEmailRejected() throws Exception {
        when(userRepository.findByEmail("existing@mymitraa.com")).thenReturn(Optional.of(new User()));

        Map<String, String> payload = new HashMap<>();
        payload.put("name", "Existing User");
        payload.put("email", "existing@mymitraa.com");
        payload.put("password", "ValidPassword@123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.message", containsString("already exists with email")));
    }
}
