package com.ergo.Springserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding.
 * This class defines a bean for password encoding using BCrypt.
 */
@Configuration
public class PasswordConfig {

    /**
     * Creates a bean for PasswordEncoder using BCrypt.
     *
     * @return a BCryptPasswordEncoder instance for securely hashing passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
