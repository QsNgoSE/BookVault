package com.bookvault.book.repository;

import com.bookvault.book.model.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for BookCategory entity
 */
@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, UUID> {
    
    // Find all book categories by book ID
    List<BookCategory> findByBookId(UUID bookId);
    
    // Find all book categories by category ID
    List<BookCategory> findByCategoryId(UUID categoryId);
    
    // Find primary book categories
    List<BookCategory> findByIsPrimaryTrue();
    
    // Find book categories by book ID and primary status
    List<BookCategory> findByBookIdAndIsPrimary(UUID bookId, Boolean isPrimary);
    
    // Count books by category
    @Query("SELECT COUNT(bc) FROM BookCategory bc WHERE bc.category.id = :categoryId")
    long countBooksByCategory(UUID categoryId);
    
    // Count categories by book
    @Query("SELECT COUNT(bc) FROM BookCategory bc WHERE bc.book.id = :bookId")
    long countCategoriesByBook(UUID bookId);
    
    // Delete all book categories for a specific book
    void deleteByBookId(UUID bookId);
    
    // Delete all book categories for a specific category
    void deleteByCategoryId(UUID categoryId);
} 