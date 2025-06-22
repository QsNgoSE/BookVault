package com.bookvault.book.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Simple DTO for book review response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookReviewResponse {
    
    private UUID id;
    private UUID userId;
    private BigDecimal rating;
    private String title;
    private String comment;
    private Boolean isVerifiedPurchase;
    private Integer helpfulCount;
    private Boolean isApproved;
    private LocalDateTime createdAt;
} 