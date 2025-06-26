package com.bookvault.auth.dto;

/**
 * Response DTO for login attempt information
 */
public class LoginAttemptResponse {
    private boolean isBanned;
    private boolean isUserBanned;
    private boolean isIpBanned;
    private long remainingBanTimeMinutes;
    private int failedAttempts;
    private int maxAttempts;
    private String message;
    private String nextAllowedLoginTime;
    
    // Default constructor
    public LoginAttemptResponse() {}
    
    // All args constructor
    public LoginAttemptResponse(boolean isBanned, boolean isUserBanned, boolean isIpBanned,
                               long remainingBanTimeMinutes, int failedAttempts, int maxAttempts,
                               String message, String nextAllowedLoginTime) {
        this.isBanned = isBanned;
        this.isUserBanned = isUserBanned;
        this.isIpBanned = isIpBanned;
        this.remainingBanTimeMinutes = remainingBanTimeMinutes;
        this.failedAttempts = failedAttempts;
        this.maxAttempts = maxAttempts;
        this.message = message;
        this.nextAllowedLoginTime = nextAllowedLoginTime;
    }
    
    // Getters and Setters
    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }
    
    public boolean isUserBanned() { return isUserBanned; }
    public void setUserBanned(boolean userBanned) { isUserBanned = userBanned; }
    
    public boolean isIpBanned() { return isIpBanned; }
    public void setIpBanned(boolean ipBanned) { isIpBanned = ipBanned; }
    
    public long getRemainingBanTimeMinutes() { return remainingBanTimeMinutes; }
    public void setRemainingBanTimeMinutes(long remainingBanTimeMinutes) { this.remainingBanTimeMinutes = remainingBanTimeMinutes; }
    
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getNextAllowedLoginTime() { return nextAllowedLoginTime; }
    public void setNextAllowedLoginTime(String nextAllowedLoginTime) { this.nextAllowedLoginTime = nextAllowedLoginTime; }
    
    public static LoginAttemptResponse userBanned(long remainingTime) {
        LoginAttemptResponse response = new LoginAttemptResponse();
        response.setBanned(true);
        response.setUserBanned(true);
        response.setIpBanned(false);
        response.setRemainingBanTimeMinutes(remainingTime);
        response.setMessage("Your account is temporarily locked due to multiple failed login attempts. Please try again in " + remainingTime + " minute(s).");
        return response;
    }
    
    public static LoginAttemptResponse ipBanned(long remainingTime) {
        LoginAttemptResponse response = new LoginAttemptResponse();
        response.setBanned(true);
        response.setUserBanned(false);
        response.setIpBanned(true);
        response.setRemainingBanTimeMinutes(remainingTime);
        response.setMessage("Too many failed login attempts from this location. Please try again in " + remainingTime + " minute(s).");
        return response;
    }
    
    public static LoginAttemptResponse failedAttempt(int currentAttempts, int maxAttempts) {
        int remaining = maxAttempts - currentAttempts;
        LoginAttemptResponse response = new LoginAttemptResponse();
        response.setBanned(false);
        response.setFailedAttempts(currentAttempts);
        response.setMaxAttempts(maxAttempts);
        response.setMessage("Invalid email or password. You have " + remaining + " attempt(s) remaining before your account is temporarily locked.");
        return response;
    }
    
    public static LoginAttemptResponse success() {
        LoginAttemptResponse response = new LoginAttemptResponse();
        response.setBanned(false);
        response.setUserBanned(false);
        response.setIpBanned(false);
        response.setRemainingBanTimeMinutes(0);
        response.setFailedAttempts(0);
        response.setMessage("Login successful");
        return response;
    }
} 