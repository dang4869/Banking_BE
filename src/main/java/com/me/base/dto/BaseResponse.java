package com.me.base.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic wrapper class for standardizing API responses.
 * This class provides a consistent response structure across all API endpoints.
 * <p>
 * Response structure:
 * - success: boolean indicating if the operation was successful
 * - message: descriptive message about the operation result
 * - data: actual response data (can be any type)
 * - timestamp: when the response was generated
 * 
 * @param <T> the type of data being returned
 * 
 * @author Base Project
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    
    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;
    
    /**
     * Descriptive message about the operation result.
     */
    private String message;
    
    /**
     * The actual response data.
     */
    private T data;
    
    /**
     * Timestamp when the response was generated.
     */
    private LocalDateTime timestamp;
    
    /**
     * Creates a successful response with data.
     * 
     * @param message success message
     * @param data response data
     * @param <T> type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> success(String message, T data) {
        return new BaseResponse<>(true, message, data, LocalDateTime.now());
    }
    
    /**
     * Creates a successful response without data.
     * 
     * @param message success message
     * @param <T> type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> success(String message) {
        return new BaseResponse<>(true, message, null, LocalDateTime.now());
    }
    
    /**
     * Creates an error response.
     * 
     * @param message error message
     * @param <T> type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> error(String message) {
        return new BaseResponse<>(false, message, null, LocalDateTime.now());
    }
    
    /**
     * Creates an error response with data.
     * 
     * @param message error message
     * @param data error details
     * @param <T> type of data
     * @return BaseResponse instance
     */
    public static <T> BaseResponse<T> error(String message, T data) {
        return new BaseResponse<>(false, message, data, LocalDateTime.now());
    }
}
