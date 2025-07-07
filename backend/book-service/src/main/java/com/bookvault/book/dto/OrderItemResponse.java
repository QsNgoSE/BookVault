package com.bookvault.book.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for order items
 */
public class OrderItemResponse {
    
    private UUID id;
    private UUID bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private String bookImageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public OrderItemResponse() {}
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private OrderItemResponse response = new OrderItemResponse();
        
        public Builder id(UUID id) {
            response.id = id;
            return this;
        }
        
        public Builder bookId(UUID bookId) {
            response.bookId = bookId;
            return this;
        }
        
        public Builder bookTitle(String bookTitle) {
            response.bookTitle = bookTitle;
            return this;
        }
        
        public Builder bookAuthor(String bookAuthor) {
            response.bookAuthor = bookAuthor;
            return this;
        }
        
        public Builder bookIsbn(String bookIsbn) {
            response.bookIsbn = bookIsbn;
            return this;
        }
        
        public Builder bookImageUrl(String bookImageUrl) {
            response.bookImageUrl = bookImageUrl;
            return this;
        }
        
        public Builder quantity(Integer quantity) {
            response.quantity = quantity;
            return this;
        }
        
        public Builder unitPrice(BigDecimal unitPrice) {
            response.unitPrice = unitPrice;
            return this;
        }
        
        public Builder totalPrice(BigDecimal totalPrice) {
            response.totalPrice = totalPrice;
            return this;
        }
        
        public Builder discountAmount(BigDecimal discountAmount) {
            response.discountAmount = discountAmount;
            return this;
        }
        
        public Builder finalPrice(BigDecimal finalPrice) {
            response.finalPrice = finalPrice;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            response.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            response.updatedAt = updatedAt;
            return this;
        }
        
        public OrderItemResponse build() {
            return response;
        }
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getBookId() { return bookId; }
    public void setBookId(UUID bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
    
    public String getBookIsbn() { return bookIsbn; }
    public void setBookIsbn(String bookIsbn) { this.bookIsbn = bookIsbn; }
    
    public String getBookImageUrl() { return bookImageUrl; }
    public void setBookImageUrl(String bookImageUrl) { this.bookImageUrl = bookImageUrl; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    public BigDecimal getFinalPrice() { return finalPrice; }
    public void setFinalPrice(BigDecimal finalPrice) { this.finalPrice = finalPrice; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public BigDecimal getDiscountPercentage() {
        if (totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) > 0 && 
            discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return discountAmount.divide(totalPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
    
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public String getBookInfo() {
        StringBuilder info = new StringBuilder(bookTitle != null ? bookTitle : "Unknown");
        if (bookAuthor != null && !bookAuthor.trim().isEmpty()) {
            info.append(" by ").append(bookAuthor);
        }
        if (bookIsbn != null && !bookIsbn.trim().isEmpty()) {
            info.append(" (ISBN: ").append(bookIsbn).append(")");
        }
        return info.toString();
    }
} 