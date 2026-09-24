package com.me.base.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User entity representing application users.
 * Contains user authentication and profile information.
 * <p>
 * Extends BaseEntity to inherit id and audit fields (createdAt, updatedAt).
 * 
 * @author Base Project
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User extends BaseEntity {

    /**
     * Unique username for authentication.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Unique email address.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Hashed password for authentication.
     * Should never be returned in API responses.
     */
    @Column(nullable = false)
    private String password;

    /**
     * User's full name.
     */
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private com.me.base.enums.Role role = com.me.base.enums.Role.USER;

    /**
     * Whether the user account is enabled/active.
     * Disabled accounts cannot login.
     */
    @Column(nullable = false)
    private Boolean enabled = true;
}
