package com.bookvault.book.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Join entity for Book-Category many-to-many relationship
 */
@Entity
@Table(name = "book_categories", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "category_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCategory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;
    
    // Business methods
    public void setPrimary() {
        this.isPrimary = true;
    }
    
    public void setSecondary() {
        this.isPrimary = false;
    }
} 