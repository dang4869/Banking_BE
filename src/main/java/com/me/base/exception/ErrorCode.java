package com.me.base.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum defining all application error codes.
 * Each error code contains:
 * - Unique code identifier
 * - Message key for i18n lookup
 * - HTTP status code
 * <p>
 * Usage:
 * throw new BusinessException(ErrorCode.USER_NOT_FOUND, userId);
 * 
 * @author Base Project
 * @version 1.0
 */
@Getter
public enum ErrorCode {
    
    // ========== User Related Errors (1xxx) ==========
    USER_NOT_FOUND("error.user.not.found", HttpStatus.NOT_FOUND),
    USER_USERNAME_EXISTS("error.user.username.exists", HttpStatus.CONFLICT),
    USER_EMAIL_EXISTS("error.user.email.exists", HttpStatus.CONFLICT),
    
    // ========== Authentication Errors (2xxx) ==========
    INVALID_CREDENTIALS("error.invalid.credentials", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("auth.token.invalid", HttpStatus.UNAUTHORIZED),
    TOKEN_MISSING("auth.token.missing", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("auth.access.denied", HttpStatus.FORBIDDEN),
    
    // ========== Refresh Token Errors (4xxx) ==========
    REFRESH_TOKEN_EXPIRED("error.refresh.token.expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_NOT_FOUND("error.refresh.token.not.found", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("error.refresh.token.invalid", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REVOKED("error.refresh.token.revoked", HttpStatus.UNAUTHORIZED),
    REFRESH_RATE_LIMIT_EXCEEDED("error.refresh.rate.limit.exceeded", HttpStatus.TOO_MANY_REQUESTS),
    
    // ========== Validation Errors (3xxx) ==========
    VALIDATION_FAILED("error.validation.failed", HttpStatus.BAD_REQUEST),
    
    // ========== Loan Errors (6xxx) ==========
    LOAN_NOT_FOUND("error.loan.not.found", HttpStatus.NOT_FOUND),
    LOAN_ALREADY_PROCESSED("error.loan.already.processed", HttpStatus.CONFLICT),
    LOAN_NOT_APPROVED("error.loan.not.approved", HttpStatus.BAD_REQUEST),
    LOAN_ALREADY_DISBURSED("error.loan.already.disbursed", HttpStatus.CONFLICT),
    LOAN_NOT_ACTIVE("error.loan.not.active", HttpStatus.BAD_REQUEST),
    LOAN_ALREADY_CLOSED("error.loan.already.closed", HttpStatus.CONFLICT),
    LOAN_REPAYMENT_NOT_FOUND("error.loan.repayment.not.found", HttpStatus.NOT_FOUND),
    LOAN_REPAYMENT_ALREADY_PAID("error.loan.repayment.already.paid", HttpStatus.CONFLICT),
    LOAN_AMOUNT_INVALID("error.loan.amount.invalid", HttpStatus.BAD_REQUEST),

    // ========== Internal Errors (5xxx) ==========
    INTERNAL_ERROR("error.internal", HttpStatus.INTERNAL_SERVER_ERROR);
    
    private final String messageKey;
    private final HttpStatus httpStatus;
    
    /**
     * Constructor for ErrorCode enum.
     * 
     * @param messageKey key for looking up localized message
     * @param httpStatus HTTP status code for this error
     */
    ErrorCode(String messageKey, HttpStatus httpStatus) {
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
    }
}
