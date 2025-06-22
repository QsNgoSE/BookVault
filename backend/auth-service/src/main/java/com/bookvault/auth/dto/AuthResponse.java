package com.bookvault.auth.dto;

import com.bookvault.shared.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Simple authentication response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    
    // Helper method
    public static AuthResponse of(String token, UUID userId, String email, 
                                 String firstName, String lastName, UserRole role) {
        return AuthResponse.builder()
                .token(token)
                .userId(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .build();
    }
} 