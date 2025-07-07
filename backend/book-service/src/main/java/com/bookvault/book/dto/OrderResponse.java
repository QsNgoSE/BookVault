package com.bookvault.book.dto;

import com.bookvault.book.model.OrderStatus;
import com.bookvault.book.model.PaymentMethod;
import com.bookvault.book.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for orders
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
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paymentTransactionId;
    private String customerEmail;
    private String customerPhone;
    private String customerName;
    private List<OrderItemResponse> orderItems;
    private String trackingNumber;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String orderNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public OrderResponse() {}
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private OrderResponse orderResponse = new OrderResponse();
        
        public Builder id(UUID id) {
            orderResponse.id = id;
            return this;
        }
        
        public Builder userId(UUID userId) {
            orderResponse.userId = userId;
            return this;
        }
        
        public Builder orderNumber(String orderNumber) {
            orderResponse.orderNumber = orderNumber;
            return this;
        }
        
        public Builder status(OrderStatus status) {
            orderResponse.status = status;
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            orderResponse.totalAmount = totalAmount;
            return this;
        }
        
        public Builder shippingCost(BigDecimal shippingCost) {
            orderResponse.shippingCost = shippingCost;
            return this;
        }
        
        public Builder taxAmount(BigDecimal taxAmount) {
            orderResponse.taxAmount = taxAmount;
            return this;
        }
        
        public Builder discountAmount(BigDecimal discountAmount) {
            orderResponse.discountAmount = discountAmount;
            return this;
        }
        
        public Builder finalAmount(BigDecimal finalAmount) {
            orderResponse.finalAmount = finalAmount;
            return this;
        }
        
        public Builder shippingAddress(String shippingAddress) {
            orderResponse.shippingAddress = shippingAddress;
            return this;
        }
        
        public Builder shippingCity(String shippingCity) {
            orderResponse.shippingCity = shippingCity;
            return this;
        }
        
        public Builder shippingState(String shippingState) {
            orderResponse.shippingState = shippingState;
            return this;
        }
        
        public Builder shippingPostalCode(String shippingPostalCode) {
            orderResponse.shippingPostalCode = shippingPostalCode;
            return this;
        }
        
        public Builder shippingCountry(String shippingCountry) {
            orderResponse.shippingCountry = shippingCountry;
            return this;
        }
        
        public Builder paymentMethod(PaymentMethod paymentMethod) {
            orderResponse.paymentMethod = paymentMethod;
            return this;
        }
        
        public Builder paymentStatus(PaymentStatus paymentStatus) {
            orderResponse.paymentStatus = paymentStatus;
            return this;
        }
        
        public Builder paymentTransactionId(String paymentTransactionId) {
            orderResponse.paymentTransactionId = paymentTransactionId;
            return this;
        }
        
        public Builder customerEmail(String customerEmail) {
            orderResponse.customerEmail = customerEmail;
            return this;
        }
        
        public Builder customerPhone(String customerPhone) {
            orderResponse.customerPhone = customerPhone;
            return this;
        }
        
        public Builder customerName(String customerName) {
            orderResponse.customerName = customerName;
            return this;
        }
        
        public Builder orderItems(List<OrderItemResponse> orderItems) {
            orderResponse.orderItems = orderItems;
            return this;
        }
        
        public Builder trackingNumber(String trackingNumber) {
            orderResponse.trackingNumber = trackingNumber;
            return this;
        }
        
        public Builder estimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
            orderResponse.estimatedDeliveryDate = estimatedDeliveryDate;
            return this;
        }
        
        public Builder deliveredAt(LocalDateTime deliveredAt) {
            orderResponse.deliveredAt = deliveredAt;
            return this;
        }
        
        public Builder cancelledAt(LocalDateTime cancelledAt) {
            orderResponse.cancelledAt = cancelledAt;
            return this;
        }
        
        public Builder cancellationReason(String cancellationReason) {
            orderResponse.cancellationReason = cancellationReason;
            return this;
        }
        
        public Builder orderNotes(String orderNotes) {
            orderResponse.orderNotes = orderNotes;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            orderResponse.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            orderResponse.updatedAt = updatedAt;
            return this;
        }
        
        public OrderResponse build() {
            return orderResponse;
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
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public List<OrderItemResponse> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemResponse> orderItems) { this.orderItems = orderItems; }
    
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
    
    // Helper methods
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }
    
    public int getTotalQuantity() {
        return orderItems != null ? 
               orderItems.stream().mapToInt(OrderItemResponse::getQuantity).sum() : 0;
    }
    
    public String getFullShippingAddress() {
        StringBuilder address = new StringBuilder();
        if (shippingAddress != null) address.append(shippingAddress);
        if (shippingCity != null) address.append(", ").append(shippingCity);
        if (shippingState != null) address.append(", ").append(shippingState);
        if (shippingPostalCode != null) address.append(" ").append(shippingPostalCode);
        if (shippingCountry != null) address.append(", ").append(shippingCountry);
        return address.toString();
    }
} 