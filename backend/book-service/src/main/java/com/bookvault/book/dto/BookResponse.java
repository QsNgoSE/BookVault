package com.bookvault.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for book response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    
    private UUID id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private BigDecimal price;
    private LocalDate publishedDate;
    private String coverImageUrl;
    private Integer stockQuantity;
    private UUID sellerId;
    private Boolean isActive;
    private BigDecimal rating;
    private Integer reviewCount;
    private String language;
    private Integer pageCount;
    private String publisher;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<CategoryResponse> categories;
    private List<BookReviewResponse> reviews;
    
    // Computed fields
    private Boolean inStock;
    private Boolean available;
    
    public Boolean getInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    public Boolean getAvailable() {
        return isActive && getInStock();
    }
} 