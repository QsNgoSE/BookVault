package com.bookvault.order.service;

import com.bookvault.order.dto.*;
import com.bookvault.order.model.*;
import com.bookvault.order.repository.OrderRepository;
import com.bookvault.order.repository.OrderItemRepository;
import com.bookvault.shared.exception.BadRequestException;
import com.bookvault.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for order operations - creation, management, tracking, etc.
 */
@Service
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    
    // Constructor (replacing @RequiredArgsConstructor)
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }
    
    /**
     * Create a new order
     */
    @Transactional
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        log.info("Creating order for user: {}", userId);
        
        // Generate unique order number
        String orderNumber = generateOrderNumber();
        
        // Calculate totals
        BigDecimal totalAmount = calculateTotalAmount(request.getItems());
        BigDecimal shippingCost = calculateShippingCost(request.getShippingCountry());
        BigDecimal taxAmount = calculateTaxAmount(totalAmount, request.getShippingCountry());
        
        // Create order entity
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .totalAmount(totalAmount)
                .shippingCost(shippingCost)
                .taxAmount(taxAmount)
                .shippingAddress(request.getShippingAddress())
                .shippingCity(request.getShippingCity())
                .shippingState(request.getShippingState())
                .shippingPostalCode(request.getShippingPostalCode())
                .shippingCountry(request.getShippingCountry())
                .paymentMethod(request.getPaymentMethod())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .customerName(request.getCustomerName())
                .orderNotes(request.getOrderNotes())
                .build();
        
        // Calculate final amount including shipping and tax
        order.calculateFinalAmount();
        
        // Save order first
        Order savedOrder = orderRepository.save(order);
        
        // Create and save order items
        for (OrderItemRequest itemRequest : request.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .bookId(itemRequest.getBookId())
                    .bookTitle(itemRequest.getBookTitle())
                    .bookAuthor(itemRequest.getBookAuthor())
                    .bookIsbn(itemRequest.getBookIsbn())
                    .bookImageUrl(itemRequest.getBookImageUrl())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(itemRequest.getUnitPrice())
                    .discountAmount(itemRequest.getDiscountAmount())
                    .build();
            
            // Calculate prices
            orderItem.calculateTotalPrice();
            
            // Associate with order
            savedOrder.addOrderItem(orderItem);
        }
        
        // Save order with items
        savedOrder = orderRepository.save(savedOrder);
        
        log.info("Order created successfully: {}", orderNumber);
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Get order by ID
     */
    public OrderResponse getOrderById(UUID orderId) {
        log.info("Getting order by ID: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        
        return mapToOrderResponse(order);
    }
    
    /**
     * Get order by order number
     */
    public OrderResponse getOrderByNumber(String orderNumber) {
        log.info("Getting order by number: {}", orderNumber);
        
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("Order not found with number: " + orderNumber));
        
        return mapToOrderResponse(order);
    }
    
    /**
     * Get orders by user ID
     */
    public List<OrderResponse> getOrdersByUserId(UUID userId) {
        log.info("Getting orders for user: {}", userId);
        
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get orders by user ID with pagination
     */
    public Page<OrderResponse> getOrdersByUserId(UUID userId, Pageable pageable) {
        log.info("Getting orders for user: {} with pagination", userId);
        
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return orders.map(this::mapToOrderResponse);
    }
    
    /**
     * Get all orders with pagination (admin)
     */
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        log.info("Getting all orders with pagination");
        
        Page<Order> orders = orderRepository.findAll(pageable);
        
        return orders.map(this::mapToOrderResponse);
    }
    
    /**
     * Get orders by status
     */
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        log.info("Getting orders by status: {}", status);
        
        List<Order> orders = orderRepository.findByStatus(status);
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update order status
     */
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        log.info("Updating order status - ID: {}, New Status: {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        
        OrderStatus oldStatus = order.getStatus();
        
        // Validate status transition
        validateStatusTransition(oldStatus, newStatus);
        
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        
        // Handle specific status transitions
        if (newStatus == OrderStatus.SHIPPED && order.getTrackingNumber() == null) {
            order.setTrackingNumber(generateTrackingNumber());
            order.setEstimatedDeliveryDate(calculateEstimatedDeliveryDate());
        }
        
        if (newStatus == OrderStatus.DELIVERED) {
            order.markAsDelivered();
        }
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order status updated - Order: {}, Old Status: {}, New Status: {}", 
                order.getOrderNumber(), oldStatus, newStatus);
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Cancel order
     */
    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String reason) {
        log.info("Cancelling order - ID: {}, Reason: {}", orderId, reason);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        
        if (!order.canBeCancelled()) {
            throw new BadRequestException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.cancel(reason);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order cancelled: {}", order.getOrderNumber());
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Update order tracking information
     */
    @Transactional
    public OrderResponse updateTrackingInfo(UUID orderId, String trackingNumber, LocalDateTime estimatedDeliveryDate) {
        log.info("Updating tracking info for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        
        order.setTrackingNumber(trackingNumber);
        order.setEstimatedDeliveryDate(estimatedDeliveryDate);
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("Tracking info updated for order: {}", order.getOrderNumber());
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Search orders by customer
     */
    public List<OrderResponse> searchOrdersByCustomer(String query) {
        log.info("Searching orders by customer: {}", query);
        
        List<Order> orders = orderRepository.searchOrdersByCustomer(query);
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get order statistics
     */
    public OrderStatistics getOrderStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting order statistics from {} to {}", startDate, endDate);
        
        Object[] stats = orderRepository.getOrderStatistics(startDate, endDate);
        
        Long totalOrders = stats[0] != null ? ((Number) stats[0]).longValue() : 0L;
        BigDecimal totalRevenue = stats[1] != null ? (BigDecimal) stats[1] : BigDecimal.ZERO;
        BigDecimal averageOrderValue = stats[2] != null ? (BigDecimal) stats[2] : BigDecimal.ZERO;
        
        return OrderStatistics.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .periodStart(startDate)
                .periodEnd(endDate)
                .build();
    }
    
    /**
     * Get orders ready for shipping
     */
    public List<OrderResponse> getOrdersReadyForShipping() {
        log.info("Getting orders ready for shipping");
        
        List<Order> orders = orderRepository.findOrdersReadyForShipping();
        
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    
    /**
     * Map Order entity to OrderResponse DTO
     */
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> orderItems = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderNumber(order.getOrderNumber())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingCost(order.getShippingCost())
                .taxAmount(order.getTaxAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingState(order.getShippingState())
                .shippingPostalCode(order.getShippingPostalCode())
                .shippingCountry(order.getShippingCountry())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paymentTransactionId(order.getPaymentTransactionId())
                .orderItems(orderItems)
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .customerName(order.getCustomerName())
                .trackingNumber(order.getTrackingNumber())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .cancellationReason(order.getCancellationReason())
                .orderNotes(order.getOrderNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    /**
     * Map OrderItem entity to OrderItemResponse DTO
     */
    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .bookId(orderItem.getBookId())
                .bookTitle(orderItem.getBookTitle())
                .bookAuthor(orderItem.getBookAuthor())
                .bookIsbn(orderItem.getBookIsbn())
                .bookImageUrl(orderItem.getBookImageUrl())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .discountAmount(orderItem.getDiscountAmount())
                .finalPrice(orderItem.getFinalPrice())
                .createdAt(orderItem.getCreatedAt())
                .updatedAt(orderItem.getUpdatedAt())
                .build();
    }
    
    /**
     * Generate unique order number
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.valueOf((int) (Math.random() * 1000));
        return "ORD-" + timestamp + "-" + String.format("%03d", Integer.parseInt(randomSuffix));
    }
    
    /**
     * Generate tracking number
     */
    private String generateTrackingNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.valueOf((int) (Math.random() * 100000));
        return "TRK-" + timestamp + "-" + String.format("%05d", Integer.parseInt(randomSuffix));
    }
    
    /**
     * Calculate total amount from order items
     */
    private BigDecimal calculateTotalAmount(List<OrderItemRequest> items) {
        return items.stream()
                .map(OrderItemRequest::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calculate shipping cost based on destination
     */
    private BigDecimal calculateShippingCost(String shippingCountry) {
        // Simple shipping calculation - can be enhanced with more complex logic
        if ("US".equalsIgnoreCase(shippingCountry) || "USA".equalsIgnoreCase(shippingCountry)) {
            return new BigDecimal("5.99");
        } else {
            return new BigDecimal("12.99");
        }
    }
    
    /**
     * Calculate tax amount
     */
    private BigDecimal calculateTaxAmount(BigDecimal totalAmount, String shippingCountry) {
        // Simple tax calculation - can be enhanced with real tax rates
        if ("US".equalsIgnoreCase(shippingCountry) || "USA".equalsIgnoreCase(shippingCountry)) {
            return totalAmount.multiply(new BigDecimal("0.08")); // 8% tax
        } else {
            return BigDecimal.ZERO; // No tax for international orders for simplicity
        }
    }
    
    /**
     * Calculate estimated delivery date
     */
    private LocalDateTime calculateEstimatedDeliveryDate() {
        // Simple calculation - 5-7 business days from now
        return LocalDateTime.now().plusDays(7);
    }
    
    /**
     * Validate status transition
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return; // No change
        }
        
        OrderStatus[] allowedTransitions = currentStatus.getNextPossibleStatuses();
        for (OrderStatus allowed : allowedTransitions) {
            if (allowed == newStatus) {
                return; // Valid transition
            }
        }
        
        throw new BadRequestException(
            String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
        );
    }
    
    // Inner class for order statistics
    public static class OrderStatistics {
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
        private LocalDateTime periodStart;
        private LocalDateTime periodEnd;
        
        // Constructor
        public OrderStatistics(Long totalOrders, BigDecimal totalRevenue, BigDecimal averageOrderValue,
                             LocalDateTime periodStart, LocalDateTime periodEnd) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
            this.periodStart = periodStart;
            this.periodEnd = periodEnd;
        }
        
        // Builder
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Long totalOrders;
            private BigDecimal totalRevenue;
            private BigDecimal averageOrderValue;
            private LocalDateTime periodStart;
            private LocalDateTime periodEnd;
            
            public Builder totalOrders(Long totalOrders) {
                this.totalOrders = totalOrders;
                return this;
            }
            
            public Builder totalRevenue(BigDecimal totalRevenue) {
                this.totalRevenue = totalRevenue;
                return this;
            }
            
            public Builder averageOrderValue(BigDecimal averageOrderValue) {
                this.averageOrderValue = averageOrderValue;
                return this;
            }
            
            public Builder periodStart(LocalDateTime periodStart) {
                this.periodStart = periodStart;
                return this;
            }
            
            public Builder periodEnd(LocalDateTime periodEnd) {
                this.periodEnd = periodEnd;
                return this;
            }
            
            public OrderStatistics build() {
                return new OrderStatistics(totalOrders, totalRevenue, averageOrderValue, periodStart, periodEnd);
            }
        }
        
        // Getters
        public Long getTotalOrders() { return totalOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public LocalDateTime getPeriodStart() { return periodStart; }
        public LocalDateTime getPeriodEnd() { return periodEnd; }
    }
} 