package com.me.base.repository;

import com.me.base.entity.RefreshToken;
import com.me.base.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RefreshToken entity.
 * <p>
 * Provides CRUD operations and custom query methods for refresh tokens.
 * All queries are optimized with proper indexing.
 * 
 * @author Base Project
 * @version 1.0
 */
@Repository
public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    /**
     * Finds a refresh token by its hash.
     * Uses index on token_hash for fast lookup.
     * 
     * @param tokenHash SHA-256 hash of the token
     * @return Optional containing the token if found
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    
    /**
     * Finds all active (non-revoked, non-expired) tokens for a user.
     * 
     * @param user the user
     * @param now current time to check expiration
     * @return list of active tokens
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user " +
           "AND rt.revoked = false AND rt.expiryDate > :now")
    List<RefreshToken> findActiveTokensByUser(@Param("user") User user, @Param("now") Instant now);
    
    /**
     * Counts active tokens for a user.
     * Used to enforce max tokens per user limit.
     * 
     * @param user the user
     * @param now current time
     * @return count of active tokens
     */
    @Query("SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user " +
           "AND rt.revoked = false AND rt.expiryDate > :now")
    long countActiveTokensByUser(@Param("user") User user, @Param("now") Instant now);
    
    /**
     * Deletes all tokens for a specific user.
     * Used for logout all devices.
     * 
     * @param user the user
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);
    
    /**
     * Deletes all expired tokens.
     * Should be called by scheduled cleanup job.
     * 
     * @param now current time
     * @return number of deleted tokens
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    int deleteByExpiryDateBefore(@Param("now") Instant now);
    
    /**
     * Revokes (soft delete) all tokens for a user.
     * Alternative to hard delete for audit trail.
     * 
     * @param user the user
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllByUser(@Param("user") User user);
    
    /**
     * Deletes oldest tokens for a user when limit is exceeded.
     * Keeps only the most recent N tokens.
     * 
     * @param user the user
     * @param limit max number of tokens to keep
     */
    @Modifying
    @Query(value = "DELETE FROM refresh_tokens WHERE id IN (" +
           "SELECT id FROM refresh_tokens WHERE user_id = :userId " +
           "AND revoked = false ORDER BY created_at DESC OFFSET :limit)", 
           nativeQuery = true)
    void deleteOldestTokensExceedingLimit(@Param("userId") Long userId, @Param("limit") int limit);
}
