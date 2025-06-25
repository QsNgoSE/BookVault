package com.bookvault.auth.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookvault.auth.dto.AuthResponse;
import com.bookvault.auth.dto.LoginRequest;
import com.bookvault.auth.dto.RegisterRequest;
import com.bookvault.auth.dto.UpdateProfileRequest;
import com.bookvault.auth.dto.UserProfileResponse;
import com.bookvault.auth.model.User;
import com.bookvault.auth.repository.UserRepository;
import com.bookvault.shared.exception.BadRequestException;
import com.bookvault.shared.exception.NotFoundException;
import com.bookvault.shared.security.JwtUtil;

/**
 * Simple Authentication Service
 */
@Service
// @RequiredArgsConstructor
// @Slf4j
@Transactional
public class AuthService {
    
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;
    
    // Constructor (replacing @RequiredArgsConstructor)
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      AuthenticationManager authenticationManager, JwtUtil jwtUtil, 
                      LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }
    
    // Register new user
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role(request.getRole())
                .isActive(true)
                .isVerified(true) // Auto-verify for simplicity
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        
        log.info("New user registered: {} with role {}", savedUser.getEmail(), savedUser.getRole());
        
        return AuthResponse.of(token, savedUser.getId(), savedUser.getEmail(), 
                             savedUser.getFirstName(), savedUser.getLastName(), savedUser.getRole());
    }
    
    // Login user
    public AuthResponse login(LoginRequest request, String clientIpAddress) {
        // Check if user is banned
        if (loginAttemptService.isUserBanned(request.getEmail())) {
            long remainingTime = loginAttemptService.getUserBanTimeRemaining(request.getEmail());
            throw new BadRequestException("User account is temporarily banned. Try again in " + remainingTime + " minutes.");
        }
        
        // Check if IP is banned
        if (loginAttemptService.isIpBanned(clientIpAddress)) {
            long remainingTime = loginAttemptService.getIpBanTimeRemaining(clientIpAddress);
            throw new BadRequestException("IP address is temporarily banned. Try again in " + remainingTime + " minutes.");
        }
        
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("User not found: " + request.getEmail()));
            
            // Clear failed attempts on successful login
            loginAttemptService.clearFailedAttempts(request.getEmail(), clientIpAddress);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());
            
            log.info("User logged in: {}", user.getEmail());
            
            return AuthResponse.of(token, user.getId(), user.getEmail(), 
                                 user.getFirstName(), user.getLastName(), user.getRole());
        } catch (BadCredentialsException e) {
            // Record failed login attempt
            loginAttemptService.recordFailedAttempt(request.getEmail(), clientIpAddress);
            throw new BadRequestException("Invalid email or password");
        }
    }
    
    // Get user profile
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    // Update user profile
    public UserProfileResponse updateUserProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        
        // Update fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        
        User savedUser = userRepository.save(user);
        log.info("Updated user profile: {}", savedUser.getEmail());
        
        return UserProfileResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .phone(savedUser.getPhone())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .isVerified(savedUser.getIsVerified())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }
    
    // Validate token and get user info
    @Transactional(readOnly = true)
    public UserProfileResponse validateToken(String token) {
        if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
            throw new BadRequestException("Invalid or expired token");
        }
        
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
} 