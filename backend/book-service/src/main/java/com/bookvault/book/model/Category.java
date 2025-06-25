package com.bookvault.book.model;

import com.bookvault.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// import lombok.*;

import java.util.List;

/**
 * Category entity for organizing books
 */
@Entity
@Table(name = "categories")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
public class Category extends BaseEntity {
    
    @Column(name = "name", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;
    
    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Column(name = "is_active", nullable = false)
    // @Builder.Default
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookCategory> bookCategories;
    
    // Constructors
    public Category() {}
    
    public Category(String name, String description, Boolean isActive, List<BookCategory> bookCategories) {
        this.name = name;
        this.description = description;
        this.isActive = isActive != null ? isActive : true;
        this.bookCategories = bookCategories;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private String description;
        private Boolean isActive = true;
        private List<BookCategory> bookCategories;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }
        
        public Builder bookCategories(List<BookCategory> bookCategories) {
            this.bookCategories = bookCategories;
            return this;
        }
        
        public Category build() {
            return new Category(name, description, isActive, bookCategories);
        }
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public List<BookCategory> getBookCategories() {
        return bookCategories;
    }
    
    public void setBookCategories(List<BookCategory> bookCategories) {
        this.bookCategories = bookCategories;
    }
    
    // Business methods
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
} 