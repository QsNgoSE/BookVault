package com.bookvault.order.controller;

import com.bookvault.order.dto.*;
import com.bookvault.order.model.OrderStatus;
import com.bookvault.order.service.OrderService;
import com.bookvault.shared.dto.ApiResponse;
import com.bookvault.shared.dto.PagedResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for order operations
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {
    
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    
    // Constructor
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Create a new order
     */
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateOrderRequest request) {
        
        log.info("Creating order for user: {}", userId);
        
        OrderResponse orderResponse = orderService.createOrder(userId, request);
        
        ApiResponse<OrderResponse> response = ApiResponse.success(orderResponse, "Order created successfully");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID orderId) {
        
        log.info("Getting order by ID: {}", orderId);
        
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        
        ApiResponse<OrderResponse> response = ApiResponse.success(orderResponse, "Order retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(@PathVariable String orderNumber) {
        
        log.info("Getting order by number: {}", orderNumber);
        
        OrderResponse orderResponse = orderService.getOrderByNumber(orderNumber);
        
        ApiResponse<OrderResponse> response = ApiResponse.success(orderResponse, "Order retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get orders for current user
     */
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @RequestHeader("X-User-Id") UUID userId) {
        
        log.info("Getting orders for user: {}", userId);
        
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        
        ApiResponse<List<OrderResponse>> response = ApiResponse.success(orders, "Orders retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get orders for current user with pagination
     */
    @GetMapping("/my-orders/paged")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<OrderResponse>> getMyOrdersPaged(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        log.info("Getting orders for user: {} with pagination", userId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderResponse> orders = orderService.getOrdersByUserId(userId, pageable);
        
        PagedResponse<OrderResponse> response = new PagedResponse<>(
            orders.getContent(),
            orders.getNumber(),
            orders.getSize(),
            orders.getTotalElements(),
            orders.getTotalPages()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all orders (Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<OrderResponse>> getAllOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        log.info("Admin getting all orders with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderResponse> orders = orderService.getAllOrders(pageable);
        
        PagedResponse<OrderResponse> response = new PagedResponse<>(
            orders.getContent(),
            orders.getNumber(),
            orders.getSize(),
            orders.getTotalElements(),
            orders.getTotalPages()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get orders by status (Admin only)
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        
        log.info("Admin getting orders by status: {}", status);
        
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        
        ApiResponse<List<OrderResponse>> response = ApiResponse.success(orders, "Orders retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update order status (Admin only)
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        
        log.info("Admin updating order status - ID: {}, New Status: {}", orderId, request.getStatus());
        
        OrderResponse orderResponse = orderService.updateOrderStatus(orderId, request.getStatus());
        
        ApiResponse<OrderResponse> response = ApiResponse.success(orderResponse, "Order status updated successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel order
     */
    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('SELLER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID orderId,
            @RequestBody CancelOrderRequest request) {
        
        log.info("Cancelling order - ID: {}, Reason: {}", orderId, request.getReason());
        
        OrderResponse orderResponse = orderService.cancelOrder(orderId, request.getReason());
        
        ApiResponse<OrderResponse> response = ApiResponse.success(orderResponse, "Order cancelled successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update tracking information (Admin only)
     */
    @PutMapping("/{orderId}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateTrackingInfo(
            @PathVariable UUID orderId,
            @RequestBody UpdateTrackingRequest request) {
        
        log.info("Admin updating tracking info for order: {}", orderId);
        
        OrderResponse orderResponse = orderService.updateTrackingInfo(
            orderId, 
            request.getTrackingNumber(), 
            request.getEstimatedDeliveryDate()
        );
        
        ApiResponse<OrderResponse> response = ApiResponse.success(orderResponse, "Tracking information updated successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Search orders by customer (Admin only)
     */
    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> searchOrdersByCustomer(
            @RequestParam String query) {
        
        log.info("Admin searching orders by customer: {}", query);
        
        List<OrderResponse> orders = orderService.searchOrdersByCustomer(query);
        
        ApiResponse<List<OrderResponse>> response = ApiResponse.success(orders, "Orders search completed successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get order statistics (Admin only)
     */
    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderService.OrderStatistics>> getOrderStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        log.info("Admin getting order statistics");
        
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate) : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? 
            LocalDateTime.parse(endDate) : LocalDateTime.now();
        
        OrderService.OrderStatistics statistics = orderService.getOrderStatistics(start, end);
        
        ApiResponse<OrderService.OrderStatistics> response = ApiResponse.success(statistics, "Order statistics retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get orders ready for shipping (Admin only)
     */
    @GetMapping("/admin/ready-for-shipping")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersReadyForShipping() {
        
        log.info("Admin getting orders ready for shipping");
        
        List<OrderResponse> orders = orderService.getOrdersReadyForShipping();
        
        ApiResponse<List<OrderResponse>> response = ApiResponse.success(orders, "Orders ready for shipping retrieved successfully");
        
        return ResponseEntity.ok(response);
    }
    
    // DTO classes for request bodies
    
    public static class UpdateOrderStatusRequest {
        private OrderStatus status;
        
        public UpdateOrderStatusRequest() {}
        
        public OrderStatus getStatus() {
            return status;
        }
        
        public void setStatus(OrderStatus status) {
            this.status = status;
        }
    }
    
    public static class CancelOrderRequest {
        private String reason;
        
        public CancelOrderRequest() {}
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
    
    public static class UpdateTrackingRequest {
        private String trackingNumber;
        private LocalDateTime estimatedDeliveryDate;
        
        public UpdateTrackingRequest() {}
        
        public String getTrackingNumber() {
            return trackingNumber;
        }
        
        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }
        
        public LocalDateTime getEstimatedDeliveryDate() {
            return estimatedDeliveryDate;
        }
        
        public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
            this.estimatedDeliveryDate = estimatedDeliveryDate;
        }
    }
} 