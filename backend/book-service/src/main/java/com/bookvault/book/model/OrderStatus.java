package com.bookvault.book.model;

/**
 * Enum representing different order statuses
 */
public enum OrderStatus {
    PENDING("Pending", "Order has been placed and is waiting for confirmation"),
    CONFIRMED("Confirmed", "Order has been confirmed and is being prepared"),
    PROCESSING("Processing", "Order is being processed and items are being prepared"),
    SHIPPED("Shipped", "Order has been shipped and is on the way"),
    DELIVERED("Delivered", "Order has been delivered to the customer"),
    CANCELLED("Cancelled", "Order has been cancelled"),
    REFUNDED("Refunded", "Order has been refunded"),
    RETURNED("Returned", "Order has been returned by the customer");

    private final String displayName;
    private final String description;

    OrderStatus(String displayName, String description) {
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
     * Check if the status indicates the order is active
     */
    public boolean isActive() {
        return this != CANCELLED && this != REFUNDED && this != RETURNED;
    }

    /**
     * Check if the status indicates the order is completed
     */
    public boolean isCompleted() {
        return this == DELIVERED;
    }

    /**
     * Check if the status indicates the order is in progress
     */
    public boolean isInProgress() {
        return this == CONFIRMED || this == PROCESSING || this == SHIPPED;
    }

    /**
     * Check if the status allows cancellation
     */
    public boolean canBeCancelled() {
        return this == PENDING || this == CONFIRMED;
    }
} 