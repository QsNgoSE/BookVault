package com.bookvault.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * BookVault Order Service Application
 * Handles order processing, shopping cart, and purchase transactions
 */
@SpringBootApplication(scanBasePackages = {"com.bookvault.order", "com.bookvault.shared"})
@EnableJpaAuditing
public class OrderServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
} 