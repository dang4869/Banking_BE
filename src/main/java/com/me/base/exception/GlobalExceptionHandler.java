package com.me.base.exception;

import com.me.base.dto.BaseResponse;
import com.me.base.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class intercepts exceptions thrown by controllers and converts them
 * into standardized error responses.
 * <p>
 * Handles:
 * - ResourceNotFoundException → 404
 * - MethodArgumentNotValidException → 400 with validation errors
 * - Generic Exception → 500
 * 
 * @author Base Project
 * @version 1.0
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    
    private final IMessageService messageService;
    
    /**
     * Handles BusinessException with ErrorCode.
     * Returns appropriate HTTP status with localized error message.
     * 
     * @param ex the exception
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException ex) {
        String message;
        try {
            message = messageService.getMessage(ex.getErrorCode().getMessageKey(), ex.getArgs());
        } catch (org.springframework.context.NoSuchMessageException e) {
            // Fallback to the raw message key if i18n translation is missing
            message = ex.getErrorCode().getMessageKey();
        }
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(BaseResponse.error(message));
    }
    
    /**
     * Handles ResourceNotFoundException.
     * Returns 404 NOT FOUND with error message.
     * 
     * @param ex the exception
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String message = ex.getMessage();
        // If message is a key, resolve it
        if (message != null && !message.contains(" ")) {
            message = messageService.getMessage(message);
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(message));
    }
    
    /**
     * Handles validation errors from @Valid annotation.
     * Returns 400 BAD REQUEST with field-specific error messages.
     * 
     * @param ex the exception
     * @return ResponseEntity with validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error("Validation failed", errors));
    }
    
    /**
     * Handles AccessDeniedException from Spring Security.
     * Returns 403 FORBIDDEN.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Void>> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("Access Denied"));
    }
    
    /**
     * Handles missing routes/resources (Spring 6 / Boot 3).
     * Returns 404 NOT FOUND.
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Void>> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error("Resource not found"));
    }
    
    /**
     * Handles incorrect HTTP methods.
     * Returns 405 METHOD NOT ALLOWED.
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.error("Method not allowed"));
    }
    
    /**
     * Handles all other uncaught exceptions.
     * Returns 500 INTERNAL SERVER ERROR with generic error message.
     * 
     * @param ex the exception
     * @return ResponseEntity with error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("An error occurred: " + ex.getMessage()));
    }
}
