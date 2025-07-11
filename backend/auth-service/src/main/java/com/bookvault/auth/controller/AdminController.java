package com.bookvault.auth.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookvault.auth.dto.AdminUserResponse;
import com.bookvault.auth.dto.AdminUserUpdateRequest;
import com.bookvault.auth.service.AdminService;
import com.bookvault.shared.dto.ApiResponse;
import com.bookvault.shared.enums.UserRole;

import jakarta.validation.Valid;

/**
 * Admin controller for user management operations
 * Only accessible by users with ADMIN role
 */
@RestController
@RequestMapping("/api/auth/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    
    private final AdminService adminService;
    
    // Constructor (replacing @RequiredArgsConstructor)
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<AdminService.AdminDashboardStats>> getDashboardStats() {
        log.info("Admin: Getting dashboard statistics");
        
        AdminService.AdminDashboardStats stats = adminService.getDashboardStats();
        
        return ResponseEntity.ok(
            ApiResponse.success(stats, "Dashboard statistics retrieved successfully")
        );
    }
    
    /**
     * Get all users with pagination
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Admin: Getting all users - page: {}, size: {}", page, size);
        
        // Use Java property names directly - JPA handles column mapping
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AdminUserResponse> users = adminService.getAllUsers(pageable);
        
        return ResponseEntity.ok(
            ApiResponse.success(users, "Users retrieved successfully")
        );
    }
    
    /**
     * Get all sellers
     */
    @GetMapping("/sellers")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllSellers() {
        log.info("Admin: Getting all sellers");
        
        List<AdminUserResponse> sellers = adminService.getAllSellers();
        
        return ResponseEntity.ok(
            ApiResponse.success(sellers, "Sellers retrieved successfully")
        );
    }
    
    /**
     * Delete seller (soft delete)
     */
    @DeleteMapping("/sellers/{sellerId}")
    public ResponseEntity<ApiResponse<Void>> deleteSeller(@PathVariable UUID sellerId) {
        log.info("Admin: Deleting seller - ID: {}", sellerId);
        
        adminService.deleteUser(sellerId);
        
        return ResponseEntity.ok(
            ApiResponse.success(null, "Seller deleted successfully")
        );
    }
    
    /**
     * Toggle seller status (activate/suspend)
     */
    @PutMapping("/sellers/{sellerId}/status")
    public ResponseEntity<ApiResponse<AdminUserResponse>> toggleSellerStatus(@PathVariable UUID sellerId) {
        log.info("Admin: Toggling seller status - ID: {}", sellerId);
        
        AdminUserResponse seller = adminService.getUserById(sellerId);
        String action = seller.getIsActive() ? "suspend" : "activate";
        
        AdminUserResponse updatedSeller = adminService.updateUserStatus(sellerId, action);
        
        String message = "activate".equals(action) ? "Seller activated successfully" : "Seller suspended successfully";
        
        return ResponseEntity.ok(
            ApiResponse.success(updatedSeller, message)
        );
    }
    
    /**
     * Get all regular users
     */
    @GetMapping("/regular-users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllRegularUsers() {
        log.info("Admin: Getting all regular users");
        
        List<AdminUserResponse> users = adminService.getAllRegularUsers();
        
        return ResponseEntity.ok(
            ApiResponse.success(users, "Regular users retrieved successfully")
        );
    }
    
    /**
     * Get users by role
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getUsersByRole(@PathVariable UserRole role) {
        log.info("Admin: Getting users by role: {}", role);
        
        List<AdminUserResponse> users = adminService.getUsersByRole(role);
        
        return ResponseEntity.ok(
            ApiResponse.success(users, "Users with role " + role + " retrieved successfully")
        );
    }
    
    /**
     * Get user by ID
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> getUserById(@PathVariable UUID userId) {
        log.info("Admin: Getting user by ID: {}", userId);
        
        AdminUserResponse user = adminService.getUserById(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success(user, "User retrieved successfully")
        );
    }
    
    /**
     * Update user status (activate/suspend)
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserStatus(
            @PathVariable UUID userId,
            @RequestBody Map<String, String> request) {
        
        String action = request.get("action");
        log.info("Admin: Updating user status - ID: {}, Action: {}", userId, action);
        
        AdminUserResponse user = adminService.updateUserStatus(userId, action);
        
        String message = "activate".equals(action) ? "User activated successfully" : "User suspended successfully";
        
        return ResponseEntity.ok(
            ApiResponse.success(user, message)
        );
    }
    
    /**
     * Update user role
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUserRole(
            @PathVariable UUID userId,
            @RequestBody Map<String, UserRole> request) {
        
        UserRole newRole = request.get("role");
        log.info("Admin: Updating user role - ID: {}, New Role: {}", userId, newRole);
        
        AdminUserResponse user = adminService.updateUserRole(userId, newRole);
        
        return ResponseEntity.ok(
            ApiResponse.success(user, "User role updated successfully")
        );
    }
    
    /**
     * Verify user email
     */
    @PutMapping("/users/{userId}/verify")
    public ResponseEntity<ApiResponse<AdminUserResponse>> verifyUser(@PathVariable UUID userId) {
        log.info("Admin: Verifying user - ID: {}", userId);
        
        AdminUserResponse user = adminService.verifyUser(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success(user, "User verified successfully")
        );
    }
    
    /**
     * Reset user password
     */
    @PutMapping("/users/{userId}/reset-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> resetUserPassword(@PathVariable UUID userId) {
        log.info("Admin: Resetting password for user ID: {}", userId);
        
        String tempPassword = adminService.resetUserPassword(userId);
        
        Map<String, String> response = Map.of(
            "message", "Password reset successfully",
            "temporaryPassword", tempPassword,
            "note", "User should change this password on next login"
        );
        
        return ResponseEntity.ok(
            ApiResponse.success(response, "Password reset successfully")
        );
    }
    
    /**
     * Update user details
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody AdminUserUpdateRequest request) {
        
        log.info("Admin: Updating user details - ID: {}", userId);
        
        AdminUserResponse user = adminService.updateUser(userId, request);
        
        return ResponseEntity.ok(
            ApiResponse.success(user, "User updated successfully")
        );
    }
    
    /**
     * Delete user (soft delete)
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        log.info("Admin: Deleting user - ID: {}", userId);
        
        adminService.deleteUser(userId);
        
        return ResponseEntity.ok(
            ApiResponse.success(null, "User deleted successfully")
        );
    }
    
    /**
     * Bulk operations for users
     */
    @PutMapping("/users/bulk-action")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bulkUserAction(
            @RequestBody Map<String, Object> request) {
        
        List<UUID> userIds = (List<UUID>) request.get("userIds");
        String action = (String) request.get("action");
        
        log.info("Admin: Bulk action - Action: {}, Count: {}", action, userIds.size());
        
        int successCount = 0;
        int errorCount = 0;
        
        for (UUID userId : userIds) {
            try {
                switch (action.toLowerCase()) {
                    case "activate":
                    case "suspend":
                        adminService.updateUserStatus(userId, action);
                        successCount++;
                        break;
                    case "verify":
                        adminService.verifyUser(userId);
                        successCount++;
                        break;
                    case "delete":
                        adminService.deleteUser(userId);
                        successCount++;
                        break;
                    default:
                        errorCount++;
                        log.warn("Unknown bulk action: {}", action);
                }
            } catch (Exception e) {
                errorCount++;
                log.error("Error processing bulk action for user {}: {}", userId, e.getMessage());
            }
        }
        
        Map<String, Object> response = Map.of(
            "totalRequested", userIds.size(),
            "successCount", successCount,
            "errorCount", errorCount,
            "action", action
        );
        
        String message = String.format("Bulk %s completed: %d successful, %d errors", 
                                     action, successCount, errorCount);
        
        return ResponseEntity.ok(
            ApiResponse.success(response, message)
        );
    }
    
    /**
     * Search users by email or name
     */
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("Admin: Searching users with query: {}", query);
        
        // This would require a custom repository method for search
        // For now, we'll return empty list with a note
        List<AdminUserResponse> users = List.of();
        
        return ResponseEntity.ok(
            ApiResponse.success(users, "Search functionality to be implemented with custom query methods")
        );
    }
} 