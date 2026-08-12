package com.carlosrosa.portfolio.controllers;

import com.carlosrosa.portfolio.dtos.JwtResponse;
import com.carlosrosa.portfolio.dtos.LoginRequest;
import com.carlosrosa.portfolio.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        JwtUtils jwtUtils;

        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

                Authentication authentication;
                try {
                        authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                        loginRequest.getPassword()));
                } catch (org.springframework.security.core.AuthenticationException e) {
                        return ResponseEntity.status(401).body(
                                        java.util.Map.of("error", "Unauthorized Access", "message", e.getMessage()));
                }

                SecurityContextHolder.getContext().setAuthentication(authentication);

                User userDetails = (User) authentication.getPrincipal();

                // Find the primary role or construct it appropriately
                String primaryRole = userDetails.getAuthorities().stream().findFirst()
                                .map(GrantedAuthority::getAuthority)
                                .orElse("ROLE_VIEWER");

                String jwt = jwtUtils.generateJwtToken(userDetails.getUsername(), primaryRole);

                List<String> roles = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(
                                jwt,
                                0L, // ID not stored in native UserDetails, normally mapped from DB
                                userDetails.getUsername(),
                                "", // Email could be queried if needed
                                roles));
        }
}
