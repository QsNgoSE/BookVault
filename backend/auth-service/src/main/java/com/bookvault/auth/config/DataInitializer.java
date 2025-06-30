package com.bookvault.auth.config;

import com.bookvault.auth.model.User;
import com.bookvault.auth.repository.UserRepository;
import com.bookvault.shared.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data initializer to create default admin account and other initial data
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) throws Exception {
        initializeDefaultAdmin();
    }
    
    /**
     * Create default admin account if no admin exists
     */
    private void initializeDefaultAdmin() {
        // Check if any admin user already exists
        long adminCount = userRepository.countByRole(UserRole.ADMIN);
        
        if (adminCount == 0) {
            log.info("No admin users found. Creating default admin account...");
            
            User defaultAdmin = User.builder()
                    .email("admin@bookvault.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstName("Admin")
                    .lastName("User")
                    .phone("+1234567890")
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .isVerified(true)
                    .build();
            
            userRepository.save(defaultAdmin);
            
            log.info("✅ Default admin account created successfully!");
            log.info("📧 Email: admin@bookvault.com");
            log.info("🔑 Password: admin123");
            log.info("⚠️  Please change the default password after first login!");
        } else {
            log.info("Admin users already exist ({}). Skipping default admin creation.", adminCount);
        }
    }
} 