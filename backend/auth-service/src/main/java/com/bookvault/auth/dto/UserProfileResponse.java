package com.bookvault.auth.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bookvault.shared.enums.UserRole;

/**
 * User profile response DTO
 */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class UserProfileResponse {
    
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String avatarUrl;
    private UserRole role;
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    
    // Constructors
    public UserProfileResponse() {}
    
    public UserProfileResponse(UUID id, String email, String firstName, String lastName, String phone, String avatarUrl,
                              UserRole role, Boolean isActive, Boolean isVerified, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.isActive = isActive;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String avatarUrl;
        private UserRole role;
        private Boolean isActive;
        private Boolean isVerified;
        private LocalDateTime createdAt;
        
        public Builder id(UUID id) {
            this.id = id;
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
        
        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }
        
        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }
        
        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }
        
        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }
        
        public Builder isVerified(Boolean isVerified) {
            this.isVerified = isVerified;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public UserProfileResponse build() {
            return new UserProfileResponse(id, email, firstName, lastName, phone, avatarUrl, role, isActive, isVerified, createdAt);
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Computed field
    public String getFullName() {
        return firstName + " " + lastName;
    }
} 