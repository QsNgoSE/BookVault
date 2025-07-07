package com.bookvault.book.model;

/**
 * Enum representing different payment methods
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card", "Payment via credit card"),
    DEBIT_CARD("Debit Card", "Payment via debit card"),
    PAYPAL("PayPal", "Payment via PayPal"),
    STRIPE("Stripe", "Payment via Stripe"),
    APPLE_PAY("Apple Pay", "Payment via Apple Pay"),
    GOOGLE_PAY("Google Pay", "Payment via Google Pay"),
    BANK_TRANSFER("Bank Transfer", "Payment via bank transfer"),
    CASH_ON_DELIVERY("Cash on Delivery", "Payment on delivery"),
    CRYPTOCURRENCY("Cryptocurrency", "Payment via cryptocurrency");

    private final String displayName;
    private final String description;

    PaymentMethod(String displayName, String description) {
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
     * Check if payment method requires online processing
     */
    public boolean requiresOnlineProcessing() {
        return this != CASH_ON_DELIVERY;
    }

    /**
     * Check if payment method is digital wallet
     */
    public boolean isDigitalWallet() {
        return this == PAYPAL || this == APPLE_PAY || this == GOOGLE_PAY;
    }

    /**
     * Check if payment method is card-based
     */
    public boolean isCardBased() {
        return this == CREDIT_CARD || this == DEBIT_CARD;
    }
} 