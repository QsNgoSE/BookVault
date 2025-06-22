package com.bookvault.book.repository;

import com.bookvault.book.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Book entity
 */
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    
    // Basic queries
    Optional<Book> findByIsbn(String isbn);
    
    List<Book> findBySellerIdAndIsActiveTrue(UUID sellerId);
    
    Page<Book> findByIsActiveTrue(Pageable pageable);
    
    // Search queries
    @Query("SELECT b FROM Book b WHERE " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "b.isActive = true")
    Page<Book> searchBooks(@Param("query") String query, Pageable pageable);
    
    // Category-based queries
    @Query("SELECT DISTINCT b FROM Book b " +
           "JOIN b.bookCategories bc " +
           "JOIN bc.category c " +
           "WHERE c.name = :categoryName AND b.isActive = true")
    Page<Book> findByCategory(@Param("categoryName") String categoryName, Pageable pageable);
    
    @Query("SELECT DISTINCT b FROM Book b " +
           "JOIN b.bookCategories bc " +
           "JOIN bc.category c " +
           "WHERE c.id = :categoryId AND b.isActive = true")
    Page<Book> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);
    
    // Price-based queries
    Page<Book> findByPriceBetweenAndIsActiveTrue(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    // Rating-based queries
    Page<Book> findByRatingGreaterThanEqualAndIsActiveTrue(BigDecimal rating, Pageable pageable);
    
    // Stock queries
    @Query("SELECT b FROM Book b WHERE b.stockQuantity > 0 AND b.isActive = true")
    Page<Book> findInStockBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.stockQuantity <= :threshold AND b.isActive = true")
    List<Book> findLowStockBooks(@Param("threshold") int threshold);
    
    // Featured/Popular books
    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.reviewCount DESC, b.rating DESC")
    Page<Book> findFeaturedBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.rating DESC, b.reviewCount DESC")
    Page<Book> findTopRatedBooks(Pageable pageable);
    
    @Query("SELECT b FROM Book b WHERE b.isActive = true ORDER BY b.createdAt DESC")
    Page<Book> findNewestBooks(Pageable pageable);
    
    // Author-based queries
    Page<Book> findByAuthorContainingIgnoreCaseAndIsActiveTrue(String author, Pageable pageable);
    
    List<Book> findByAuthorAndIsActiveTrue(String author);
    
    // Advanced search with multiple filters
    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN b.bookCategories bc " +
           "LEFT JOIN bc.category c " +
           "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:categoryName IS NULL OR c.name = :categoryName) AND " +
           "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR b.price <= :maxPrice) AND " +
           "(:minRating IS NULL OR b.rating >= :minRating) AND " +
           "b.isActive = true")
    Page<Book> findBooksWithFilters(@Param("title") String title,
                                   @Param("author") String author,
                                   @Param("categoryName") String categoryName,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minRating") BigDecimal minRating,
                                   Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(b) FROM Book b WHERE b.sellerId = :sellerId AND b.isActive = true")
    long countBooksBySeller(@Param("sellerId") UUID sellerId);
    
    @Query("SELECT COUNT(b) FROM Book b WHERE b.isActive = true")
    long countActiveBooks();
    
    @Query("SELECT AVG(b.rating) FROM Book b WHERE b.sellerId = :sellerId AND b.rating IS NOT NULL")
    Optional<BigDecimal> findAverageRatingBySeller(@Param("sellerId") UUID sellerId);
} 