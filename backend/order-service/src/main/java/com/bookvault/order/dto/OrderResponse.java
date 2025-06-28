package com.bookvault.order.dto;

import com.bookvault.order.model.OrderStatus;
import com.bookvault.order.model.PaymentMethod;
import com.bookvault.order.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for order data
 */
public class OrderResponse {
    
    private UUID id;
    private UUID userId;
    private String orderNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    
    // Shipping Information
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
    private String fullShippingAddress;
    
    // Payment Information
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentTransactionId;
    
    // Order Items
    private List<OrderItemResponse> orderItems;
    
    // Customer Information
    private String customerEmail;
    private String customerPhone;
    private String customerName;
    
    // Tracking Information
    private String trackingNumber;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    
    // Notes
    private String orderNotes;
    
    // Metadata
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int itemCount;
    private int totalQuantity;
    
    // Default constructor
    public OrderResponse() {}
    
    // Full constructor
    public OrderResponse(UUID id, UUID userId, String orderNumber, OrderStatus status,
                        BigDecimal totalAmount, BigDecimal shippingCost, BigDecimal taxAmount,
                        BigDecimal discountAmount, BigDecimal finalAmount, String shippingAddress,
                        String shippingCity, String shippingState, String shippingPostalCode,
                        String shippingCountry, PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                        String paymentTransactionId, List<OrderItemResponse> orderItems,
                        String customerEmail, String customerPhone, String customerName,
                        String trackingNumber, LocalDateTime estimatedDeliveryDate,
                        LocalDateTime deliveredAt, LocalDateTime cancelledAt, String cancellationReason,
                        String orderNotes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.shippingCost = shippingCost;
        this.taxAmount = taxAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingState = shippingState;
        this.shippingPostalCode = shippingPostalCode;
        this.shippingCountry = shippingCountry;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentTransactionId = paymentTransactionId;
        this.orderItems = orderItems;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.trackingNumber = trackingNumber;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.deliveredAt = deliveredAt;
        this.cancelledAt = cancelledAt;
        this.cancellationReason = cancellationReason;
        this.orderNotes = orderNotes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        
        // Calculate derived fields
        this.itemCount = orderItems != null ? orderItems.size() : 0;
        this.totalQuantity = orderItems != null ? 
            orderItems.stream().mapToInt(OrderItemResponse::getQuantity).sum() : 0;
        this.fullShippingAddress = buildFullShippingAddress();
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID id;
        private UUID userId;
        private String orderNumber;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private BigDecimal shippingCost;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String shippingAddress;
        private String shippingCity;
        private String shippingState;
        private String shippingPostalCode;
        private String shippingCountry;
        private PaymentMethod paymentMethod;
        private PaymentStatus paymentStatus;
        private String paymentTransactionId;
        private List<OrderItemResponse> orderItems;
        private String customerEmail;
        private String customerPhone;
        private String customerName;
        private String trackingNumber;
        private LocalDateTime estimatedDeliveryDate;
        private LocalDateTime deliveredAt;
        private LocalDateTime cancelledAt;
        private String cancellationReason;
        private String orderNotes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        
        public Builder orderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }
        
        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public Builder shippingCost(BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }
        
        public Builder taxAmount(BigDecimal taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }
        
