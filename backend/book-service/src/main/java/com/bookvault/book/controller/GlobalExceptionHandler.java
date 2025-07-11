package com.bookvault.book.controller;

import com.bookvault.shared.dto.ApiResponse;
import com.bookvault.shared.exception.BadRequestException;
import com.bookvault.shared.exception.BookVaultException;
import com.bookvault.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for Book Service
 * Provides consistent error responses across all endpoints
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle security/authorization exceptions
     */
    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ResponseEntity<ApiResponse<Object>> handleSecurityException(Exception e) {
        log.warn("Security exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage(), "AUTHORIZATION_ERROR"));
    }

    /**
     * Handle resource not found exceptions
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), "RESOURCE_NOT_FOUND"));
    }

    /**
     * Handle bad request exceptions
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException e) {
        log.warn("Bad request: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), "VALIDATION_ERROR"));
    }

    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        String message = "Validation failed for fields: " + String.join(", ", errors.keySet());
        log.warn("Validation error: {}", message);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, "VALIDATION_ERROR", 
                        errors.entrySet().stream()
                                .map(entry -> entry.getKey() + ": " + entry.getValue())
                                .collect(Collectors.toList())));
    }

    /**
     * Handle method argument type mismatch (e.g., invalid UUID format)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                e.getValue(), e.getName(), e.getRequiredType().getSimpleName());
        log.warn("Type mismatch error: {}", message);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, "INVALID_PARAMETER"));
    }

    /**
     * Handle file upload size exceeded
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        String message = "File size exceeds maximum allowed size";
        log.warn("File upload size exceeded: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, "FILE_SIZE_EXCEEDED"));
    }

    /**
     * Handle custom BookVault exceptions
     */
    @ExceptionHandler(BookVaultException.class)
    public ResponseEntity<ApiResponse<Object>> handleBookVaultException(BookVaultException e) {
        log.warn("BookVault exception: {} (Code: {})", e.getMessage(), e.getErrorCode());
        
        return ResponseEntity.status(e.getHttpStatus())
                .body(ApiResponse.error(e.getMessage(), e.getErrorCode()));
    }

    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage(), "INVALID_ARGUMENT"));
    }

    /**
     * Handle illegal state exceptions
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException e) {
        log.warn("Illegal state: {}", e.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getMessage(), "INVALID_STATE"));
    }

    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred", "RUNTIME_ERROR"));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred", "INTERNAL_ERROR"));
    }
} 