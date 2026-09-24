package com.me.base.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User requests.
 * Used for creating and updating user information.
 * 
 * Includes validation constraints:
 * - username: required, 3-50 characters
 * - email: required, valid email format
 * - fullName: required, not blank
 * - password: required, minimum 6 characters
 * 
 * @author Base Project
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    /**
     * Username for the user account.
     */
    @NotBlank(message = "{validation.username.required}")
    @Size(min = 3, max = 50, message = "{validation.username.size}")
    private String username;
    
    /**
     * Email address for the user.
     */
    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;
    
    /**
     * Full name of the user.
     */
    @NotBlank(message = "{validation.fullname.required}")
    private String fullName;
    
    /**
     * Password for the user account.
     */
    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, message = "{validation.password.size}")
    private String password;

    private String phoneNumber;
    
    private String address;
}
