package com.me.base.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * RefreshToken entity for managing refresh tokens.
 * <p>
 * Security features:
 * - Tokens are hashed (SHA-256) before storage
 * - Device and IP tracking for security monitoring
 * - Automatic expiration handling
 * <p>
 * Performance features:
 * - Indexed columns for fast lookups
 * - Lazy loading for User relationship
 * 
 * @author Base Project
 * @version 1.0
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_token_hash", columnList = "token_hash"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_expiry_date", columnList = "expiry_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RefreshToken extends BaseEntity {
    
    /**
     * SHA-256 hash of the actual token.
     * The plain token is sent to client but never stored.
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    
    /**
     * User who owns this refresh token.
     * Lazy loaded for performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * Expiration date of the token.
     * Tokens are automatically invalid after this date.
     */
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;
    
    /**
     * Device information (User-Agent).
     * Used for security monitoring and device management.
     */
    @Column(name = "device_info", length = 512)
    private String deviceInfo;
    
    /**
     * IP address from which token was created.
     * Used for detecting suspicious activity.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    /**
     * Last time this token was used for refresh.
     * Helps identify inactive tokens.
     */
    @Column(name = "last_used_at")
    private Instant lastUsedAt;
    
    /**
     * Whether this token has been revoked.
     * Revoked tokens cannot be used even if not expired.
     */
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;
    
    /**
     * Checks if the token is expired.
     * 
     * @return true if token is expired
     */
    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }
}
