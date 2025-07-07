package com.bookvault.book.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request DTO for order items within an order
 */
public class OrderItemRequest {
    
    @NotNull(message = "Book ID is required")
    private UUID bookId;
    
    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Book title must not exceed 200 characters")
    private String bookTitle;
    
    @Size(max = 200, message = "Book author must not exceed 200 characters")
    private String bookAuthor;
    
    @Size(max = 20, message = "Book ISBN must not exceed 20 characters")
    private String bookIsbn;
    
    @Size(max = 500, message = "Book image URL must not exceed 500 characters")
    private String bookImageUrl;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
    
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    // Constructors
    public OrderItemRequest() {}
    
    public OrderItemRequest(UUID bookId, String bookTitle, Integer quantity, BigDecimal unitPrice) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    // Getters and Setters
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
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    
    // Helper methods
    public BigDecimal getTotalPrice() {
        if (quantity != null && unitPrice != null) {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    public BigDecimal getFinalPrice() {
        BigDecimal total = getTotalPrice();
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        return total.subtract(discount);
    }
} 