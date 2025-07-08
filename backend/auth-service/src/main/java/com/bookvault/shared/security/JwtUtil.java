package com.bookvault.shared.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bookvault.shared.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWT utility class for token generation and validation
 */
// @Slf4j
@Component
public class JwtUtil {
    
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    
    private final SecretKey secretKey;
    private final long jwtExpirationMs;
    private final long refreshExpirationMs;
    
    public JwtUtil(
            @Value("${jwt.secret:bookvault-secret-key-that-should-be-very-long-and-secure-in-production-environment}") String secret,
            @Value("${jwt.expiration:86400000}") long jwtExpirationMs,
            @Value("${jwt.refresh-expiration:604800000}") long refreshExpirationMs) {
        
        // Ensure the secret is long enough for HMAC-SHA algorithms (at least 256 bits / 32 bytes)
        if (secret.length() < 32) {
            secret = secret + "0123456789abcdef0123456789abcdef".substring(secret.length());
        }
        
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }
    
    /**
     * Generate JWT token for user
     */
    @SuppressWarnings("deprecation")
    public String generateToken(UUID userId, String email, UserRole role) {
        Date expiryDate = Date.from(
                LocalDateTime.now()
                        .plusSeconds(jwtExpirationMs / 1000)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Generate refresh token
     */
    @SuppressWarnings("deprecation")
    public String generateRefreshToken(UUID userId) {
        Date expiryDate = Date.from(
                LocalDateTime.now()
                        .plusSeconds(refreshExpirationMs / 1000)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Extract user ID from token
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.getSubject());
    }
    
    /**
     * Extract email from token
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("email", String.class);
    }
    
    /**
     * Extract role from token
     */
    public UserRole getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        String roleString = claims.get("role", String.class);
        return UserRole.valueOf(roleString);
    }
    
    /**
     * Get expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }
    
    /**
     * Validate token
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }
    
    /**
     * Check if token is refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Get claims from token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
} 