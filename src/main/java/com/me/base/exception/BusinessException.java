package com.me.base.exception;

import lombok.Getter;

/**
 * Base exception class for all business exceptions.
 * Uses ErrorCode enum for centralized error management.
 *
 * @author Base Project
 * @version 1.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] args;
    
    /**
     * Constructor with error code only.
     * 
     * @param errorCode the error code
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = null;
    }
    
    /**
     * Constructor with error code and message parameters.
     * 
     * @param errorCode the error code
     * @param args parameters for message formatting
     */
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
        this.args = args;
    }
    
    /**
     * Constructor with error code and cause.
     * 
     * @param errorCode the error code
     * @param cause the underlying cause
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessageKey(), cause);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    /**
     * Constructor with error code, message parameters, and cause.
     * 
     * @param errorCode the error code
     * @param cause the underlying cause
     * @param args parameters for message formatting
     */
    public BusinessException(ErrorCode errorCode, Throwable cause, Object... args) {
        super(errorCode.getMessageKey(), cause);
        this.errorCode = errorCode;
        this.args = args;
    }
}
