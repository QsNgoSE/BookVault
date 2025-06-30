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
    
    // Constants for ban policies - EXACT USER REQUIREMENTS
    private static final int MAX_USER_ATTEMPTS_TEMP_BAN = 3;    // 3 attempts = 15-minute ban
    private static final int MAX_USER_ATTEMPTS_DB_BAN = 5;      // 5 attempts = PERMANENT database ban
    private static final int MAX_IP_ATTEMPTS = 5;              // 5 IP attempts = 30-minute ban
    
    private static final long USER_TEMP_BAN_DURATION_MINUTES = 15;  // 15 minutes for temp ban
    private static final long USER_DB_BAN_DURATION_YEARS = 10;      // 10 years = permanent ban
    private static final long IP_BAN_DURATION_MINUTES = 30;         // 30 minutes for IP ban
    
    // Redis key prefixes
    private static final String USER_ATTEMPTS_PREFIX = "user_attempts:";
    private static final String IP_ATTEMPTS_PREFIX = "ip_attempts:";
    private static final String USER_TEMP_BAN_PREFIX = "user_temp_ban:";
    private static final String USER_DB_BAN_PREFIX = "user_db_ban:";
    private static final String IP_BAN_PREFIX = "ip_ban:";
    
    /**
     * Check if user is temporarily banned (3 attempts = 15 min ban)
     */
    public boolean isUserTemporarilyBanned(String email) {
        String key = USER_TEMP_BAN_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * Check if user is database banned (5 attempts = 24 hour ban)
     */
    public boolean isUserDatabaseBanned(String email) {
        String key = USER_DB_BAN_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * Check if user is banned (either temporary or database ban)
     */
    public boolean isUserBanned(String email) {
        return isUserTemporarilyBanned(email) || isUserDatabaseBanned(email);
    }
    
    /**
     * Get remaining ban time for user in minutes (checks both ban types)
     */
    public long getUserBanTimeRemaining(String email) {
        // Check temporary ban first
        String tempKey = USER_TEMP_BAN_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(tempKey))) {
            Long expireTime = redisTemplate.getExpire(tempKey, TimeUnit.MINUTES);
            return expireTime != null ? expireTime : 0;
        }
        
        // Check database ban
        String dbKey = USER_DB_BAN_PREFIX + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(dbKey))) {
            Long expireTime = redisTemplate.getExpire(dbKey, TimeUnit.MINUTES);
            return expireTime != null ? expireTime : 0;
        }
        
        return 0;
    }
    
    /**
     * Get ban type and details for better error messages
     */
    public BanInfo getUserBanInfo(String email) {
        if (isUserTemporarilyBanned(email)) {
            long remaining = getUserBanTimeRemaining(email);
            return new BanInfo(BanInfo.BanType.TEMPORARY, remaining, 
                "Account temporarily locked due to failed login attempts. Try again in " + remaining + " minutes.");
        }
        
        if (isUserDatabaseBanned(email)) {
            return new BanInfo(BanInfo.BanType.DATABASE, 0, 
                "Account permanently banned due to multiple failed login attempts. Contact administrator for assistance.");
        }
        
        return new BanInfo(BanInfo.BanType.NONE, 0, "Not banned");
    }
    
    // Helper class for ban information
    public static class BanInfo {
        public enum BanType { NONE, TEMPORARY, DATABASE }
        
        private final BanType type;
        private final long remainingMinutes;
        private final String message;
        
        public BanInfo(BanType type, long remainingMinutes, String message) {
            this.type = type;
            this.remainingMinutes = remainingMinutes;
            this.message = message;
        }
        
        public BanType getType() { return type; }
        public long getRemainingMinutes() { return remainingMinutes; }
        public String getMessage() { return message; }
        public boolean isBanned() { return type != BanType.NONE; }
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
     * Get current failed attempt count for user
     */
    public int getUserFailedAttempts(String email) {
        String key = USER_ATTEMPTS_PREFIX + email;
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null ? Integer.parseInt(attempts) : 0;
    }
    
    /**
     * Get current failed attempt count for IP
     */
    public int getIpFailedAttempts(String ipAddress) {
        String key = IP_ATTEMPTS_PREFIX + ipAddress;
        String attempts = redisTemplate.opsForValue().get(key);
        return attempts != null ? Integer.parseInt(attempts) : 0;
    }
    
    /**
     * Get remaining ban time for IP in minutes
     */
    public long getIpBanTimeRemaining(String ipAddress) {
        String key = IP_BAN_PREFIX + ipAddress;
        Long expireTime = redisTemplate.getExpire(key, TimeUnit.MINUTES);
        return expireTime != null ? expireTime : 0;
    }
    
    /**
     * Clear user attempts and ban (for admin password reset)
     */
    public void clearUserAttempts(String email) {
        String attemptsKey = USER_ATTEMPTS_PREFIX + email;
        String tempBanKey = USER_TEMP_BAN_PREFIX + email;
        String dbBanKey = USER_DB_BAN_PREFIX + email;
        
        redisTemplate.delete(attemptsKey);
        redisTemplate.delete(tempBanKey);
        redisTemplate.delete(dbBanKey);
        
        logger.info("Cleared all attempts and bans for user: {}", email);
    }
    
    /**
     * Clear all bans for development/testing purposes
     */
    public void clearAllBans() {
        // This method should only be used in development
        redisTemplate.delete(redisTemplate.keys(USER_TEMP_BAN_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(USER_DB_BAN_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(IP_BAN_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(USER_ATTEMPTS_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(IP_ATTEMPTS_PREFIX + "*"));
        logger.info("All bans and failed attempts cleared");
    }
    
    private void recordUserFailedAttempt(String email) {
        String attemptsKey = USER_ATTEMPTS_PREFIX + email;
        String tempBanKey = USER_TEMP_BAN_PREFIX + email;
        String dbBanKey = USER_DB_BAN_PREFIX + email;
        
        // Check if user is already banned - if so, don't increment attempts
        if (isUserBanned(email)) {
            logger.debug("User {} is already banned, not incrementing attempts", email);
            return;
        }
        
        // Increment attempt count
        Long attempts = redisTemplate.opsForValue().increment(attemptsKey);
        
        // Set expiry for attempts counter (24 hours)
        if (attempts == 1) {
            redisTemplate.expire(attemptsKey, Duration.ofHours(24));
        }
        
        // Apply progressive banning policy
        if (attempts >= MAX_USER_ATTEMPTS_DB_BAN) {
            // 5 attempts = PERMANENT database ban (10 years = effectively permanent)
            redisTemplate.opsForValue().set(dbBanKey, "PERMANENT_BAN", Duration.ofDays(USER_DB_BAN_DURATION_YEARS * 365));
            redisTemplate.delete(attemptsKey);
            redisTemplate.delete(tempBanKey); // Clear temp ban if exists
            logger.warn("User {} PERMANENTLY BANNED after {} failed attempts - requires admin intervention", 
                    email, attempts);
        } else if (attempts >= MAX_USER_ATTEMPTS_TEMP_BAN) {
            // 3 attempts = 15-minute temporary ban
            redisTemplate.opsForValue().set(tempBanKey, "banned", Duration.ofMinutes(USER_TEMP_BAN_DURATION_MINUTES));
            // DON'T delete attempts key - keep it for potential permanent ban
            logger.warn("User {} TEMPORARILY banned for {} minutes after {} failed attempts", 
                    email, USER_TEMP_BAN_DURATION_MINUTES, attempts);
        } else {
            logger.debug("User {} failed attempt {}/{} (temp ban at {})", 
                    email, attempts, MAX_USER_ATTEMPTS_DB_BAN, MAX_USER_ATTEMPTS_TEMP_BAN);
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
        
        // Check if IP should be banned (5 attempts = 30-minute ban)
        if (attempts >= MAX_IP_ATTEMPTS) {
            // Ban IP for 30 minutes
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