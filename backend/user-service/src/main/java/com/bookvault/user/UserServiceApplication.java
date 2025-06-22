package com.bookvault.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * BookVault User Service Application
 * Handles user profile management and user-related operations
 */
@SpringBootApplication(scanBasePackages = {"com.bookvault.user", "com.bookvault.shared"})
@EnableJpaAuditing
public class UserServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
} 