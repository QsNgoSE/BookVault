package com.bookvault.order.repository;

import com.bookvault.order.model.Order;
import com.bookvault.order.model.OrderStatus;
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
    List<Order> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    
    /**
     * Check if order number exists
     */
    boolean existsByOrderNumber(String orderNumber);
    
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
     * Find recent orders (last N days)
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :sinceDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("sinceDate") LocalDateTime sinceDate);
    
    /**
     * Find orders with total amount greater than specified value
     */
    @Query("SELECT o FROM Order o WHERE o.finalAmount >= :minAmount ORDER BY o.finalAmount DESC")
    List<Order> findOrdersWithMinAmount(@Param("minAmount") java.math.BigDecimal minAmount);
    
    /**
     * Find pending orders older than specified time
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :beforeDate")
    List<Order> findPendingOrdersOlderThan(@Param("status") OrderStatus status, @Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * Get order statistics by date range
     */
    @Query("SELECT COUNT(o), SUM(o.finalAmount), AVG(o.finalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    Object[] getOrderStatistics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders that can be shipped (confirmed status)
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'CONFIRMED' AND o.paymentStatus = 'COMPLETED' ORDER BY o.createdAt ASC")
    List<Order> findOrdersReadyForShipping();
    
    /**
     * Search orders by customer name or email
     */
    @Query("SELECT o FROM Order o WHERE LOWER(o.customerName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(o.customerEmail) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY o.createdAt DESC")
    List<Order> searchOrdersByCustomer(@Param("query") String query);
    
    /**
     * Find orders by tracking number
     */
    Optional<Order> findByTrackingNumber(String trackingNumber);
} 