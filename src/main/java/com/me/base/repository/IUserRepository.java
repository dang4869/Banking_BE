package com.me.base.repository;

import com.me.base.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom query methods for User entities.
 * <p>
 * Custom query methods:
 * - findByUsername: find user by username
 * - findByEmail: find user by email
 * - existsByUsername: check if username exists
 * - existsByEmail: check if email exists
 * 
 * @author Base Project
 * @version 1.0
 */
@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Finds a user by email.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Checks if a username already exists.
     * 
     * @param username the username to check
     * @return true if username exists
     */
    Boolean existsByUsername(String username);
    
    /**
     * Checks if an email already exists.
     * 
     * @param email the email to check
     * @return true if email exists
     */
    Boolean existsByEmail(String email);
}
