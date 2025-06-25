package com.bookvault.book.dto;

import jakarta.validation.constraints.*;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Simple DTO for updating books
 */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
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
    
    // Constructors
    public BookUpdateRequest() {}
    
    public BookUpdateRequest(String title, String author, String description, BigDecimal price,
                            String coverImageUrl, Integer stockQuantity, String language,
                            Integer pageCount, String publisher) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.coverImageUrl = coverImageUrl;
        this.stockQuantity = stockQuantity;
        this.language = language;
        this.pageCount = pageCount;
        this.publisher = publisher;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String title;
        private String author;
        private String description;
        private BigDecimal price;
        private String coverImageUrl;
        private Integer stockQuantity;
        private String language;
        private Integer pageCount;
        private String publisher;
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder author(String author) {
            this.author = author;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }
        
        public Builder coverImageUrl(String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
            return this;
        }
        
        public Builder stockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }
        
        public Builder language(String language) {
            this.language = language;
            return this;
        }
        
        public Builder pageCount(Integer pageCount) {
            this.pageCount = pageCount;
            return this;
        }
        
        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }
        
        public BookUpdateRequest build() {
            return new BookUpdateRequest(title, author, description, price, coverImageUrl,
                                       stockQuantity, language, pageCount, publisher);
        }
    }
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public Integer getPageCount() {
        return pageCount;
    }
    
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
} 