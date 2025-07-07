package com.bookvault.book.model;

/**
 * Enum representing different payment statuses
 */
public enum PaymentStatus {
    PENDING("Pending", "Payment is pending"),
    PROCESSING("Processing", "Payment is being processed"),
    COMPLETED("Completed", "Payment has been completed successfully"),
    FAILED("Failed", "Payment has failed"),
    CANCELLED("Cancelled", "Payment has been cancelled"),
    REFUNDED("Refunded", "Payment has been refunded"),
    PARTIALLY_REFUNDED("Partially Refunded", "Payment has been partially refunded"),
    DISPUTED("Disputed", "Payment is under dispute"),
    EXPIRED("Expired", "Payment has expired");

    private final String displayName;
    private final String description;

    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
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
     * Check if payment is in progress
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }

    /**
     * Check if payment has failed
     */
    public boolean hasFailed() {
        return this == FAILED || this == CANCELLED || this == EXPIRED;
    }

    /**
     * Check if payment can be refunded
     */
    public boolean canBeRefunded() {
        return this == COMPLETED;
    }
} 