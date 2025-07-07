package com.bookvault.book.repository;

import com.bookvault.book.model.Order;
import com.bookvault.book.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Order entity operations
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    /**
     * Find orders by user ID
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    /**
     * Find orders by user ID with pagination
     */
    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    
    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Find orders by status
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Find orders by status with pagination
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    /**
     * Find orders by multiple statuses
     */
    List<Order> findByStatusIn(List<OrderStatus> statuses);
    
    /**
     * Find orders by user ID and status
     */
    List<Order> findByUserIdAndStatus(UUID userId, OrderStatus status);
    
    /**
     * Find orders by user ID and multiple statuses
     */
    List<Order> findByUserIdAndStatusIn(UUID userId, List<OrderStatus> statuses);
    
    /**
     * Find orders created between dates
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find orders created between dates with pagination
     */
    Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Find orders by customer email
     */
    List<Order> findByCustomerEmailIgnoreCase(String email);
    
    /**
     * Find orders by customer phone
     */
    List<Order> findByCustomerPhone(String phone);
    
    /**
     * Count orders by status
     */
    long countByStatus(OrderStatus status);
    
    /**
     * Count orders by user ID
     */
    long countByUserId(UUID userId);
    
    /**
     * Count orders by user ID and status
     */
    long countByUserIdAndStatus(UUID userId, OrderStatus status);
    
    /**
     * Find recent orders (last 30 days)
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Find orders by total amount range
     */
    @Query("SELECT o FROM Order o WHERE o.finalAmount BETWEEN :minAmount AND :maxAmount ORDER BY o.createdAt DESC")
    List<Order> findByAmountRange(@Param("minAmount") java.math.BigDecimal minAmount, 
                                  @Param("maxAmount") java.math.BigDecimal maxAmount);
    
    /**
     * Calculate total revenue by status
     */
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.status = :status")
    java.math.BigDecimal getTotalRevenueByStatus(@Param("status") OrderStatus status);
    
    /**
     * Calculate total revenue for user
     */
    @Query("SELECT SUM(o.finalAmount) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    java.math.BigDecimal getTotalRevenueByUser(@Param("userId") UUID userId, @Param("status") OrderStatus status);
    
    /**
     * Find top customers by order count
     */
    @Query("SELECT o.userId, COUNT(o) as orderCount FROM Order o GROUP BY o.userId ORDER BY orderCount DESC")
    List<Object[]> findTopCustomersByOrderCount();
    
    /**
     * Find orders that need attention (pending/processing for too long)
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'PROCESSING') AND o.createdAt <= :cutoffDate")
    List<Order> findOrdersNeedingAttention(@Param("cutoffDate") LocalDateTime cutoffDate);
} 