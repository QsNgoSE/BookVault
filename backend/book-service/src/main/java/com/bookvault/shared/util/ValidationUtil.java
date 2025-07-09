package com.bookvault.shared.util;

import com.bookvault.shared.exception.BadRequestException;

import java.util.regex.Pattern;

/**
 * Utility class for validation methods
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[+]?[0-9]{10,15}$");
    
    /**
     * Validate email format
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Invalid email format");
        }
    }
    
    /**
     * Validate password strength
     */
    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
    }
    
    /**
     * Validate phone number format
     */
    public static void validatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new BadRequestException("Invalid phone number format");
            }
        }
    }
    
    /**
     * Validate required string field
     */
    public static void validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " is required");
        }
    }
} 