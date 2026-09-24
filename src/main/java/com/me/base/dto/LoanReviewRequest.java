package com.me.base.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO để ADMIN duyệt hoặc từ chối đơn vay.
 */
@Data
public class LoanReviewRequest {

    /** true = duyệt, false = từ chối */
    private boolean approved;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String adminNote;
}
