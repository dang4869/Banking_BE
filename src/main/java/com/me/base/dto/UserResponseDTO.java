package com.me.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User responses.
 * Contains user information to be returned in API responses.
 * <p>
 * Note: Password is excluded from this DTO for security reasons.
 * 
 * @author Base Project
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    
    /**
     * User's unique identifier.
     */
    private Long id;
    
    /**
     * Username of the user.
     */
    private String username;
    
    /**
     * Email address of the user.
     */
    private String email;
    
    /**
     * Full name of the user.
     */
    private String fullName;
    
    /**
     * Whether the user account is enabled.
     */
    private Boolean enabled;
    
    /**
     * Phone number of the user.
     */
    private String phoneNumber;

    /**
     * Address of the user.
     */
    private String address;

    /**
     * Role of the user.
     */
    private com.me.base.enums.Role role;

    /**
     * Timestamp when the user was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user was last updated.
     */
    private LocalDateTime updatedAt;
}
