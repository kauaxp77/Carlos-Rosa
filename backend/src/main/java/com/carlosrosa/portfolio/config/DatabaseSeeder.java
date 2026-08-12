package com.carlosrosa.portfolio.config;

import com.carlosrosa.portfolio.entities.Role;
import com.carlosrosa.portfolio.entities.User;
import com.carlosrosa.portfolio.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DatabaseSeeder {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seedAdmin() {
        Optional<User> adminOpt = userRepository.findByEmail("admin@carlosrosa.com");

        if (adminOpt.isEmpty()) {
            System.out.println("[DatabaseSeeder] Criando usuario ADMIN master.");
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@carlosrosa.com");
            // Encrypt test password matching our prompt deliverables
            admin.setPassword(passwordEncoder.encode("123mudar"));

            // To be safe assuming Flyway created roles, we mock role attribution for this
            // functional logic layer
            Role r = new Role();
            r.setId(1); // The ID specified in V1 Flyway schema for ROLE_ADMIN
            // If flyway logic breaks references, JPA unmerges it. But using pre-generated
            // IDs works.

            Set<Role> roles = new HashSet<>();
            roles.add(r);
            admin.setRoles(roles);

            try {
                userRepository.save(admin);
                System.out.println("[DatabaseSeeder] Usuario ADMIN criado com sucesso!");
            } catch (Exception e) {
                System.out.println(
                        "[DatabaseSeeder] Flyway ainda executando ou ID 1 não existe. Execução de fallback abortada.");
                e.printStackTrace();
            }
        }
    }
}
