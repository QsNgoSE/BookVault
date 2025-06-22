package com.bookvault.shared.exception;

/**
 * Exception thrown when a bad request is made
 */
public class BadRequestException extends BookVaultException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
} 