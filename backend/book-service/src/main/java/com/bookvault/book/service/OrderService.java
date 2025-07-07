package com.bookvault.book.service;

import com.bookvault.book.dto.*;
import com.bookvault.book.model.*;
import com.bookvault.book.repository.*;
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
@Transactional
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    
    public OrderService(OrderRepository orderRepository, 
                       OrderItemRepository orderItemRepository,
                       BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
    }
    
    /**
     * Create a new order
     */
    public OrderResponse createOrder(UUID userId, CreateOrderRequest request) {
        log.info("Creating order for user: {}", userId);
        
        // Generate unique order number
        String orderNumber = generateOrderNumber();
        
        // Calculate total amount from items
        BigDecimal totalAmount = calculateTotalAmount(request.getItems());
        
        // Create order
        Order order = Order.builder()
                .userId(userId)
                .orderNumber(orderNumber)
                .totalAmount(totalAmount)
                .shippingCost(calculateShippingCost(request.getShippingCountry()))
                .taxAmount(calculateTaxAmount(totalAmount))
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
            // Verify book exists and has sufficient stock
            Book book = bookRepository.findById(itemRequest.getBookId())
                    .orElseThrow(() -> new NotFoundException("Book not found: " + itemRequest.getBookId()));
            
            if (!book.isAvailable()) {
                throw new BadRequestException("Book is not available: " + book.getTitle());
            }
            
            if (book.getStockQuantity() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for book: " + book.getTitle() + 
                        ". Available: " + book.getStockQuantity() + ", Requested: " + itemRequest.getQuantity());
            }
            
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
            
            // Update book stock
            book.decrementStock(itemRequest.getQuantity());
            bookRepository.save(book);
        }
        
        // Save order with items
        savedOrder = orderRepository.save(savedOrder);
        
        log.info("Order created successfully: {}", orderNumber);
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        log.info("Getting order by ID: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        
        return mapToOrderResponse(order);
    }
    
    /**
     * Get order by order number
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        log.info("Getting order by number: {}", orderNumber);
        
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new NotFoundException("Order not found with number: " + orderNumber));
        
        return mapToOrderResponse(order);
    }
    
    /**
     * Get orders by user ID
     */
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(UUID userId, Pageable pageable) {
        log.info("Getting orders for user: {} with pagination", userId);
        
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return orders.map(this::mapToOrderResponse);
    }
    
    /**
     * Get all orders with pagination (admin)
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        log.info("Getting all orders with pagination");
        
        Page<Order> orders = orderRepository.findAll(pageable);
        
        return orders.map(this::mapToOrderResponse);
    }
    
    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
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
        
        // If order is cancelled, restore stock
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            restoreStockForOrder(order);
        }
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order status updated - Order: {}, Old Status: {}, New Status: {}", 
                order.getOrderNumber(), oldStatus, newStatus);
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Cancel order
     */
    public OrderResponse cancelOrder(UUID orderId, String reason) {
        log.info("Cancelling order - ID: {}, Reason: {}", orderId, reason);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        
        if (!order.canBeCancelled()) {
            throw new BadRequestException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        order.cancel(reason);
        order.setUpdatedAt(LocalDateTime.now());
        
        // Restore stock for cancelled order
        restoreStockForOrder(order);
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("Order cancelled: {}", order.getOrderNumber());
        
        return mapToOrderResponse(savedOrder);
    }
    
    /**
     * Update order tracking information
     */
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
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .customerName(order.getCustomerName())
                .orderItems(orderItems)
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
        return "BV-" + timestamp + "-" + String.format("%03d", Integer.parseInt(randomSuffix));
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
        // Simple shipping cost calculation
        if ("United States".equalsIgnoreCase(shippingCountry) || "US".equalsIgnoreCase(shippingCountry)) {
            return new BigDecimal("9.99");
        } else {
            return new BigDecimal("19.99"); // International shipping
        }
    }
    
    /**
     * Calculate tax amount
     */
    private BigDecimal calculateTaxAmount(BigDecimal totalAmount) {
        // Simple 8% tax calculation
        return totalAmount.multiply(new BigDecimal("0.08"));
    }
    
    /**
     * Calculate estimated delivery date
     */
    private LocalDateTime calculateEstimatedDeliveryDate() {
        // Simple calculation: 5-7 business days from now
        return LocalDateTime.now().plusDays(7);
    }
    
    /**
     * Validate status transition
     */
    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // Define valid transitions
        switch (currentStatus) {
            case PENDING:
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case CONFIRMED:
                if (newStatus != OrderStatus.PROCESSING && newStatus != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case PROCESSING:
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case SHIPPED:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new BadRequestException("Invalid status transition from " + currentStatus + " to " + newStatus);
                }
                break;
            case DELIVERED:
            case CANCELLED:
            case REFUNDED:
            case RETURNED:
                throw new BadRequestException("Cannot change status from " + currentStatus);
        }
    }
    
    /**
     * Restore stock for cancelled/returned order
     */
    private void restoreStockForOrder(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Book book = bookRepository.findById(item.getBookId()).orElse(null);
            if (book != null) {
                book.incrementStock(item.getQuantity());
                bookRepository.save(book);
                log.info("Restored {} units of book {} to stock", item.getQuantity(), book.getTitle());
            }
        }
    }
} 