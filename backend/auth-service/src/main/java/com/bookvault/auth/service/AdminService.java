package com.bookvault.auth.service;

import com.bookvault.auth.dto.AdminUserResponse;
import com.bookvault.auth.dto.AdminUserUpdateRequest;
import com.bookvault.auth.model.User;
import com.bookvault.auth.repository.UserRepository;
import com.bookvault.shared.enums.UserRole;
import com.bookvault.shared.exception.BadRequestException;
import com.bookvault.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for admin operations - user management, role assignment, etc.
 */
@Service
public class AdminService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    
    // Constructor (replacing @RequiredArgsConstructor)
    public AdminService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder,
                       LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }
    
    /**
     * Get all users with pagination
     */
    public Page<AdminUserResponse> getAllUsers(Pageable pageable) {
        log.info("Admin: Getting all users with pagination");
        
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToAdminUserResponse);
    }
    
    /**
     * Get users by role
     */
    public List<AdminUserResponse> getUsersByRole(UserRole role) {
        log.info("Admin: Getting users by role: {}", role);
        
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(this::mapToAdminUserResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all sellers (users with SELLER role)
     */
    public List<AdminUserResponse> getAllSellers() {
        return getUsersByRole(UserRole.SELLER);
    }
    
    /**
     * Get all regular users (users with USER role)
     */
    public List<AdminUserResponse> getAllRegularUsers() {
        return getUsersByRole(UserRole.USER);
    }
    
    /**
     * Get user by ID for admin operations
     */
    public AdminUserResponse getUserById(UUID userId) {
        log.info("Admin: Getting user by ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        return mapToAdminUserResponse(user);
    }
    
    /**
     * Update user status (activate/deactivate)
     */
    @Transactional
    public AdminUserResponse updateUserStatus(UUID userId, String action) {
        log.info("Admin: Updating user status - ID: {}, Action: {}", userId, action);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        // Prevent admin from deactivating themselves
        if (user.isAdmin() && "suspend".equals(action)) {
            throw new BadRequestException("Cannot suspend admin user");
        }
        
        switch (action.toLowerCase()) {
            case "activate":
                user.activate();
                log.info("User activated: {}", user.getEmail());
                break;
            case "suspend":
                user.deactivate();
                log.info("User suspended: {}", user.getEmail());
                break;
            default:
                throw new BadRequestException("Invalid action. Use 'activate' or 'suspend'");
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        return mapToAdminUserResponse(savedUser);
    }
    
    /**
     * Update user role
     */
    @Transactional
    public AdminUserResponse updateUserRole(UUID userId, UserRole newRole) {
        log.info("Admin: Updating user role - ID: {}, New Role: {}", userId, newRole);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        log.info("User role updated - Email: {}, Old Role: {}, New Role: {}", 
                user.getEmail(), oldRole, newRole);
        
        return mapToAdminUserResponse(savedUser);
    }
    
    /**
     * Verify user email
     */
    @Transactional
    public AdminUserResponse verifyUser(UUID userId) {
        log.info("Admin: Verifying user - ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        user.verify();
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        log.info("User verified: {}", user.getEmail());
        
        return mapToAdminUserResponse(savedUser);
    }
    
    /**
     * Reset user password (generates new temporary password)
     */
    @Transactional
    public String resetUserPassword(UUID userId) {
        log.info("Admin: Resetting password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        // Generate temporary password
        String tempPassword = generateTemporaryPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);
        
        user.setPassword(encodedPassword);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Clear any login attempts for this user
        loginAttemptService.clearUserAttempts(user.getEmail());
        
        log.info("Password reset for user: {}", user.getEmail());
        
        return tempPassword;
    }
    
    /**
     * Update user details (admin operation)
     */
    @Transactional
    public AdminUserResponse updateUser(UUID userId, AdminUserUpdateRequest request) {
        log.info("Admin: Updating user details - ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        // Check if email is being changed and if it's already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new BadRequestException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }
        
        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        if (request.getIsVerified() != null) {
            user.setIsVerified(request.getIsVerified());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        log.info("User updated: {}", user.getEmail());
        
        return mapToAdminUserResponse(savedUser);
    }
    
    /**
     * Delete user (soft delete by deactivating)
     */
    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Admin: Deleting user - ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        
        // Prevent admin from deleting themselves
        if (user.isAdmin()) {
            throw new BadRequestException("Cannot delete admin user");
        }
        
        // Soft delete - deactivate user instead of hard delete
        user.deactivate();
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("User deleted (deactivated): {}", user.getEmail());
    }
    
    /**
     * Get admin dashboard statistics
     */
    public AdminDashboardStats getDashboardStats() {
        log.info("Admin: Getting dashboard statistics");
        
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        long inactiveUsers = totalUsers - activeUsers;
        long totalSellers = userRepository.countByRole(UserRole.SELLER);
        long activeSellers = userRepository.countByRoleAndIsActiveTrue(UserRole.SELLER);
        long totalAdmins = userRepository.countByRole(UserRole.ADMIN);
        long verifiedUsers = userRepository.countByIsVerifiedTrue();
        
        return AdminDashboardStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .inactiveUsers(inactiveUsers)
                .totalSellers(totalSellers)
                .activeSellers(activeSellers)
                .totalAdmins(totalAdmins)
                .verifiedUsers(verifiedUsers)
                .build();
    }
    
    // Helper methods
    
    /**
     * Map User entity to AdminUserResponse DTO
     */
    private AdminUserResponse mapToAdminUserResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    /**
     * Generate temporary password for password reset
     */
    private String generateTemporaryPassword() {
        // Generate a secure random password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        
        return password.toString();
    }
    
    // Inner class for dashboard statistics
    public static class AdminDashboardStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long inactiveUsers;
        private Long totalSellers;
        private Long activeSellers;
        private Long totalAdmins;
        private Long verifiedUsers;
        
        // Constructor
        public AdminDashboardStats(Long totalUsers, Long activeUsers, Long inactiveUsers,
                                 Long totalSellers, Long activeSellers, Long totalAdmins, Long verifiedUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.totalSellers = totalSellers;
            this.activeSellers = activeSellers;
            this.totalAdmins = totalAdmins;
            this.verifiedUsers = verifiedUsers;
        }
        
        // Builder
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private Long totalUsers;
            private Long activeUsers;
            private Long inactiveUsers;
            private Long totalSellers;
            private Long activeSellers;
            private Long totalAdmins;
            private Long verifiedUsers;
            
            public Builder totalUsers(Long totalUsers) {
                this.totalUsers = totalUsers;
                return this;
            }
            
            public Builder activeUsers(Long activeUsers) {
                this.activeUsers = activeUsers;
                return this;
            }
            
            public Builder inactiveUsers(Long inactiveUsers) {
                this.inactiveUsers = inactiveUsers;
                return this;
            }
            
            public Builder totalSellers(Long totalSellers) {
                this.totalSellers = totalSellers;
                return this;
            }
            
            public Builder activeSellers(Long activeSellers) {
                this.activeSellers = activeSellers;
                return this;
            }
            
            public Builder totalAdmins(Long totalAdmins) {
                this.totalAdmins = totalAdmins;
                return this;
            }
            
            public Builder verifiedUsers(Long verifiedUsers) {
                this.verifiedUsers = verifiedUsers;
                return this;
            }
            
            public AdminDashboardStats build() {
                return new AdminDashboardStats(totalUsers, activeUsers, inactiveUsers,
                                             totalSellers, activeSellers, totalAdmins, verifiedUsers);
            }
        }
        
        // Getters
        public Long getTotalUsers() { return totalUsers; }
        public Long getActiveUsers() { return activeUsers; }
        public Long getInactiveUsers() { return inactiveUsers; }
        public Long getTotalSellers() { return totalSellers; }
        public Long getActiveSellers() { return activeSellers; }
        public Long getTotalAdmins() { return totalAdmins; }
        public Long getVerifiedUsers() { return verifiedUsers; }
    }
} 