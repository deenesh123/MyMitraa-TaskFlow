package com.mymitraa.taskflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mymitraa.taskflow.api_controller.UserController;
import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.UserRepository;
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

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("USER-01: Admin can retrieve all users, password hash is stripped")
    void testGetAllUsers() throws Exception {
        User u1 = new User();
        u1.setId("u-1");
        u1.setName("Alice Admin");
        u1.setEmail("alice@mymitraa.com");
        u1.setPassword("hashedPassword");

        User u2 = new User();
        u2.setId("u-2");
        u2.setName("Bob Dev");
        u2.setEmail("bob@mymitraa.com");
        u2.setPassword("hashedPassword2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        mockMvc.perform(get("/api/users/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.data[1].password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("USER-02: Get user by existing ID returns 200, non-existent returns 404")
    void testGetUserById() throws Exception {
        User user = new User();
        user.setId("u-existing");
        user.setName("Charlie QA");
        user.setEmail("charlie@mymitraa.com");

        when(userRepository.findById("u-existing")).thenReturn(Optional.of(user));
        when(userRepository.findById("u-missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/u-existing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.data.name", is("Charlie QA")));

        mockMvc.perform(get("/api/users/u-missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(404)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("USER-03: Admin cannot provision Super Admin account (HTTP 403)")
    void testAdminCannotProvisionSuperAdmin() throws Exception {
        User superAdminTarget = new User();
        superAdminTarget.setName("Sneaky User");
        superAdminTarget.setEmail("sneaky@mymitraa.com");
        superAdminTarget.setRole("Super Admin");

        mockMvc.perform(post("/api/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(superAdminTarget)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.statusCode", is(403)))
                .andExpect(jsonPath("$.message", containsString("Only Super Admin can provision Super Admin")));
    }

    @Test
    @WithMockUser(username = "superadmin", roles = {"SUPER_ADMIN"})
    @DisplayName("USER-04: Super Admin can provision valid users with hashed default password")
    void testSuperAdminCanProvisionUser() throws Exception {
        User newUser = new User();
        newUser.setName("New Dev");
        newUser.setEmail("newdev@mymitraa.com");
        newUser.setRole("Developer");

        when(userRepository.findByEmail("newdev@mymitraa.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername(any())).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$encodedDefaultPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId("u-created");
            return u;
        });

        mockMvc.perform(post("/api/users/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode", is(200)))
                .andExpect(jsonPath("$.data.email", is("newdev@mymitraa.com")));
    }
}
