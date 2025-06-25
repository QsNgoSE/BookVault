package com.bookvault.auth.dto;

import java.util.UUID;

import com.bookvault.shared.enums.UserRole;

/**
 * Simple authentication response DTO
 */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class AuthResponse {    
    private String token;
    // @Builder.Default
    private String type = "Bearer";
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, String type, UUID userId, String email, String firstName, String lastName, UserRole role) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String token;
        private String type = "Bearer";
        private UUID userId;
        private String email;
        private String firstName;
        private String lastName;
        private UserRole role;
        
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }
        
        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
        
        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }
        
        public AuthResponse build() {
            return new AuthResponse(token, type, userId, email, firstName, lastName, role);
        }
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
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