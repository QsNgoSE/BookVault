package com.bookvault.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * BookVault Auth Service
 * Simple authentication and user management
 */
@SpringBootApplication(scanBasePackages = {"com.bookvault.auth", "com.bookvault.shared"})

@EnableJpaAuditing
public class AuthServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
} 