package com.me.base.service.impl;

import com.me.base.dto.UserDTO;
import com.me.base.dto.UserResponseDTO;
import com.me.base.entity.User;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import com.me.base.exception.ResourceNotFoundException;
import com.me.base.repository.IUserRepository;
import com.me.base.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of IUserService.
 * Provides business logic for user management operations.
 * 
 * @author Base Project
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements IUserService {
    
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO createUser(UserDTO userDTO) {
        // Check if username already exists
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userDTO.getUsername()))) {
            throw new BusinessException(ErrorCode.USER_USERNAME_EXISTS);
        }
        
        // Check if email already exists
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userDTO.getEmail()))) {
            throw new BusinessException(ErrorCode.USER_EMAIL_EXISTS);
        }
        
        // Create new user entity
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        user.setEnabled(true);
        user.setRole(com.me.base.enums.Role.USER); // Mặc định là USER
        
        // Save and return
        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponseDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        
        // Check if username is being changed and if it already exists
        if (!user.getUsername().equals(userDTO.getUsername()) &&
                Boolean.TRUE.equals(userRepository.existsByUsername(userDTO.getUsername()))) {
            throw new BusinessException(ErrorCode.USER_USERNAME_EXISTS);
        }
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(userDTO.getEmail()) &&
                Boolean.TRUE.equals(userRepository.existsByEmail(userDTO.getEmail()))) {
            throw new BusinessException(ErrorCode.USER_EMAIL_EXISTS);
        }
        
        // Update fields
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setFullName(userDTO.getFullName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setAddress(userDTO.getAddress());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, email));
        return mapToResponseDTO(user);
    }
    
    @Override
    public void lockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Override
    public void unlockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, id));
        user.setEnabled(true);
        userRepository.save(user);
    }
    
    /**
     * Maps User entity to UserResponseDTO.
     * 
     * @param user user entity
     * @return user response DTO
     */
    private UserResponseDTO mapToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setEnabled(user.getEnabled());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
