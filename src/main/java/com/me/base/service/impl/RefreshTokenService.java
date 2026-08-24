package com.me.base.service.impl;

import com.me.base.entity.RefreshToken;
import com.me.base.entity.User;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import com.me.base.repository.IRefreshTokenRepository;
import com.me.base.service.IRefreshTokenService;
import com.me.base.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Implementation of IRefreshTokenService.
 * Manages refresh token lifecycle with security and performance optimizations.
 * 
 * @author Base Project
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService implements IRefreshTokenService {
    
    private final IRefreshTokenRepository refreshTokenRepository;
    
    @Value("${jwt.refresh-expiration:604800000}") // 7 days default
    private Long refreshTokenDurationMs;
    
    @Value("${security.max-refresh-tokens-per-user:5}")
    private int maxTokensPerUser;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String createRefreshToken(User user, String deviceInfo, String ipAddress) {
        // Generate plain UUID token
        String plainToken = UUID.randomUUID().toString();
        
        // Hash token for storage
        String tokenHash = HashUtil.sha256(plainToken);
        
        // Check if user has too many active tokens
        long activeTokenCount = refreshTokenRepository.countActiveTokensByUser(user, Instant.now());
        if (activeTokenCount >= maxTokensPerUser) {
            // Delete oldest tokens to make room
            refreshTokenRepository.deleteOldestTokensExceedingLimit(user.getId(), maxTokensPerUser - 1);
            log.info("Cleaned up old tokens for user: {}", user.getUsername());
        }
        
        // Create new refresh token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setDeviceInfo(deviceInfo);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setRevoked(false);
        
        refreshTokenRepository.save(refreshToken);
        
        log.debug("Created refresh token for user: {} from IP: {}", user.getUsername(), ipAddress);
        
        // Return plain token to client (NOT the hash!)
        return plainToken;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        // Hash the incoming token
        String tokenHash = HashUtil.sha256(token);
        
        // Find token by hash
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        
        // Check if revoked
        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED);
        }
        
        // Check if expired
        if (refreshToken.isExpired()) {
            // Delete expired token
            refreshTokenRepository.delete(refreshToken);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        
        // Update last used time
        refreshToken.setLastUsedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);
        
        return refreshToken;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRefreshToken(RefreshToken token) {
        refreshTokenRepository.delete(token);
        log.debug("Deleted refresh token for user: {}", token.getUser().getUsername());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
        log.info("Deleted all refresh tokens for user: {}", user.getUsername());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        if (deleted > 0) {
            log.info("Cleaned up {} expired refresh tokens", deleted);
        }
        return deleted;
    }
}
