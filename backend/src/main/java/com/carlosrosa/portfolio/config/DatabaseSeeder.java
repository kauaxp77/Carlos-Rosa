package com.carlosrosa.portfolio.config;

import com.carlosrosa.portfolio.entities.Role;
import com.carlosrosa.portfolio.entities.User;
import com.carlosrosa.portfolio.repositories.RoleRepository;
import com.carlosrosa.portfolio.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void seedAdmin() {
        Optional<User> adminOpt = userRepository.findByEmail("admin@carlosrosa.com");

        if (adminOpt.isEmpty()) {
            System.out.println("[DatabaseSeeder] Criando usuario ADMIN master.");
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@carlosrosa.com");
            admin.setPassword(passwordEncoder.encode("123mudar"));

            // Buscar a role ADMIN criada pela Flyway
            Optional<Role> adminRoleOpt = roleRepository.findByName("ADMIN");
            
            if (adminRoleOpt.isPresent()) {
                Set<Role> roles = new HashSet<>();
                roles.add(adminRoleOpt.get());
                admin.setRoles(roles);

                try {
                    userRepository.save(admin);
                    System.out.println("[DatabaseSeeder] Usuario ADMIN criado com sucesso!");
                } catch (Exception e) {
                    System.out.println("[DatabaseSeeder] Erro ao criar usuario ADMIN.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("[DatabaseSeeder] Role ADMIN não encontrada. Verifique as migrações Flyway.");
            }
        }
    }
}
