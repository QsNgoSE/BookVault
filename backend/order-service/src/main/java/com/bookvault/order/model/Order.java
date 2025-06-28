package com.bookvault.order.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order entity representing customer orders
 */
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    
    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @Column(name = "order_number", nullable = false, unique = true)
    @NotBlank(message = "Order number is required")
    @Size(max = 50, message = "Order number must not exceed 50 characters")
    private String orderNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;
    
    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;
    
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;
    
    // Shipping Information
    @Column(name = "shipping_address", nullable = false)
    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address must not exceed 500 characters")
    private String shippingAddress;
    
    @Column(name = "shipping_city", nullable = false)
    @NotBlank(message = "Shipping city is required")
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;
    
    @Column(name = "shipping_state")
    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    private String shippingState;
    
    @Column(name = "shipping_postal_code", nullable = false)
    @NotBlank(message = "Shipping postal code is required")
    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    private String shippingPostalCode;
    
    @Column(name = "shipping_country", nullable = false)
    @NotBlank(message = "Shipping country is required")
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    private String shippingCountry;
    
    // Payment Information
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_transaction_id")
    @Size(max = 100, message = "Payment transaction ID must not exceed 100 characters")
    private String paymentTransactionId;
    
    // Order Items - One-to-Many relationship
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // Customer Information
    @Column(name = "customer_email", nullable = false)
    @NotBlank(message = "Customer email is required")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;
    
    @Column(name = "customer_phone")
    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;
    
    @Column(name = "customer_name", nullable = false)
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;
    
    // Tracking Information
    @Column(name = "tracking_number")
    @Size(max = 100, message = "Tracking number must not exceed 100 characters")
    private String trackingNumber;
    
    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "cancellation_reason")
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    private String cancellationReason;
    
    // Notes
    @Column(name = "order_notes")
    @Size(max = 1000, message = "Order notes must not exceed 1000 characters")
    private String orderNotes;
    
    // Default constructor
    public Order() {}
    
    // Constructor with required fields
    public Order(UUID userId, String orderNumber, BigDecimal totalAmount, String shippingAddress,
                String shippingCity, String shippingPostalCode, String shippingCountry,
                PaymentMethod paymentMethod, String customerEmail, String customerName) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.finalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingPostalCode = shippingPostalCode;
        this.shippingCountry = shippingCountry;
        this.paymentMethod = paymentMethod;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID userId;
        private String orderNumber;
        private OrderStatus status = OrderStatus.PENDING;
        private BigDecimal totalAmount;
        private BigDecimal shippingCost = BigDecimal.ZERO;
        private BigDecimal taxAmount = BigDecimal.ZERO;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private String shippingAddress;
        private String shippingCity;
        private String shippingState;
        private String shippingPostalCode;
        private String shippingCountry;
        private PaymentMethod paymentMethod;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;
        private String paymentTransactionId;
        private String trackingNumber;
        private LocalDateTime estimatedDeliveryDate;
        private String customerEmail;
        private String customerPhone;
        private String customerName;
        private String orderNotes;
        
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
        
        public Builder trackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
            return this;
        }
        
        public Builder estimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
            this.estimatedDeliveryDate = estimatedDeliveryDate;
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
        
        public Builder orderNotes(String orderNotes) {
            this.orderNotes = orderNotes;
            return this;
        }
        
        public Order build() {
            Order order = new Order(userId, orderNumber, totalAmount, shippingAddress,
                                  shippingCity, shippingPostalCode, shippingCountry,
                                  paymentMethod, customerEmail, customerName);
            order.status = status;
            order.shippingCost = shippingCost;
            order.taxAmount = taxAmount;
            order.discountAmount = discountAmount;
            order.shippingState = shippingState;
            order.paymentStatus = paymentStatus;
            order.paymentTransactionId = paymentTransactionId;
            order.trackingNumber = trackingNumber;
            order.estimatedDeliveryDate = estimatedDeliveryDate;
            order.customerPhone = customerPhone;
            order.orderNotes = orderNotes;
            
            // Calculate final amount
            order.calculateFinalAmount();
            
            return order;
        }
    }
    
    // Getters and Setters
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
    
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    
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
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getOrderNotes() { return orderNotes; }
    public void setOrderNotes(String orderNotes) { this.orderNotes = orderNotes; }
    
    // Business logic methods
    
    /**
     * Calculate final amount including taxes, shipping, and discounts
     */
    public void calculateFinalAmount() {
        BigDecimal itemsTotal = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        BigDecimal shipping = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        
        this.finalAmount = itemsTotal.add(shipping).add(tax).subtract(discount);
    }
    
    /**
     * Add order item to this order
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    
    /**
     * Remove order item from this order
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
    }
    
    /**
     * Check if order can be cancelled
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    /**
     * Cancel the order
     */
    public void cancel(String reason) {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled in current status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }
    
    /**
     * Mark order as delivered
     */
    public void markAsDelivered() {
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }
    
    /**
     * Get order items count
     */
    public int getItemCount() {
        return orderItems.size();
    }
    
    /**
     * Get total quantity of all items
     */
    public int getTotalQuantity() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    /**
     * Get full shipping address as formatted string
     */
    public String getFullShippingAddress() {
        StringBuilder address = new StringBuilder();
        address.append(shippingAddress);
        address.append(", ").append(shippingCity);
        if (shippingState != null && !shippingState.trim().isEmpty()) {
            address.append(", ").append(shippingState);
        }
        address.append(" ").append(shippingPostalCode);
        address.append(", ").append(shippingCountry);
        return address.toString();
    }
    
    /**
     * Check if order is in a final state
     */
    public boolean isFinalState() {
        return status == OrderStatus.DELIVERED || 
               status == OrderStatus.CANCELLED || 
               status == OrderStatus.REFUNDED;
    }
}