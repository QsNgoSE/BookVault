package com.bookvault.auth.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * CORS Configuration for frontend integration
 */
@Configuration
public class CorsConfig {
    
    @Value("${cors.allowed-origins:https://qsngose.github.io,http://localhost:5500,http://127.0.0.1:5500,http://localhost:8080,http://localhost:3000}")
    private String allowedOrigins;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Split allowed origins from property and trim whitespace
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        
        // Use setAllowedOrigins for exact matching (required when allowCredentials is true)
        configuration.setAllowedOrigins(origins);
        
        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // Allow specific methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Configure path mapping
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
} 