        public Builder discountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }
        
        public Builder finalAmount(BigDecimal finalAmount) {
            this.finalAmount = finalAmount;
            return this;
        }
        
        public Builder shippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }
        
        public Builder shippingCity(String shippingCity) {
            this.shippingCity = shippingCity;
            return this;
        }
        
        public Builder shippingState(String shippingState) {
            this.shippingState = shippingState;
            return this;
        }
        
        public Builder shippingPostalCode(String shippingPostalCode) {
            this.shippingPostalCode = shippingPostalCode;
            return this;
        }
        
        public Builder shippingCountry(String shippingCountry) {
            this.shippingCountry = shippingCountry;
            return this;
        }
        
        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }
        
        public Builder paymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }
        
        public Builder paymentTransactionId(String paymentTransactionId) {
            this.paymentTransactionId = paymentTransactionId;
            return this;
        }
        
        public Builder orderItems(List<OrderItemResponse> orderItems) {
            this.orderItems = orderItems;
            return this;
        }
        
        public Builder customerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }
        
        public Builder customerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
            return this;
        }
        
        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }
        
        public Builder trackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
            return this;
        }
        
        public Builder estimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
            this.estimatedDeliveryDate = estimatedDeliveryDate;
            return this;
        }
        
        public Builder deliveredAt(LocalDateTime deliveredAt) {
            this.deliveredAt = deliveredAt;
            return this;
        }
        
        public Builder cancelledAt(LocalDateTime cancelledAt) {
            this.cancelledAt = cancelledAt;
            return this;
        }
        
        public Builder cancellationReason(String cancellationReason) {
            this.cancellationReason = cancellationReason;
            return this;
        }
        
        public Builder orderNotes(String orderNotes) {
            this.orderNotes = orderNotes;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public OrderResponse build() {
            return new OrderResponse(id, userId, orderNumber, status, totalAmount, shippingCost,
                                   taxAmount, discountAmount, finalAmount, shippingAddress,
                                   shippingCity, shippingState, shippingPostalCode, shippingCountry,
                                   paymentMethod, paymentStatus, paymentTransactionId, orderItems,
                                   customerEmail, customerPhone, customerName, trackingNumber,
                                   estimatedDeliveryDate, deliveredAt, cancelledAt, cancellationReason,
                                   orderNotes, createdAt, updatedAt);
        }
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
    
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    
    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }
    
    public String getShippingState() { return shippingState; }
    public void setShippingState(String shippingState) { this.shippingState = shippingState; }
    
    public String getShippingPostalCode() { return shippingPostalCode; }
    public void setShippingPostalCode(String shippingPostalCode) { this.shippingPostalCode = shippingPostalCode; }
    
    public String getShippingCountry() { return shippingCountry; }
    public void setShippingCountry(String shippingCountry) { this.shippingCountry = shippingCountry; }
    
    public String getFullShippingAddress() { return fullShippingAddress; }
    public void setFullShippingAddress(String fullShippingAddress) { this.fullShippingAddress = fullShippingAddress; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    
    public List<OrderItemResponse> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemResponse> orderItems) { this.orderItems = orderItems; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    
    public LocalDateTime getEstimatedDeliveryDate() { return estimatedDeliveryDate; }
    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) { this.estimatedDeliveryDate = estimatedDeliveryDate; }
    
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    
    // Helper methods
    
    /**
     * Build full shipping address string
     */
    private String buildFullShippingAddress() {
        StringBuilder address = new StringBuilder();
        if (shippingAddress != null) address.append(shippingAddress);
        if (shippingCity != null) address.append(", ").append(shippingCity);
        if (shippingState != null && !shippingState.trim().isEmpty()) {
            address.append(", ").append(shippingState);
        }
        if (shippingPostalCode != null) address.append(" ").append(shippingPostalCode);
        if (shippingCountry != null) address.append(", ").append(shippingCountry);
        return address.toString();
    }
    
    /**
     * Check if order can be cancelled
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    /**
     * Check if order is in final state
     */
    public boolean isFinalState() {
        return status == OrderStatus.DELIVERED || 
               status == OrderStatus.CANCELLED || 
               status == OrderStatus.REFUNDED;
    }
    
    /**
     * Get status display name
     */
    public String getStatusDisplayName() {
        return status != null ? status.getDescription() : "Unknown";
    }
    
    /**
     * Get payment method display name
     */
    public String getPaymentMethodDisplayName() {
        return paymentMethod != null ? paymentMethod.getDisplayName() : "Unknown";
    }
    
    /**
     * Get payment status display name
     */
    public String getPaymentStatusDisplayName() {
        return paymentStatus != null ? paymentStatus.getDescription() : "Unknown";
    }
} 