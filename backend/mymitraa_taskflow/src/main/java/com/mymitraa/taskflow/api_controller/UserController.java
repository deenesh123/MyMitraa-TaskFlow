package com.mymitraa.taskflow.api_controller;

import com.mymitraa.taskflow.api_domain.User;
import com.mymitraa.taskflow.api_repository.UserRepository;
import com.mymitraa.taskflow.api_successcode.CustomResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/getAll")
    public ResponseEntity<CustomResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(new CustomResponse(200, "USERS_FETCHED", users));
    }

    @PostMapping("/save")
    public ResponseEntity<CustomResponse> saveUser(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("Password@123"));
        } else if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getUsername() == null && user.getEmail() != null) {
            user.setUsername(user.getEmail().contains("@") ? user.getEmail().split("@")[0] : user.getEmail());
        }
        User saved = userRepository.save(user);
        return ResponseEntity.ok(new CustomResponse(200, "USER_SAVED", saved));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CustomResponse> updateUser(@PathVariable("id") String id, @RequestBody User update) {
        Optional<User> existingOpt = userRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.ok(new CustomResponse(404, "USER_NOT_FOUND", null));
        }
        User existing = existingOpt.get();
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getEmail() != null) existing.setEmail(update.getEmail());
        if (update.getRole() != null) existing.setRole(update.getRole());
        if (update.getTeam() != null) existing.setTeam(update.getTeam());
        if (update.getStatus() != null) existing.setStatus(update.getStatus());
        if (update.getAvatar() != null) existing.setAvatar(update.getAvatar());
        if (update.getPassword() != null && !update.getPassword().isEmpty() && !update.getPassword().startsWith("$2a$")) {
            existing.setPassword(passwordEncoder.encode(update.getPassword()));
        }
        User saved = userRepository.save(existing);
        return ResponseEntity.ok(new CustomResponse(200, "USER_UPDATED", saved));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponse> deleteUser(@PathVariable("id") String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new CustomResponse(200, "USER_DELETED", true));
        }
        return ResponseEntity.ok(new CustomResponse(404, "USER_NOT_FOUND", false));
    }
}
