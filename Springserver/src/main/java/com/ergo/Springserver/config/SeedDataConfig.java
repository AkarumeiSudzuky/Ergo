package com.ergo.Springserver.config;

import com.ergo.Springserver.model.user.User;
import com.ergo.Springserver.model.user.UserDao;
import com.ergo.Springserver.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * SeedDataConfig initializes the database with default data if empty.
 * This implementation ensures at least one admin user is present.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDao userDao;

    /**
     * Executes on application startup to seed initial data.
     *
     * @param args command-line arguments.
     * @throws Exception if an error occurs during execution.
     */
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {

            User admin = User.builder()
                    .username("admin")
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("password"))
                    .build();

            userDao.save(admin);

            log.debug("Created ADMIN user - {}", admin);
        }
    }
}
