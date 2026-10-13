package com.me.base.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.base.dto.BaseResponse;
import com.me.base.dto.UserDTO;
import com.me.base.dto.UserResponseDTO;
import com.me.base.service.IMessageService;
import com.me.base.service.IUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for User management operations.
 * Provides CRUD endpoints for user resources.
 * <p>
 * All endpoints require authentication (JWT token).
 * @author Base Project
 * @version 1.0
 */
@Tag(name = "Users", description = "User management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final IUserService userService;
    private final IMessageService messageService;
    
    /**
     * Creates a new user.
     * 
     * @param userDTO user data
     * @return ResponseEntity with created user
     */
    @Operation(summary = "Create user", description = "Creates a new user account")
    @PostMapping
    public ResponseEntity<BaseResponse<UserResponseDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO user = userService.createUser(userDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success(messageService.getMessage("success.user.created"), user));
    }
    
    /**
     * Retrieves all users.
     * 
     * @return ResponseEntity with list of users
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @GetMapping
    public ResponseEntity<BaseResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.users.retrieved"), users));
    }
    
    /**
     * Retrieves a user by ID.
     * 
     * @param id user ID
     * @return ResponseEntity with user data
     */
    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.user.retrieved"), user));
    }
    
    /**
     * Updates an existing user.
     * 
     * @param id user ID
     * @param userDTO updated user data
     * @return ResponseEntity with updated user
     */
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO user = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.user.updated"), user));
    }
    
    /**
     * Deletes a user.
     * 
     * @param id user ID
     * @return ResponseEntity with success message
     */
    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.user.deleted")));
    }

    /**
     * Retrieves the currently logged-in user.
     * 
     * @return ResponseEntity with current user data
     */
    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's information")
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // CustomUserDetailsService sets the user's ID as the username in UserDetails
        String userIdStr = authentication.getName(); 
        UserResponseDTO user = userService.getUserById(Long.valueOf(userIdStr));
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.user.retrieved"), user));
    }
}
