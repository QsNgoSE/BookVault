package com.bookvault.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * BookVault Book Service
 * Handles book catalog management, search, and reviews
 */
@SpringBootApplication(scanBasePackages = {"com.bookvault.book", "com.bookvault.shared"})
@EnableJpaAuditing
public class BookServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
} 