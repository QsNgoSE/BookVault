package com.bookvault.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * BookVault Discovery Service - Eureka Server
 * Service discovery for microservices architecture
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
} 