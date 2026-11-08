package com.mymitraa.taskflow.api_controller;

import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.UserRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean isCallerSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_SUPER_ADMIN") ||
                               a.getAuthority().equalsIgnoreCase("Super Admin"));
    }

    @GetMapping({"", "/getAll"})
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR_MANAGER')")
    public ResponseEntity<CustomResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(new CustomResponse(200, "USERS_FETCHED", users));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'HR_MANAGER')")
    public ResponseEntity<CustomResponse> getUserById(@PathVariable("id") String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(new CustomResponse(404, "USER_NOT_FOUND", null));
        }
        User user = userOpt.get();
        user.setPassword(null);
        return ResponseEntity.ok(new CustomResponse(200, "USER_FETCHED", user));
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CustomResponse> saveUser(@RequestBody User user) {
        if ("Super Admin".equalsIgnoreCase(user.getRole()) && !isCallerSuperAdmin()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body(new CustomResponse(403, "Only Super Admin can provision Super Admin accounts", null));
        }

        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail().trim()).isPresent()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse(400, "User already exists with email: " + user.getEmail(), null));
        }

        if (user.getUsername() == null && user.getEmail() != null) {
            user.setUsername(user.getEmail().contains("@") ? user.getEmail().split("@")[0] : user.getEmail());
        }

        if (user.getUsername() != null && userRepository.findByUsername(user.getUsername().trim()) != null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(new CustomResponse(400, "Username already taken: " + user.getUsername(), null));
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("Password@123"));
        } else if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getCreated_date() == null) {
            user.setCreated_date(new java.text.SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        }

        User saved = userRepository.save(user);
        saved.setPassword(null);
        return ResponseEntity.ok(new CustomResponse(200, "USER_SAVED", saved));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CustomResponse> updateUser(@PathVariable("id") String id, @RequestBody User update) {
        Optional<User> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(new CustomResponse(404, "USER_NOT_FOUND", null));
        }
        User existing = existingOpt.get();

        // Security check: Admin cannot modify Super Admin privileges or accounts
        boolean callerIsSuperAdmin = isCallerSuperAdmin();
        if ("Super Admin".equalsIgnoreCase(existing.getRole()) && !callerIsSuperAdmin) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body(new CustomResponse(403, "Admins cannot modify Super Admin accounts", null));
        }

        if (update.getRole() != null && "Super Admin".equalsIgnoreCase(update.getRole()) && !callerIsSuperAdmin) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body(new CustomResponse(403, "Only Super Admin can assign the Super Admin role", null));
        }

        if (update.getEmail() != null && !update.getEmail().equalsIgnoreCase(existing.getEmail())) {
            if (userRepository.findByEmail(update.getEmail().trim()).isPresent()) {
                return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                        .body(new CustomResponse(400, "Email already in use by another user", null));
            }
            existing.setEmail(update.getEmail().trim());
        }

        if (update.getName() != null) existing.setName(update.getName());
        if (update.getRole() != null) existing.setRole(update.getRole());
        if (update.getTeam() != null) existing.setTeam(update.getTeam());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        if (update.getAvatar() != null) existing.setAvatar(update.getAvatar());
        if (update.getPassword() != null && !update.getPassword().isEmpty() && !update.getPassword().startsWith("$2a$")) {
            existing.setPassword(passwordEncoder.encode(update.getPassword()));
        }

        User saved = userRepository.save(existing);
        saved.setPassword(null);
        return ResponseEntity.ok(new CustomResponse(200, "USER_UPDATED", saved));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CustomResponse> deleteUser(@PathVariable("id") String id) {
        Optional<User> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                    .body(new CustomResponse(404, "USER_NOT_FOUND", false));
        }
        User existing = existingOpt.get();

        if ("Super Admin".equalsIgnoreCase(existing.getRole())) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body(new CustomResponse(403, "Super Admin accounts cannot be deleted", false));
        }

        if ("Admin".equalsIgnoreCase(existing.getRole()) && !isCallerSuperAdmin()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body(new CustomResponse(403, "Only Super Admin can delete Admin accounts", false));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(new CustomResponse(200, "USER_DELETED", true));
    }
}
