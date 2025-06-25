package com.bookvault.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for book response data
 */
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
    
    // Constructors
    public BookResponse() {}
    
    public BookResponse(UUID id, String title, String author, String isbn, String description,
                       BigDecimal price, LocalDate publishedDate, String coverImageUrl, Integer stockQuantity,
                       UUID sellerId, Boolean isActive, BigDecimal rating, Integer reviewCount, String language,
                       Integer pageCount, String publisher, LocalDateTime createdAt, LocalDateTime updatedAt,
                       List<CategoryResponse> categories, List<BookReviewResponse> reviews, Boolean inStock, Boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.price = price;
        this.publishedDate = publishedDate;
        this.coverImageUrl = coverImageUrl;
        this.stockQuantity = stockQuantity;
        this.sellerId = sellerId;
        this.isActive = isActive;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.language = language;
        this.pageCount = pageCount;
        this.publisher = publisher;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categories = categories;
        this.reviews = reviews;
        this.inStock = inStock;
        this.available = available;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
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
        private Boolean inStock;
        private Boolean available;
        
        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        
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
        
        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }
        
        public Builder rating(BigDecimal rating) {
            this.rating = rating;
            return this;
        }
        
        public Builder reviewCount(Integer reviewCount) {
            this.reviewCount = reviewCount;
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
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        public Builder categories(List<CategoryResponse> categories) {
            this.categories = categories;
            return this;
        }
        
        public Builder reviews(List<BookReviewResponse> reviews) {
            this.reviews = reviews;
            return this;
        }
        
        public Builder inStock(Boolean inStock) {
            this.inStock = inStock;
            return this;
        }
        
        public Builder available(Boolean available) {
            this.available = available;
            return this;
        }
        
        public BookResponse build() {
            return new BookResponse(id, title, author, isbn, description, price, publishedDate,
                                  coverImageUrl, stockQuantity, sellerId, isActive, rating, reviewCount,
                                  language, pageCount, publisher, createdAt, updatedAt, categories,
                                  reviews, inStock, available);
        }
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    
    public Integer getReviewCount() {
        return reviewCount;
    }
    
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
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
    
    public List<CategoryResponse> getCategories() {
        return categories;
    }
    
    public void setCategories(List<CategoryResponse> categories) {
        this.categories = categories;
    }
    
    public List<BookReviewResponse> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<BookReviewResponse> reviews) {
        this.reviews = reviews;
    }
    
    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }
    
    public void setAvailable(Boolean available) {
        this.available = available;
    }
    
    // Computed fields (override the auto-generated getters)
    public Boolean getInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    public Boolean getAvailable() {
        return isActive && getInStock();
    }
} 