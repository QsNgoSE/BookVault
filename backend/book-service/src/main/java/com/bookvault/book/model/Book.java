package com.bookvault.book.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
// import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Book entity representing a book in the catalog
 */
@Entity
@Table(name = "books")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
public class Book extends BaseEntity {
    
    @Column(name = "title", nullable = false, length = 255)
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Column(name = "author", nullable = false, length = 255)
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @Column(name = "isbn", unique = true, length = 20)
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$", 
             message = "Invalid ISBN format")
    private String isbn;
    
    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be positive")
    private BigDecimal price;
    
    @Column(name = "published_date")
    private LocalDate publishedDate;
    
    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;
    
    @Column(name = "stock_quantity", nullable = false)
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must not be negative")
    private Integer stockQuantity;
    
    @Column(name = "seller_id", nullable = false)
    @NotNull(message = "Seller ID is required")
    private UUID sellerId;
    
    @Column(name = "is_active", nullable = false)
    // @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "rating", precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private BigDecimal rating;
    
    @Column(name = "review_count")
    @Min(value = 0, message = "Review count must not be negative")
    // @Builder.Default
    private Integer reviewCount = 0;
    
    @Column(name = "language", length = 50)
    private String language;
    
    @Column(name = "page_count")
    @Min(value = 1, message = "Page count must be positive")
    private Integer pageCount;
    
    @Column(name = "publisher", length = 255)
    private String publisher;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookCategory> bookCategories;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookReview> reviews;
    
    // Constructors
    public Book() {}
    
    public Book(String title, String author, String isbn, String description, BigDecimal price,
                LocalDate publishedDate, String coverImageUrl, Integer stockQuantity, UUID sellerId,
                Boolean isActive, BigDecimal rating, Integer reviewCount, String language,
                Integer pageCount, String publisher, List<BookCategory> bookCategories, List<BookReview> reviews) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.price = price;
        this.publishedDate = publishedDate;
        this.coverImageUrl = coverImageUrl;
        this.stockQuantity = stockQuantity;
        this.sellerId = sellerId;
        this.isActive = isActive != null ? isActive : true;
        this.rating = rating;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
        this.language = language;
        this.pageCount = pageCount;
        this.publisher = publisher;
        this.bookCategories = bookCategories;
        this.reviews = reviews;
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
        private Boolean isActive = true;
        private BigDecimal rating;
        private Integer reviewCount = 0;
        private String language;
        private Integer pageCount;
        private String publisher;
        private List<BookCategory> bookCategories;
        private List<BookReview> reviews;
        
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
        
        public Builder bookCategories(List<BookCategory> bookCategories) {
            this.bookCategories = bookCategories;
            return this;
        }
        
        public Builder reviews(List<BookReview> reviews) {
            this.reviews = reviews;
            return this;
        }
        
        public Book build() {
            return new Book(title, author, isbn, description, price, publishedDate, coverImageUrl,
                           stockQuantity, sellerId, isActive, rating, reviewCount, language,
                           pageCount, publisher, bookCategories, reviews);
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
    
    public List<BookCategory> getBookCategories() {
        return bookCategories;
    }
    
    public void setBookCategories(List<BookCategory> bookCategories) {
        this.bookCategories = bookCategories;
    }
    
    public List<BookReview> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<BookReview> reviews) {
        this.reviews = reviews;
    }
    
    // Business methods
    public void updateRating(BigDecimal newRating, int newReviewCount) {
        this.rating = newRating;
        this.reviewCount = newReviewCount;
    }
    
    public void updateStock(int quantity) {
        this.stockQuantity = quantity;
    }
    
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    public boolean isAvailable() {
        return isActive && isInStock();
    }
    
    public void decrementStock(int quantity) {
        if (stockQuantity == null) {
            throw new IllegalArgumentException("Stock quantity is not initialized");
        }
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + stockQuantity + ", Requested: " + quantity);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity -= quantity;
    }
    
    public void incrementStock(int quantity) {
        if (stockQuantity == null) {
            this.stockQuantity = quantity;
        } else {
            this.stockQuantity += quantity;
        }
    }
    
    /**
     * Reserve stock for an order (temporary hold)
     */
    public void reserveStock(int quantity) {
        if (stockQuantity == null || stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock for reservation");
        }
        // In a real system, you might have a separate reserved_stock field
        // For now, we'll just validate availability
    }
    
    /**
     * Release reserved stock
     */
    public void releaseReservedStock(int quantity) {
        // In a real system, you would decrease reserved_stock
        // For now, we'll just validate
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }
} 