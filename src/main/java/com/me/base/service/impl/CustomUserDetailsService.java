package com.me.base.service.impl;

import com.me.base.entity.User;
import com.me.base.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * Loads user-specific data during authentication.
 * 
 * @author Base Project
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final IUserRepository userRepository;
    
    /**
     * Loads user by username for authentication.
     * Throws exception if user not found or account is disabled.
     * 
     * Note: Returns UserDetails with userId as "username" for JWT compatibility.
     * 
     * @param email the email to load user by
     * @return UserDetails object with userId as username
     * @throws UsernameNotFoundException if user not found or disabled
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        // Check if user account is enabled
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new UsernameNotFoundException("User account is disabled: " + email);
        }
        
        // Return UserDetails with userId as "username" for JWT validation
        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(), // Use ID as username
                user.getPassword(),
                user.getEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name()))

        );
    }
}
