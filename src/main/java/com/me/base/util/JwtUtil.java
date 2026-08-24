package com.me.base.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT token operations.
 * Handles token generation, validation, and extraction of claims.
 * <p>
 * JWT Structure:
 * - sub: User ID (unique identifier)
 * - email: User email
 * - username: Username
 * 
 * @author Base Project
 * @version 1.0
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * Generates a secret key from the configured secret string.
     * 
     * @return SecretKey for signing tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * Extracts the user ID from the JWT token (from subject claim).
     * 
     * @param token JWT token
     * @return user ID from token
     */
    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return Long.parseLong(subject);
    }
    
    /**
     * Extracts the email from the JWT token (from custom claim).
     * 
     * @param token JWT token
     * @return email from token
     */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }
    
    /**
     * Extracts the username from the JWT token (from custom claim).
     * 
     * @param token JWT token
     * @return username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }
    
    /**
     * Extracts the expiration date from the JWT token.
     * 
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extracts a specific claim from the JWT token.
     * 
     * @param token JWT token
     * @param claimsResolver function to extract the claim
     * @param <T> type of the claim
     * @return extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Extracts all claims from the JWT token.
     * 
     * @param token JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Checks if the token is expired.
     * 
     * @param token JWT token
     * @return true if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Generates a JWT token with user ID and additional user information.
     * 
     * @param userId user ID (will be subject)
     * @param email user email
     * @param username username
     * @return generated JWT token
     */
    public String generateToken(Long userId, String email, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("username", username);
        return createToken(claims, userId.toString());
    }
    
    /**
     * Creates a JWT token with the specified claims and subject.
     * 
     * @param claims additional claims to include
     * @param subject subject (user ID) of the token
     * @return generated JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Validates the JWT token against the user ID.
     * 
     * @param token JWT token
     * @param userId user ID to validate against
     * @return true if token is valid
     */
    public Boolean validateToken(String token, Long userId) {
        final Long tokenUserId = extractUserId(token);
        return (tokenUserId.equals(userId) && !isTokenExpired(token));
    }
    
    /**
     * Validates the JWT token against UserDetails.
     * This is kept for compatibility with Spring Security.
     * Note: UserDetails.getUsername() should return the user ID as string.
     * 
     * @param token JWT token
     * @param userDetails user details to validate against
     * @return true if token is valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final Long userId = extractUserId(token);
        // UserDetails username should contain userId
        return (userId.toString().equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
