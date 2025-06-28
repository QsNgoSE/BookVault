package com.bookvault.order.dto;

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
    
    // Default constructor
    public OrderItemResponse() {}
    
    // Full constructor
    public OrderItemResponse(UUID id, UUID bookId, String bookTitle, String bookAuthor,
                            String bookIsbn, String bookImageUrl, Integer quantity,
                            BigDecimal unitPrice, BigDecimal totalPrice, BigDecimal discountAmount,
                            BigDecimal finalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.bookImageUrl = bookImageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.discountAmount = discountAmount;
        this.finalPrice = finalPrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
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
        
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        
        public Builder bookId(UUID bookId) {
            this.bookId = bookId;
            return this;
        }
        
        public Builder bookTitle(String bookTitle) {
            this.bookTitle = bookTitle;
            return this;
        }
        
        public Builder bookAuthor(String bookAuthor) {
            this.bookAuthor = bookAuthor;
            return this;
        }
        
        public Builder bookIsbn(String bookIsbn) {
            this.bookIsbn = bookIsbn;
            return this;
        }
        
        public Builder bookImageUrl(String bookImageUrl) {
            this.bookImageUrl = bookImageUrl;
            return this;
        }
        
        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }
        
        public Builder unitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
            return this;
        }
        
        public Builder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }
        
        public Builder discountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }
        
        public Builder finalPrice(BigDecimal finalPrice) {
            this.finalPrice = finalPrice;
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
        
        public OrderItemResponse build() {
            return new OrderItemResponse(id, bookId, bookTitle, bookAuthor, bookIsbn,
                                       bookImageUrl, quantity, unitPrice, totalPrice,
                                       discountAmount, finalPrice, createdAt, updatedAt);
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getBookId() {
        return bookId;
    }
    
    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBookAuthor() {
        return bookAuthor;
    }
    
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    
    public String getBookIsbn() {
        return bookIsbn;
    }
    
    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }
    
    public String getBookImageUrl() {
        return bookImageUrl;
    }
    
    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
    
    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    
    /**
     * Get discount percentage
     */
    public BigDecimal getDiscountPercentage() {
        if (totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) > 0 && 
            discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return discountAmount.divide(totalPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Check if item has discount
     */
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Get formatted book info
     */
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