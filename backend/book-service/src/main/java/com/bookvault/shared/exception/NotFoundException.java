package com.bookvault.shared.exception;

/**
 * Exception thrown when a resource is not found
 */
public class NotFoundException extends BookVaultException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 