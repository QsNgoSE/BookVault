package com.bookvault.book.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * BookReview entity for book reviews and ratings
 */
@Entity
@Table(name = "book_reviews", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookReview extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    @NotNull(message = "Rating is required")
    @DecimalMin(value = "1.0", message = "Rating must be between 1 and 5")
    @DecimalMax(value = "5.0", message = "Rating must be between 1 and 5")
    private BigDecimal rating;
    
    @Column(name = "title", length = 255)
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;
    
    @Column(name = "is_verified_purchase", nullable = false)
    @Builder.Default
    private Boolean isVerifiedPurchase = false;
    
    @Column(name = "helpful_count")
    @Min(value = 0, message = "Helpful count must not be negative")
    @Builder.Default
    private Integer helpfulCount = 0;
    
    @Column(name = "is_approved", nullable = false)
    @Builder.Default
    private Boolean isApproved = true;
    
    // Business methods
    public void approve() {
        this.isApproved = true;
    }
    
    public void reject() {
        this.isApproved = false;
    }
    
    public void markAsVerifiedPurchase() {
        this.isVerifiedPurchase = true;
    }
    
    public void incrementHelpfulCount() {
        this.helpfulCount++;
    }
    
    public void decrementHelpfulCount() {
        if (this.helpfulCount > 0) {
            this.helpfulCount--;
        }
    }
} 