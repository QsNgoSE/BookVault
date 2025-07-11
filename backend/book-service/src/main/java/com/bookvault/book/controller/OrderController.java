package com.bookvault.book.controller;

import com.bookvault.book.dto.*;
import com.bookvault.book.model.OrderStatus;
import com.bookvault.book.service.OrderService;
import com.bookvault.shared.dto.ApiResponse;
import com.bookvault.shared.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for order operations
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for order operations")
public class OrderController {
    
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Create a new order
     */
    @PostMapping
    @Operation(summary = "Create order", description = "Create a new order")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Parameter(description = "User ID") @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody CreateOrderRequest request) {
        
        log.info("Creating order for user: {}", userId);
        
        OrderResponse order = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(order, "Order created successfully"));
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Get order details by order ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @Parameter(description = "Order ID") @PathVariable UUID orderId,
            @Parameter(description = "User ID") @RequestAttribute("userId") UUID userId) {
        
        log.info("Getting order: {} for user: {}", orderId, userId);
        
        OrderResponse order = orderService.getOrderById(orderId);
        
        // Ensure user can only access their own orders (unless admin)
        if (!order.getUserId().equals(userId) && !isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied to this order"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by number", description = "Get order details by order number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(
            @Parameter(description = "Order number") @PathVariable String orderNumber,
            @Parameter(description = "User ID") @RequestAttribute("userId") UUID userId) {
        
        log.info("Getting order by number: {} for user: {}", orderNumber, userId);
        
        OrderResponse order = orderService.getOrderByNumber(orderNumber);
        
        // Ensure user can only access their own orders (unless admin)
        if (!order.getUserId().equals(userId) && !isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied to this order"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    /**
     * Get user's orders
     */
    @GetMapping("/my-orders")
    @Operation(summary = "Get user orders", description = "Get all orders for the authenticated user")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getUserOrders(
            @Parameter(description = "User ID") @RequestAttribute("userId") UUID userId) {
        
        log.info("Getting orders for user: {}", userId);
        
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
    }
    
    /**
     * Get user's orders with pagination
     */
    @GetMapping("/my-orders/paged")
    @Operation(summary = "Get user orders with pagination", description = "Get paginated orders for the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getUserOrdersPaged(
            @Parameter(description = "User ID") @RequestAttribute("userId") UUID userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting paginated orders for user: {}", userId);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderResponse> ordersPage = orderService.getOrdersByUserId(userId, pageable);
        
        PagedResponse<OrderResponse> pagedResponse = PagedResponse.<OrderResponse>builder()
                .content(ordersPage.getContent())
                .page(ordersPage.getNumber())
                .size(ordersPage.getSize())
                .totalElements(ordersPage.getTotalElements())
                .totalPages(ordersPage.getTotalPages())
                .first(ordersPage.isFirst())
                .last(ordersPage.isLast())
                .numberOfElements(ordersPage.getNumberOfElements())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Orders retrieved successfully"));
    }
    
    /**
     * Cancel order
     */
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable UUID orderId,
            @Parameter(description = "User ID") @RequestAttribute("userId") UUID userId,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason) {
        
        log.info("Cancelling order: {} for user: {}", orderId, userId);
        
        // First check if user owns this order
        OrderResponse existingOrder = orderService.getOrderById(orderId);
        if (!existingOrder.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied to this order"));
        }
        
        String cancellationReason = reason != null ? reason : "Cancelled by customer";
        OrderResponse order = orderService.cancelOrder(orderId, cancellationReason);
        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled successfully"));
    }
    
    // Admin endpoints
    
    /**
     * Get all orders (Admin only)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders", description = "Get all orders with pagination (Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<OrderResponse>>> getAllOrders(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Admin: Getting all orders with pagination");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<OrderResponse> ordersPage = orderService.getAllOrders(pageable);
        
        PagedResponse<OrderResponse> pagedResponse = PagedResponse.<OrderResponse>builder()
                .content(ordersPage.getContent())
                .page(ordersPage.getNumber())
                .size(ordersPage.getSize())
                .totalElements(ordersPage.getTotalElements())
                .totalPages(ordersPage.getTotalPages())
                .first(ordersPage.isFirst())
                .last(ordersPage.isLast())
                .numberOfElements(ordersPage.getNumberOfElements())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Orders retrieved successfully"));
    }
    
    /**
     * Get orders by status (Admin only)
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get orders by status", description = "Get orders by status (Admin only)")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByStatus(
            @Parameter(description = "Order status") @PathVariable OrderStatus status) {
        
        log.info("Admin: Getting orders by status: {}", status);
        
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(orders, "Orders retrieved successfully"));
    }
    
    /**
     * Update order status (Admin only)
     */
    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Update order status (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable UUID orderId,
            @Parameter(description = "New status") @RequestParam OrderStatus status) {
        
        log.info("Admin: Updating order status - ID: {}, Status: {}", orderId, status);
        
        OrderResponse order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated successfully"));
    }
    
    /**
     * Update tracking information (Admin only)
     */
    @PutMapping("/admin/{orderId}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update tracking info", description = "Update order tracking information (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> updateTrackingInfo(
            @Parameter(description = "Order ID") @PathVariable UUID orderId,
            @Parameter(description = "Tracking number") @RequestParam String trackingNumber,
            @Parameter(description = "Estimated delivery date") @RequestParam(required = false) String estimatedDeliveryDate) {
        
        log.info("Admin: Updating tracking info for order: {}", orderId);
        
        LocalDateTime deliveryDate = null;
        if (estimatedDeliveryDate != null && !estimatedDeliveryDate.isEmpty()) {
            try {
                deliveryDate = LocalDateTime.parse(estimatedDeliveryDate);
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid date format. Use ISO format: yyyy-MM-ddTHH:mm:ss"));
            }
        }
        
        OrderResponse order = orderService.updateTrackingInfo(orderId, trackingNumber, deliveryDate);
        return ResponseEntity.ok(ApiResponse.success(order, "Tracking information updated successfully"));
    }
    
    /**
     * Get order by ID (Admin only)
     */
    @GetMapping("/admin/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get order by ID (Admin)", description = "Get order details by ID (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByIdAdmin(
            @Parameter(description = "Order ID") @PathVariable UUID orderId) {
        
        log.info("Admin: Getting order: {}", orderId);
        
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }
    
    /**
     * Cancel order (Admin only)
     */
    @PutMapping("/admin/{orderId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancel order (Admin)", description = "Cancel an order (Admin only)")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrderAdmin(
            @Parameter(description = "Order ID") @PathVariable UUID orderId,
            @Parameter(description = "Cancellation reason") @RequestParam(required = false) String reason) {
        
        log.info("Admin: Cancelling order: {}", orderId);
        
        String cancellationReason = reason != null ? reason : "Cancelled by admin";
        OrderResponse order = orderService.cancelOrder(orderId, cancellationReason);
        return ResponseEntity.ok(ApiResponse.success(order, "Order cancelled successfully"));
    }
    
    /**
     * Get order tracking information
     */
    @GetMapping("/{orderId}/tracking")
    @Operation(summary = "Get order tracking", description = "Get tracking information for an order")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderTracking(
            @Parameter(description = "Order ID") @PathVariable UUID orderId,
            @Parameter(description = "User ID") @RequestAttribute("userId") UUID userId) {
        
        log.info("Getting tracking info for order: {} by user: {}", orderId, userId);
        
        OrderResponse order = orderService.getOrderById(orderId);
        
        // Check if user owns this order or is admin
        if (!order.getUserId().equals(userId) && !isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied to this order"));
        }
        
        // Return the order with tracking information
        return ResponseEntity.ok(ApiResponse.success(order, "Order tracking information retrieved successfully"));
    }
    
    /**
     * Check if current user has admin role
     */
    private boolean isCurrentUserAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getAuthorities() == null) {
                return false;
            }
            
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        } catch (Exception e) {
            log.error("Error checking admin role: {}", e.getMessage());
            return false;
        }
    }
} 