package com.bookvault.auth.repository;

import com.bookvault.auth.model.User;
import com.bookvault.shared.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Simple User repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByIsActiveTrue();
    
    long countByRole(UserRole role);
    
    long countByIsActiveTrue();
    
    long countByRoleAndIsActiveTrue(UserRole role);
    
    long countByIsVerifiedTrue();
} 