package com.app.demo.configuration;

import com.app.demo.entity.User;
import com.app.demo.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDataSeeder {

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            String adminPassword = System.getenv("APP_ADMIN_PASSWORD");
            String spectatorPassword = System.getenv("APP_SPECTATOR_PASSWORD");
            
            if (adminPassword == null || adminPassword.isBlank()) {
                throw new IllegalStateException("APP_ADMIN_PASSWORD environment variable must be set");
            }
            if (spectatorPassword == null || spectatorPassword.isBlank()) {
                throw new IllegalStateException("APP_SPECTATOR_PASSWORD environment variable must be set");
            }

            seedUser(userRepository, encoder, "admin", adminPassword, "ADMIN");
            seedUser(userRepository, encoder, "user", spectatorPassword, "SPECTATOR");
        };
    }

    private void seedUser(UserRepository repo, PasswordEncoder encoder,
                          String username, String rawPassword, String role) {
        if (repo.findByUsername(username).isPresent()) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(role);
        repo.save(user);
    }
}