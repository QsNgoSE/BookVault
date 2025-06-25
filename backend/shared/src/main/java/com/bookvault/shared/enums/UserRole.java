package com.bookvault.shared.enums;

// import lombok.Getter;

/**
 * User role enumeration for role-based security
 */
// @Getter
public enum UserRole {
    USER("ROLE_USER", "Regular user with basic permissions"),
    SELLER("ROLE_SELLER", "Seller with book management permissions"),
    ADMIN("ROLE_ADMIN", "Administrator with full system access");
    
    private final String authority;
    private final String description;
    
    UserRole(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }
    
    public static UserRole fromAuthority(String authority) {
        for (UserRole role : values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown authority: " + authority);
    }
    
    // Manual getter methods (replacing Lombok)
    public String getAuthority() {
        return authority;
    }
    
    public String getDescription() {
        return description;
    }
} 