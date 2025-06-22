package com.bookvault.book.repository;

import com.bookvault.book.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Category entity
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    // Basic queries
    Optional<Category> findByName(String name);
    
    List<Category> findByIsActiveTrue();
    
    Optional<Category> findByNameAndIsActiveTrue(String name);
    
    // Statistics
    @Query("SELECT COUNT(bc) FROM BookCategory bc WHERE bc.category.id = :categoryId")
    long countBooksByCategory(UUID categoryId);
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.name ASC")
    List<Category> findActiveCategoriesOrderByName();
} 