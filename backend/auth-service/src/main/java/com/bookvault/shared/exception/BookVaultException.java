package com.bookvault.shared.exception;

// import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for BookVault application
 */
// @Getter
public class BookVaultException extends RuntimeException {
    
    private final String errorCode;
    private final HttpStatus httpStatus;
    
    public BookVaultException(String message) {
        super(message);
        this.errorCode = "BOOKVAULT_ERROR";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BookVaultException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BookVaultException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public BookVaultException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BOOKVAULT_ERROR";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BookVaultException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
    public BookVaultException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    // Manual getter methods (replacing Lombok)
    public String getErrorCode() {
        return errorCode;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
} 