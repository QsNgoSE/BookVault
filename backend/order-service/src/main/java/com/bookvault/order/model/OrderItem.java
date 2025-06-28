package com.bookvault.order.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * OrderItem entity representing individual items within an order
 */
@Entity
@Table(name = "order_items")
public class OrderItem extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull(message = "Order is required")
    private Order order;
    
    @Column(name = "book_id", nullable = false)
    @NotNull(message = "Book ID is required")
    private UUID bookId;
    
    @Column(name = "book_title", nullable = false)
    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Book title must not exceed 200 characters")
    private String bookTitle;
    
    @Column(name = "book_author")
    @Size(max = 200, message = "Book author must not exceed 200 characters")
    private String bookAuthor;
    
    @Column(name = "book_isbn")
    @Size(max = 20, message = "Book ISBN must not exceed 20 characters")
    private String bookIsbn;
    
    @Column(name = "book_image_url")
    @Size(max = 500, message = "Book image URL must not exceed 500 characters")
    private String bookImageUrl;
    
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private BigDecimal totalPrice;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;
    
    // Default constructor
    public OrderItem() {}
    
    // Constructor with required fields
    public OrderItem(UUID bookId, String bookTitle, Integer quantity, BigDecimal unitPrice) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    // Full constructor
    public OrderItem(UUID bookId, String bookTitle, String bookAuthor, String bookIsbn, 
                    String bookImageUrl, Integer quantity, BigDecimal unitPrice, BigDecimal discountAmount) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookIsbn = bookIsbn;
        this.bookImageUrl = bookImageUrl;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        calculateTotalPrice();
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID bookId;
        private String bookTitle;
        private String bookAuthor;
        private String bookIsbn;
        private String bookImageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        
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
        
        public Builder discountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }
        
        public OrderItem build() {
            return new OrderItem(bookId, bookTitle, bookAuthor, bookIsbn, 
                               bookImageUrl, quantity, unitPrice, discountAmount);
        }
    }
    
    // Getters and Setters
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
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
        calculateTotalPrice();
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
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
        this.discountAmount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        calculateFinalPrice();
    }
    
    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
    
    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }
    
    // Business logic methods
    
    /**
     * Calculate total price based on quantity and unit price
     */
    public void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            calculateFinalPrice();
        }
    }
    
    /**
     * Calculate final price after applying discount
     */
    public void calculateFinalPrice() {
        if (totalPrice != null) {
            BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
            this.finalPrice = totalPrice.subtract(discount);
        }
    }
    
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
        StringBuilder info = new StringBuilder(bookTitle);
        if (bookAuthor != null && !bookAuthor.trim().isEmpty()) {
            info.append(" by ").append(bookAuthor);
        }
        if (bookIsbn != null && !bookIsbn.trim().isEmpty()) {
            info.append(" (ISBN: ").append(bookIsbn).append(")");
        }
        return info.toString();
    }
} 