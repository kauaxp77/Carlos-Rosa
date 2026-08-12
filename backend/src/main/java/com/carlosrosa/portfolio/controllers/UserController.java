package com.carlosrosa.portfolio.controllers;

import com.carlosrosa.portfolio.dtos.UserCreateRequest;
import com.carlosrosa.portfolio.dtos.UserDTO;
import com.carlosrosa.portfolio.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateRequest request, Authentication authentication) {
        try {
            UserDTO newUser = userService.createUser(request, authentication.getName());
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        try {
            userService.deleteUser(id, authentication.getName());
            return ResponseEntity.ok(java.util.Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
