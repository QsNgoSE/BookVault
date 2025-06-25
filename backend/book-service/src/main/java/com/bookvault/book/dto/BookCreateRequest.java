package com.bookvault.book.dto;

import jakarta.validation.constraints.*;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating a new book
 */
// @Data
// @Builder
// @NoArgsConstructor
// @AllArgsConstructor
public class BookCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$", 
             message = "Invalid ISBN format")
    private String isbn;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be positive")
    private BigDecimal price;
    
    private LocalDate publishedDate;
    
    private String coverImageUrl;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must not be negative")
    private Integer stockQuantity;
    
    @NotNull(message = "Seller ID is required")
    private UUID sellerId;
    
    private String language;
    
    @Min(value = 1, message = "Page count must be positive")
    private Integer pageCount;
    
    private String publisher;
    
    @NotEmpty(message = "At least one category is required")
    private List<String> categoryNames;
    
    // Constructors
    public BookCreateRequest() {}
    
    public BookCreateRequest(String title, String author, String isbn, String description, BigDecimal price,
                            LocalDate publishedDate, String coverImageUrl, Integer stockQuantity, UUID sellerId,
                            String language, Integer pageCount, String publisher, List<String> categoryNames) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.price = price;
        this.publishedDate = publishedDate;
        this.coverImageUrl = coverImageUrl;
        this.stockQuantity = stockQuantity;
        this.sellerId = sellerId;
        this.language = language;
        this.pageCount = pageCount;
        this.publisher = publisher;
        this.categoryNames = categoryNames;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String title;
        private String author;
        private String isbn;
        private String description;
        private BigDecimal price;
        private LocalDate publishedDate;
        private String coverImageUrl;
        private Integer stockQuantity;
        private UUID sellerId;
        private String language;
        private Integer pageCount;
        private String publisher;
        private List<String> categoryNames;
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder author(String author) {
            this.author = author;
            return this;
        }
        
        public Builder isbn(String isbn) {
            this.isbn = isbn;
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
        
        public Builder publishedDate(LocalDate publishedDate) {
            this.publishedDate = publishedDate;
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
        
        public Builder sellerId(UUID sellerId) {
            this.sellerId = sellerId;
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
        
        public Builder categoryNames(List<String> categoryNames) {
            this.categoryNames = categoryNames;
            return this;
        }
        
        public BookCreateRequest build() {
            return new BookCreateRequest(title, author, isbn, description, price, publishedDate,
                                       coverImageUrl, stockQuantity, sellerId, language, pageCount,
                                       publisher, categoryNames);
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
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
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
    
    public LocalDate getPublishedDate() {
        return publishedDate;
    }
    
    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
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
    
    public UUID getSellerId() {
        return sellerId;
    }
    
    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
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
    
    public List<String> getCategoryNames() {
        return categoryNames;
    }
    
    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }
} 