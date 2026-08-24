package com.me.base.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 * This exception is typically used in service layer when fetching entities by ID
 * or other criteria that should exist but don't.
 * <p>
 * HTTP Status: 404 NOT FOUND
 * 
 * @author Base Project
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * 
     * @param message the detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
