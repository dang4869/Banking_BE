package com.me.base.controller;

import com.me.base.dto.*;
import com.me.base.service.IMessageService;
import com.me.base.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations.
 * Provides endpoints for user registration, login, token refresh, and logout.
 * 
 * @author Base Project
 * @version 1.0
 */
@Tag(name = "Authentication", description = "Authentication management APIs including JWT and refresh tokens")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final IMessageService messageService;
    
    /**
     * Registers a new user and returns access + refresh tokens.
     * 
     * @param userDTO user registration data
     * @param request HTTP request for device/IP tracking
     * @return ResponseEntity with authentication response
     */
    @Operation(summary = "Register new user", 
               description = "Creates a new user account and returns JWT access token and refresh token")
    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AuthResponseDTO>> register(
            @Valid @RequestBody UserDTO userDTO,
            HttpServletRequest request) {
        
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = getClientIP(request);
        
        AuthResponseDTO response = authService.register(userDTO, deviceInfo, ipAddress);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success(messageService.getMessage("success.register"), response));
    }
    
    /**
     * Authenticates a user and returns access + refresh tokens.
     * 
     * @param loginDTO login credentials
     * @param request HTTP request for device/IP tracking
     * @return ResponseEntity with authentication response
     */
    @Operation(summary = "Login user", 
               description = "Authenticates user and returns JWT access token and refresh token")
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDTO>> login(
            @Valid @RequestBody LoginDTO loginDTO,
            HttpServletRequest request) {
        
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = getClientIP(request);
        
        AuthResponseDTO response = authService.login(loginDTO, deviceInfo, ipAddress);
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.login"), response));
    }
    
    /**
     * Refreshes access token using refresh token.
     * Returns new access token and new refresh token (token rotation).
     * 
     * @param refreshTokenRequest refresh token request
     * @param request HTTP request for device/IP tracking
     * @return ResponseEntity with new tokens
     */
    @Operation(summary = "Refresh access token", 
               description = "Uses refresh token to obtain new access token and refresh token (token rotation)")
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponseDTO>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request) {
        
        String deviceInfo = request.getHeader("User-Agent");
        String ipAddress = getClientIP(request);
        
        AuthResponseDTO response = authService.refreshAccessToken(
            refreshTokenRequest.getRefreshToken(), 
            deviceInfo, 
            ipAddress
        );
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.token.refreshed"), response));
    }
    
    /**
     * Logs out user by revoking the refresh token.
     * 
     * @param refreshTokenRequest refresh token to revoke
     * @return ResponseEntity with success message
     */
    @Operation(summary = "Logout", 
               description = "Revokes the refresh token, effectively logging out the user from this device")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        authService.logout(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.logout")));
    }
    
    /**
     * Extracts client IP address from request.
     * Checks various headers for proxy/load balancer scenarios.
     * 
     * @param request HTTP request
     * @return client IP address
     */
    private String getClientIP(HttpServletRequest request) {
        String[] headers = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Get first IP if multiple IPs are present
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
}
