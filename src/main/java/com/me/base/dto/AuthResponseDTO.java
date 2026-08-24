package com.me.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 * Contains JWT tokens and user information.
 * 
 * @author Base Project
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    
    /**
     * JWT access token for API authentication.
     */
    private String token;
    
    /**
     * Refresh token for obtaining new access tokens.
     */
    private String refreshToken;
    
    /**
     * Token type (always "Bearer").
     */
    private String tokenType = "Bearer";
    
    /**
     * Access token expiration time in seconds.
     */
    private Long expiresIn;
    
    /**
     * User information.
     */
    private UserResponseDTO user;
    
    /**
     * Constructor for backward compatibility (without refresh token).
     */
    public AuthResponseDTO(String token, UserResponseDTO user) {
        this.token = token;
        this.tokenType = "Bearer";
        this.user = user;
    }
}
