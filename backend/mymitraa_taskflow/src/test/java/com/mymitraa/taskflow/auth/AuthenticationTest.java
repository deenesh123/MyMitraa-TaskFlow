package com.mymitraa.taskflow.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.AuthController;
import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.UserRepository;
import com.mymitraa.taskflow.security.JwtAuthenticationFilter;
import com.mymitraa.taskflow.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.mockito.Mockito;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class AuthenticationTest {

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
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-auth-123");
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@mymitraa.com");
        testUser.setName("Test User");
        testUser.setRole("Employee");
        testUser.setStatus("Active");
        testUser.setEnabled(true);
        testUser.setPassword("$2a$10$hashedPasswordValueForTesting1234567890");
    }

    @Test
    @DisplayName("AUTH-01: Successful login with valid email and password")
    void testSuccessfulLogin() throws Exception {
        when(userRepository.findByEmail("testuser@mymitraa.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password@123", testUser.getPassword())).thenReturn(true);
        when(jwtTokenUtil.generateToken("testuser")).thenReturn("mocked-jwt-token-string");

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "testuser@mymitraa.com");
        payload.put("password", "Password@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("LOGIN_SUCCESS")))
                .andExpect(jsonPath("$.data.email", is("testuser@mymitraa.com")))
                .andExpect(jsonPath("$.data.password").doesNotExist()) // Never expose password hash
                .andExpect(jsonPath("$.metaInfo.token", notNullValue()));
    }

    @Test
    @DisplayName("AUTH-02: Login failure with incorrect password returns 401")
    void testLoginWithIncorrectPassword() throws Exception {
        when(userRepository.findByEmail("testuser@mymitraa.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongPassword", testUser.getPassword())).thenReturn(false);

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "testuser@mymitraa.com");
        payload.put("password", "WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", containsString("Invalid username/email or password")));
    }

    @Test
    @DisplayName("AUTH-03: Login failure with nonexistent email returns 401")
    void testLoginWithNonexistentEmail() throws Exception {
        when(userRepository.findByEmail("nonexistent@mymitraa.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("nonexistent@mymitraa.com")).thenReturn(null);

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "nonexistent@mymitraa.com");
        payload.put("password", "Password@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", containsString("Invalid username/email or password")));
    }

    @Test
    @DisplayName("AUTH-04: Login with empty email or empty password returns 401")
    void testLoginWithEmptyCredentials() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", "");
        payload.put("password", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", is(401)))
                .andExpect(jsonPath("$.message", containsString("Both identifier (email or username) and password are required for login")));
    }

    @Test
    @DisplayName("AUTH-05: Deactivated/disabled user account blocked from login (HTTP 403)")
    void testDeactivatedAccountLoginBlocked() throws Exception {
        testUser.setStatus("Inactive");
        testUser.setEnabled(false);
        when(userRepository.findByEmail("testuser@mymitraa.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password@123", testUser.getPassword())).thenReturn(true);

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "testuser@mymitraa.com");
        payload.put("password", "Password@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code", is(403)))
                .andExpect(jsonPath("$.message", containsString("deactivated or disabled")));
    }

    @Test
    @DisplayName("AUTH-06: Logout successfully clears session context")
    void testLogoutSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("LOGGED_OUT_SUCCESSFULLY")))
                .andExpect(jsonPath("$.data", is(true)));
    }
}
