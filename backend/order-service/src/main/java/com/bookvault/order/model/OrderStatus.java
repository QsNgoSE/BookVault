package com.bookvault.order.model;

/**
 * Enumeration for order status
 */
public enum OrderStatus {
    PENDING("Order placed and awaiting confirmation"),
    CONFIRMED("Order confirmed and being processed"),
    PROCESSING("Order is being prepared"),
    SHIPPED("Order has been shipped"),
    OUT_FOR_DELIVERY("Order is out for delivery"),
    DELIVERED("Order has been delivered"),
    CANCELLED("Order has been cancelled"),
    REFUNDED("Order has been refunded");
    
    private final String description;
    
    OrderStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if this status allows cancellation
     */
    public boolean allowsCancellation() {
        return this == PENDING || this == CONFIRMED;
    }
    
    /**
     * Check if this is a final status (order completed or terminated)
     */
    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED || this == REFUNDED;
    }
    
    /**
     * Get next possible statuses from current status
     */
    public OrderStatus[] getNextPossibleStatuses() {
        switch (this) {
            case PENDING:
                return new OrderStatus[]{CONFIRMED, CANCELLED};
            case CONFIRMED:
                return new OrderStatus[]{PROCESSING, CANCELLED};
            case PROCESSING:
                return new OrderStatus[]{SHIPPED, CANCELLED};
            case SHIPPED:
                return new OrderStatus[]{OUT_FOR_DELIVERY, DELIVERED};
            case OUT_FOR_DELIVERY:
                return new OrderStatus[]{DELIVERED};
            case DELIVERED:
                return new OrderStatus[]{REFUNDED};
            case CANCELLED:
            case REFUNDED:
            default:
                return new OrderStatus[0]; // No further transitions
        }
    }
} 