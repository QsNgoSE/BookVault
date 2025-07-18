package com.bookvault.order.config;

import com.bookvault.shared.security.JwtUtil;
import com.bookvault.shared.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

/**
 * JWT Authentication Filter for Order Service
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    // Define public endpoints to skip JWT processing
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/actuator/health",
        "/v3/api-docs",
        "/swagger-ui"
    );
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Skip JWT processing for public endpoints
        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = authHeader.substring(7);
            
            // Validate token and extract user information
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                UUID userId = jwtUtil.getUserIdFromToken(token);
                String email = jwtUtil.getEmailFromToken(token);
                UserRole role = jwtUtil.getRoleFromToken(token);
                
                List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role.name())
                );
                
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                // Add user ID to request attributes for easy access in controllers
                request.setAttribute("userId", userId);
                request.setAttribute("userEmail", email);
                request.setAttribute("userRole", role);
            }
        } catch (Exception e) {
            // Log JWT authentication failures
            if (logger.isDebugEnabled()) {
                logger.debug("JWT authentication failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Check if endpoint is public to skip JWT processing
     */
    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestPath::startsWith);
    }
} 