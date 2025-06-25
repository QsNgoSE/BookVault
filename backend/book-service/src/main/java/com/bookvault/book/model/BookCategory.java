package com.bookvault.book.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
// import lombok.*;

/**
 * Join entity for Book-Category many-to-many relationship
 */
@Entity
@Table(name = "book_categories", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "category_id"}))
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
public class BookCategory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "is_primary", nullable = false)
    // @Builder.Default
    private Boolean isPrimary = false;
    
    // Constructors
    public BookCategory() {}
    
    public BookCategory(Book book, Category category, Boolean isPrimary) {
        this.book = book;
        this.category = category;
        this.isPrimary = isPrimary != null ? isPrimary : false;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Book book;
        private Category category;
        private Boolean isPrimary = false;
        
        public Builder book(Book book) {
            this.book = book;
            return this;
        }
        
        public Builder category(Category category) {
            this.category = category;
            return this;
        }
        
        public Builder isPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }
        
        public BookCategory build() {
            return new BookCategory(book, category, isPrimary);
        }
    }
    
    // Getters and Setters
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    
    // Business methods
    public void setPrimary() {
        this.isPrimary = true;
    }
    
    public void setSecondary() {
        this.isPrimary = false;
    }
} 