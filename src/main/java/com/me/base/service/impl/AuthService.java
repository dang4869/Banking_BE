package com.me.base.service.impl;

import com.me.base.dto.AuthResponseDTO;
import com.me.base.dto.LoginDTO;
import com.me.base.dto.UserDTO;
import com.me.base.dto.UserResponseDTO;
import com.me.base.entity.RefreshToken;
import com.me.base.entity.User;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import com.me.base.repository.IUserRepository;
import com.me.base.service.IRefreshTokenService;
import com.me.base.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling authentication operations.
 * Manages user registration and login with JWT token generation.
 * 
 * @author Base Project
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final IRefreshTokenService refreshTokenService;
    private final IUserRepository userRepository;
    
    @Value("${jwt.expiration:900000}") // 15 minutes default
    private Long accessTokenDurationMs;
    
    /**
     * Registers a new user account and generates tokens.
     * 
     * @param userDTO registration data
     * @param deviceInfo device information from request
     * @param ipAddress IP address from request
     * @return authentication response with tokens
     */
    public AuthResponseDTO register(UserDTO userDTO, String deviceInfo, String ipAddress) {
        UserResponseDTO user = userService.createUser(userDTO);
        
        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getUsername());
        
        // Get full user entity for refresh token
        User userEntity = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        String refreshToken = refreshTokenService.createRefreshToken(userEntity, deviceInfo, ipAddress);
        
        return new AuthResponseDTO(
            accessToken,
            refreshToken,
            "Bearer",
            accessTokenDurationMs / 1000, // Convert to seconds
            user
        );
    }
    
    /**
     * Authenticates a user and generates tokens.
     * 
     * @param loginDTO login credentials
     * @param deviceInfo device information
     * @param ipAddress IP address
     * @return authentication response with tokens
     * @throws BusinessException if credentials are invalid
     */
    public AuthResponseDTO login(LoginDTO loginDTO, String deviceInfo, String ipAddress) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
                )
            );
            
            // Get user details
            UserResponseDTO user = userService.getUserByUsername(loginDTO.getEmail());
            
            // Generate tokens
            String accessToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getUsername());
            
            // Get full user entity for refresh token
            User userEntity = userRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
            String refreshToken = refreshTokenService.createRefreshToken(userEntity, deviceInfo, ipAddress);
            
            return new AuthResponseDTO(
                accessToken,
                refreshToken,
                "Bearer",
                accessTokenDurationMs / 1000,
                user
            );
            
        } catch (BadCredentialsException e) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
    
    /**
     * Refreshes access token using refresh token.
     * 
     * @param refreshTokenValue refresh token
     * @param deviceInfo device information
     * @param ipAddress IP address
     * @return new authentication response with new tokens
     */
    public AuthResponseDTO refreshAccessToken(String refreshTokenValue, String deviceInfo, String ipAddress) {
        // Verify refresh token
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenValue);
        
        // Delete old refresh token (token rotation)
        refreshTokenService.deleteRefreshToken(refreshToken);
        
        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(
            refreshToken.getUser().getId(), 
            refreshToken.getUser().getEmail(), 
            refreshToken.getUser().getUsername()
        );
        String newRefreshToken = refreshTokenService.createRefreshToken(refreshToken.getUser(), deviceInfo, ipAddress);
        
        // Get user details
        UserResponseDTO user = userService.getUserByUsername(refreshToken.getUser().getEmail());
        
        return new AuthResponseDTO(
            newAccessToken,
            newRefreshToken,
            "Bearer",
            accessTokenDurationMs / 1000,
            user
        );
    }
    
    /**
     * Logs out user by revoking refresh token.
     * 
     * @param refreshTokenValue refresh token to revoke
     */
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenValue);
        refreshTokenService.deleteRefreshToken(refreshToken);
    }
    
    /**
     * Logs out user from all devices.
     * 
     * @param email user email
     */
    public void logoutAll(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        refreshTokenService.deleteAllByUser(user);
    }
}
