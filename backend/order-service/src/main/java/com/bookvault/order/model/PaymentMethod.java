package com.bookvault.order.model;

/**
 * Enumeration for payment methods
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PAYPAL("PayPal"),
    BANK_TRANSFER("Bank Transfer"),
    CASH_ON_DELIVERY("Cash on Delivery"),
    DIGITAL_WALLET("Digital Wallet"),
    CRYPTOCURRENCY("Cryptocurrency");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Check if this payment method requires immediate payment
     */
    public boolean requiresImmediatePayment() {
        return this != CASH_ON_DELIVERY && this != BANK_TRANSFER;
    }
    
    /**
     * Check if this payment method supports refunds
     */
    public boolean supportsRefunds() {
        return this != CASH_ON_DELIVERY && this != CRYPTOCURRENCY;
    }
} 