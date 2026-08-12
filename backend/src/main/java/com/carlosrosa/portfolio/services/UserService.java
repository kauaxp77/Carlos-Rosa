package com.carlosrosa.portfolio.services;

import com.carlosrosa.portfolio.dtos.UserCreateRequest;
import com.carlosrosa.portfolio.dtos.UserDTO;
import com.carlosrosa.portfolio.entities.AuditLog;
import com.carlosrosa.portfolio.entities.Role;
import com.carlosrosa.portfolio.entities.User;
import com.carlosrosa.portfolio.repositories.AuditLogRepository;
import com.carlosrosa.portfolio.repositories.RoleRepository;
import com.carlosrosa.portfolio.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserCreateRequest request, String adminUsername) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String requestedRole = request.getRole() != null ? request.getRole().toUpperCase() : "VIEWER";
        Role role = roleRepository.findByName(requestedRole)
                .orElseGet(() -> {
                    Role newRole = new Role(requestedRole);
                    return roleRepository.save(newRole);
                });

        user.setRoles(Collections.singleton(role));
        User saved = userRepository.save(user);

        auditLogRepository.save(new AuditLog("CREATE", "USER", "Created user " + saved.getUsername(), adminUsername));

        return mapToDTO(saved);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long id, String adminUsername) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getUsername().equals("admin") || user.getUsername().equals(adminUsername)) {
            throw new RuntimeException("Cannot delete super admin or yourself.");
        }

        userRepository.delete(user);
        auditLogRepository.save(new AuditLog("DELETE", "USER", "Deleted user " + user.getUsername(), adminUsername));
    }

    private UserDTO mapToDTO(User user) {
        List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), roles);
    }
}
