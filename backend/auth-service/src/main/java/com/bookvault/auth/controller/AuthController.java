package com.bookvault.auth.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookvault.auth.dto.AuthResponse;
import com.bookvault.auth.dto.LoginRequest;
import com.bookvault.auth.dto.RegisterRequest;
import com.bookvault.auth.dto.UpdateProfileRequest;
import com.bookvault.auth.dto.UserProfileResponse;
import com.bookvault.auth.service.AuthService;
import com.bookvault.auth.util.ClientIpUtil;
import com.bookvault.shared.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Simple Authentication REST Controller
 */
@RestController
@RequestMapping("/api/auth")
// @RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and profile management")
public class AuthController {
    
    private final AuthService authService;
    
    // Constructor (replacing @RequiredArgsConstructor)
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Register a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "User registered successfully"));
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String clientIp = ClientIpUtil.getClientIpAddress(httpRequest);
        AuthResponse response = authService.login(request, clientIp);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }
    
    @GetMapping("/profile/{userId}")
    @Operation(summary = "Get user profile", description = "Get user profile information")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        
        UserProfileResponse profile = authService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @PutMapping("/profile/{userId}")
    @Operation(summary = "Update user profile", description = "Update user profile information")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Valid @RequestBody UpdateProfileRequest request) {
        
        UserProfileResponse profile = authService.updateUserProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile updated successfully"));
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate token", description = "Validate JWT token and return user info")
    public ResponseEntity<ApiResponse<UserProfileResponse>> validateToken(
            @Parameter(description = "JWT token") @RequestHeader("Authorization") String authHeader) {
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid authorization header"));
        }
        
        String token = authHeader.substring(7);
        UserProfileResponse profile = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if auth service is running")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running"));
    }
    
    @PostMapping("/clear-bans")
    @Operation(summary = "Clear all login bans", description = "Clear all login bans and failed attempts (for development)")
    public ResponseEntity<ApiResponse<String>> clearAllBans() {
        authService.clearAllLoginAttempts();
        return ResponseEntity.ok(ApiResponse.success("All login bans cleared", "All bans and failed attempts have been cleared"));
    }
} 