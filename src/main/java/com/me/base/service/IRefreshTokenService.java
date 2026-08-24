package com.me.base.service;

import com.me.base.entity.RefreshToken;
import com.me.base.entity.User;

/**
 * Service interface for refresh token operations.
 * 
 * @author Base Project
 * @version 1.0
 */
public interface IRefreshTokenService {
    
    /**
     * Creates a new refresh token for a user.
     * 
     * @param user user to create token for
     * @param deviceInfo device/browser information
     * @param ipAddress IP address of the request
     * @return the plain token string (not hashed)
     */
    String createRefreshToken(User user, String deviceInfo, String ipAddress);
    
    /**
     * Verifies and returns a refresh token by its plain value.
     * 
     * @param token plain token string
     * @return RefreshToken entity
     * @throws com.me.base.exception.BusinessException if token invalid/expired/revoked
     */
    RefreshToken verifyRefreshToken(String token);
    
    /**
     * Deletes a specific refresh token.
     * 
     * @param token the token to delete
     */
    void deleteRefreshToken(RefreshToken token);
    
    /**
     * Deletes all refresh tokens for a user (logout all devices).
     * 
     * @param user the user
     */
    void deleteAllByUser(User user);
    
    /**
     * Cleans up expired refresh tokens.
     * Should be called by a scheduled job.
     * 
     * @return number of deleted tokens
     */
    int cleanupExpiredTokens();
}
