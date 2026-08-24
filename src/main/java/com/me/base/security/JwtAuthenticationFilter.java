package com.me.base.security;

import com.me.base.service.impl.CustomUserDetailsService;
import com.me.base.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter.
 * Intercepts incoming requests and validates JWT tokens.
 * <p>
 * This filter:
 * 1. Extracts JWT token from Authorization header
 * 2. Validates the token
 * 3. Sets authentication in SecurityContext if valid
 * 
 * @author Base Project
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    
    /**
     * Filters incoming requests to validate JWT tokens.
     * 
     * @param request HTTP request
     * @param response HTTP response
     * @param filterChain filter chain
     * @throws ServletException if an error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Extract Authorization header
        final String authorizationHeader = request.getHeader("Authorization");
        
        Long userId = null;
        String jwt = null;
        
        // Check if token is present and properly formatted
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                userId = jwtUtil.extractUserId(jwt);
            } catch (Exception e) {
                logger.error("Error extracting user ID from JWT: " + e.getMessage());
            }
        }
        
        // Validate token and set authentication
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Extract email from JWT to load user
                String email = jwtUtil.extractEmail(jwt);
                
                // Load user details - this will throw exception if user doesn't exist
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                
                // Validate token
                if (Boolean.TRUE.equals(jwtUtil.validateToken(jwt, userDetails))) {
                    UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, 
                            null, 
                            userDetails.getAuthorities()
                        );
                    authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                // User not found or other error - token is invalid
                // This handles the case where user was deleted but token is still valid
                logger.error("Authentication failed for user ID {}: {}");
                // Don't set authentication - request will be unauthorized
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
