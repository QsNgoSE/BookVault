package com.bookvault.auth.dto;

import com.bookvault.shared.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for admin user update operations
 */
public class AdminUserUpdateRequest {
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    private UserRole role;
    private Boolean isActive;
    private Boolean isVerified;
    
    // Default constructor
    public AdminUserUpdateRequest() {}
    
    // Full constructor
    public AdminUserUpdateRequest(String email, String firstName, String lastName, 
                                String phone, UserRole role, Boolean isActive, Boolean isVerified) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
        this.isVerified = isVerified;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private UserRole role;
        private Boolean isActive;
        private Boolean isVerified;
        
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
        
        public AdminUserUpdateRequest build() {
            return new AdminUserUpdateRequest(email, firstName, lastName, phone, 
                                             role, isActive, isVerified);
        }
    }
    
    // Getters and Setters
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
} 