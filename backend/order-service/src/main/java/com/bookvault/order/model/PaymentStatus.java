package com.bookvault.order.model;

/**
 * Enumeration for payment status
 */
public enum PaymentStatus {
    PENDING("Payment is pending"),
    PROCESSING("Payment is being processed"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    CANCELLED("Payment was cancelled"),
    REFUNDED("Payment was refunded"),
    PARTIALLY_REFUNDED("Payment was partially refunded");
    
    private final String description;
    
    PaymentStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if payment is successful
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
    
    /**
     * Check if payment is in a final state
     */
    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED || this == REFUNDED;
    }
    
    /**
     * Check if payment can be refunded
     */
    public boolean canBeRefunded() {
        return this == COMPLETED || this == PARTIALLY_REFUNDED;
    }
    
    /**
     * Check if payment is pending or processing
     */
    public boolean isPendingOrProcessing() {
        return this == PENDING || this == PROCESSING;
    }
} 