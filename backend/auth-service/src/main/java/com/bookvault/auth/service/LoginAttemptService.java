package com.bookvault.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Service to handle login attempt tracking and banning logic
 */
@Service
public class LoginAttemptService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);
    private final RedisTemplate<String, String> redisTemplate;
    
    public LoginAttemptService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    // Constants for ban policies
    private static final int MAX_USER_ATTEMPTS = 3;
    private static final int MAX_IP_ATTEMPTS = 5;
    private static final long USER_BAN_DURATION_MINUTES = 15;
    private static final long IP_BAN_DURATION_MINUTES = 30;
    
    // Redis key prefixes
    private static final String USER_ATTEMPTS_PREFIX = "user_attempts:";
    private static final String IP_ATTEMPTS_PREFIX = "ip_attempts:";
    private static final String USER_BAN_PREFIX = "user_ban:";
    private static final String IP_BAN_PREFIX = "ip_ban:";
    
    /**
     * Check if user is banned
     */
    public boolean isUserBanned(String email) {
        String key = USER_BAN_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * Check if IP is banned
     */
    public boolean isIpBanned(String ipAddress) {
        String key = IP_BAN_PREFIX + ipAddress;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * Record failed login attempt and handle banning logic
     */
    public void recordFailedAttempt(String email, String ipAddress) {
        recordUserFailedAttempt(email);
        recordIpFailedAttempt(ipAddress);
    }
    
    /**
     * Clear failed attempts on successful login
     */
    public void clearFailedAttempts(String email, String ipAddress) {
        clearUserFailedAttempts(email);
        clearIpFailedAttempts(ipAddress);
    }
    
    /**
     * Get remaining ban time for user in minutes
     */
    public long getUserBanTimeRemaining(String email) {
        String key = USER_BAN_PREFIX + email;
        Long expireTime = redisTemplate.getExpire(key, TimeUnit.MINUTES);
        return expireTime != null ? expireTime : 0;
    }
    
    /**
     * Get remaining ban time for IP in minutes
     */
    public long getIpBanTimeRemaining(String ipAddress) {
        String key = IP_BAN_PREFIX + ipAddress;
        Long expireTime = redisTemplate.getExpire(key, TimeUnit.MINUTES);
        return expireTime != null ? expireTime : 0;
    }
    
    private void recordUserFailedAttempt(String email) {
        String attemptsKey = USER_ATTEMPTS_PREFIX + email;
        String banKey = USER_BAN_PREFIX + email;
        
        // Increment attempt count
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        
        // Set expiry for attempts counter (24 hours)
        if (attempts == 1) {
            redisTemplate.expire(attemptsKey, Duration.ofHours(24));
        }
        
        // Check if user should be banned
        if (attempts >= MAX_USER_ATTEMPTS) {
            // Ban user
            redisTemplate.opsForValue().set(banKey, "banned", Duration.ofMinutes(USER_BAN_DURATION_MINUTES));
            // Clear attempt counter
            redisTemplate.delete(attemptsKey);
            logger.warn("User {} banned for {} minutes after {} failed attempts", 
                    email, USER_BAN_DURATION_MINUTES, attempts);
        } else {
            logger.debug("User {} failed attempt {}/{}", email, attempts, MAX_USER_ATTEMPTS);
        }
    }
    
    private void recordIpFailedAttempt(String ipAddress) {
        String attemptsKey = IP_ATTEMPTS_PREFIX + ipAddress;
        String banKey = IP_BAN_PREFIX + ipAddress;
        
        // Increment attempt count
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        
        // Set expiry for attempts counter (24 hours)
        if (attempts == 1) {
            redisTemplate.expire(attemptsKey, Duration.ofHours(24));
        }
        
        // Check if IP should be banned
        if (attempts >= MAX_IP_ATTEMPTS) {
            // Ban IP
            redisTemplate.opsForValue().set(banKey, "banned", Duration.ofMinutes(IP_BAN_DURATION_MINUTES));
            // Clear attempt counter
            redisTemplate.delete(attemptsKey);
            logger.warn("IP {} banned for {} minutes after {} failed attempts", 
                    ipAddress, IP_BAN_DURATION_MINUTES, attempts);
        } else {
            logger.debug("IP {} failed attempt {}/{}", ipAddress, attempts, MAX_IP_ATTEMPTS);
        }
    }
    
    private void clearUserFailedAttempts(String email) {
        String attemptsKey = USER_ATTEMPTS_PREFIX + email;
        redisTemplate.delete(attemptsKey);
        logger.debug("Cleared failed attempts for user: {}", email);
    }
    
    private void clearIpFailedAttempts(String ipAddress) {
        String attemptsKey = IP_ATTEMPTS_PREFIX + ipAddress;
        redisTemplate.delete(attemptsKey);
        logger.debug("Cleared failed attempts for IP: {}", ipAddress);
    }
} 