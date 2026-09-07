package com.me.base.service;

import com.me.base.dto.UserDTO;
import com.me.base.dto.UserResponseDTO;

import java.util.List;

/**
 * Service interface for User operations.
 * Defines business logic methods for managing users.
 * 
 * @author Base Project
 * @version 1.0
 */
public interface IUserService {
    
    /**
     * Creates a new user.
     * 
     * @param userDTO user data
     * @return created user information
     */
    UserResponseDTO createUser(UserDTO userDTO);
    
    /**
     * Retrieves a user by ID.
     * 
     * @param id user ID
     * @return user information
     */
    UserResponseDTO getUserById(Long id);
    
    /**
     * Retrieves all users.
     * 
     * @return list of all users
     */
    List<UserResponseDTO> getAllUsers();
    
    /**
     * Updates an existing user.
     * 
     * @param id user ID
     * @param userDTO updated user data
     * @return updated user information
     */
    UserResponseDTO updateUser(Long id, UserDTO userDTO);
    
    /**
     * Deletes a user.
     * 
     * @param id user ID
     */
    void deleteUser(Long id);
    
    /**
     * Retrieves a user by username.
     * 
     * @param username username to search
     * @return user information
     */
    UserResponseDTO getUserByUsername(String username);

    /**
     * Khóa tài khoản (disable).
     * @param id user ID
     */
    void lockUser(Long id);

    /**
     * Mở khóa tài khoản (enable).
     * @param id user ID
     */
    void unlockUser(Long id);
}
