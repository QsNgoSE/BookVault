package com.bookvault.book.config;

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
 * OPTIMIZED JWT Authentication Filter for Book Service
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    // OPTIMIZATION: Define public endpoints to skip JWT processing
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/books/categories",
        "/api/books/search",
        "/api/books/featured", 
        "/api/books/bestsellers",
        "/api/books/new-releases",
        "/api/books/filter",
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
        String method = request.getMethod();
        
        // CRITICAL: Allow OPTIONS requests to pass through for CORS preflight
        if ("OPTIONS".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // OPTIMIZATION: Skip JWT processing for public GET endpoints
        if ("GET".equals(method) && isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // OPTIMIZATION: Skip JWT processing for public book browsing
        if ("GET".equals(method) && (requestPath.equals("/api/books") || 
            requestPath.matches("/api/books/[a-fA-F0-9-]{36}") ||
            requestPath.startsWith("/api/books/category/") ||
            requestPath.startsWith("/api/books/author/") ||
            requestPath.startsWith("/api/books/isbn/"))) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // IMPORTANT: Seller endpoints require authentication
        if (requestPath.startsWith("/api/books/seller/")) {
            // Continue with JWT authentication for seller endpoints
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = authHeader.substring(7);
            
            // OPTIMIZATION: Quick token validation before expensive operations
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
                
                // Set userId as request attribute for controller access
                request.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            // OPTIMIZATION: Reduce logging noise for invalid tokens
            if (logger.isDebugEnabled()) {
                logger.debug("JWT authentication failed: " + e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * OPTIMIZATION: Check if endpoint is public to skip JWT processing
     */
    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(requestPath::startsWith);
    }
} 