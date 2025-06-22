package com.bookvault.book.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Simple DTO for updating books
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateRequest {
    
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @DecimalMin(value = "0.00", message = "Price must be positive")
    private BigDecimal price;
    
    private String coverImageUrl;
    
    @Min(value = 0, message = "Stock quantity must not be negative")
    private Integer stockQuantity;
    
    private String language;
    
    @Min(value = 1, message = "Page count must be positive")
    private Integer pageCount;
    
    private String publisher;
} 