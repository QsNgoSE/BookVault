package com.bookvault.order.dto;

import com.bookvault.order.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request DTO for creating new orders
 */
public class CreateOrderRequest {
    
    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;
    
    // Shipping Information
    @NotBlank(message = "Shipping address is required")
    @Size(max = 500, message = "Shipping address must not exceed 500 characters")
    private String shippingAddress;
    
    @NotBlank(message = "Shipping city is required")
    @Size(max = 100, message = "Shipping city must not exceed 100 characters")
    private String shippingCity;
    
    @Size(max = 100, message = "Shipping state must not exceed 100 characters")
    private String shippingState;
    
    @NotBlank(message = "Shipping postal code is required")
    @Size(max = 20, message = "Shipping postal code must not exceed 20 characters")
    private String shippingPostalCode;
    
    @NotBlank(message = "Shipping country is required")
    @Size(max = 100, message = "Shipping country must not exceed 100 characters")
    private String shippingCountry;
    
    // Payment Information
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    // Customer Information
    @NotBlank(message = "Customer email is required")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;
    
    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;
    
    // Optional Notes
    @Size(max = 1000, message = "Order notes must not exceed 1000 characters")
    private String orderNotes;
    
    // Default constructor
    public CreateOrderRequest() {}
    
    // Full constructor
    public CreateOrderRequest(List<OrderItemRequest> items, String shippingAddress, String shippingCity,
                             String shippingState, String shippingPostalCode, String shippingCountry,
                             PaymentMethod paymentMethod, String customerEmail, String customerPhone,
                             String customerName, String orderNotes) {
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingState = shippingState;
        this.shippingPostalCode = shippingPostalCode;
        this.shippingCountry = shippingCountry;
        this.paymentMethod = paymentMethod;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerName = customerName;
        this.orderNotes = orderNotes;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private List<OrderItemRequest> items;
        private String shippingAddress;
        private String shippingCity;
        private String shippingState;
        private String shippingPostalCode;
        private String shippingCountry;
        private PaymentMethod paymentMethod;
        private String customerEmail;
        private String customerPhone;
        private String customerName;
        private String orderNotes;
        
        public Builder items(List<OrderItemRequest> items) {
            this.items = items;
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
        
        public CreateOrderRequest build() {
            return new CreateOrderRequest(items, shippingAddress, shippingCity, shippingState,
                                        shippingPostalCode, shippingCountry, paymentMethod,
                                        customerEmail, customerPhone, customerName, orderNotes);
        }
    }
    
    // Getters and Setters
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    
    public String getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public String getShippingCity() {
        return shippingCity;
    }
    
    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }
    
    public String getShippingState() {
        return shippingState;
    }
    
    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }
    
    public String getShippingPostalCode() {
        return shippingPostalCode;
    }
    
    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }
    
    public String getShippingCountry() {
        return shippingCountry;
    }
    
    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getOrderNotes() {
        return orderNotes;
    }
    
    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }
} 