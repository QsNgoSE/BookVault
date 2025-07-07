package com.bookvault.book.repository;

import com.bookvault.book.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for OrderItem entity operations
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    
    /**
     * Find order items by order ID
     */
    List<OrderItem> findByOrderId(UUID orderId);
    
    /**
     * Find order items by book ID
     */
    List<OrderItem> findByBookId(UUID bookId);
    
    /**
     * Count order items by order ID
     */
    long countByOrderId(UUID orderId);
    
    /**
     * Count order items by book ID
     */
    long countByBookId(UUID bookId);
    
    /**
     * Get total quantity for a book across all orders
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.bookId = :bookId")
    Long getTotalQuantityByBookId(@Param("bookId") UUID bookId);
    
    /**
     * Find most popular books by quantity sold
     */
    @Query("SELECT oi.bookId, oi.bookTitle, SUM(oi.quantity) as totalSold FROM OrderItem oi " +
           "GROUP BY oi.bookId, oi.bookTitle ORDER BY totalSold DESC")
    List<Object[]> findMostPopularBooks();
    
    /**
     * Find order items by book title (case insensitive search)
     */
    @Query("SELECT oi FROM OrderItem oi WHERE LOWER(oi.bookTitle) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<OrderItem> findByBookTitleContainingIgnoreCase(@Param("title") String title);
    
    /**
     * Find order items by book author (case insensitive search)
     */
    @Query("SELECT oi FROM OrderItem oi WHERE LOWER(oi.bookAuthor) LIKE LOWER(CONCAT('%', :author, '%'))")
    List<OrderItem> findByBookAuthorContainingIgnoreCase(@Param("author") String author);
    
    /**
     * Find order items with discounts
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.discountAmount > 0")
    List<OrderItem> findOrderItemsWithDiscounts();
    
    /**
     * Calculate total revenue for a specific book
     */
    @Query("SELECT SUM(oi.finalPrice) FROM OrderItem oi WHERE oi.bookId = :bookId")
    java.math.BigDecimal getTotalRevenueByBookId(@Param("bookId") UUID bookId);
} 