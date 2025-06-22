package com.bookvault.book.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Book entity representing a book in the catalog
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    @Column(name = "stock_quantity", nullable = false)
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must not be negative")
    private Integer stockQuantity;
    
    @Column(name = "seller_id", nullable = false)
    @NotNull(message = "Seller ID is required")
    private UUID sellerId;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "rating", precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Rating must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 0 and 5")
    private BigDecimal rating;
    
    @Column(name = "review_count")
    @Min(value = 0, message = "Review count must not be negative")
    @Builder.Default
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
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.stockQuantity -= quantity;
    }
    
    public void incrementStock(int quantity) {
        this.stockQuantity += quantity;
    }
} 