package com.bookvault.book.config;

import com.bookvault.shared.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Enhanced Security configuration for Book Service
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtUtil jwtUtil;
    
    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {}) // Use the existing CorsConfig
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Health and documentation endpoints
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // Public READ access to books
                .requestMatchers(HttpMethod.GET, "/api/books/categories").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/featured").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/bestsellers").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/new-releases").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/filter").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/category/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/author/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/isbn/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/books/seller/**").authenticated() // Seller-specific endpoints require auth
                .requestMatchers(HttpMethod.GET, "/api/books").permitAll() // Allow public book browsing
                
                // Require authentication for write operations
                .requestMatchers(HttpMethod.POST, "/api/books").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/books/upload").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/books/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/books/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/books/**").authenticated()
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
} 