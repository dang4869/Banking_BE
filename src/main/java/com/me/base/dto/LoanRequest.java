package com.me.base.dto;

import com.me.base.enums.LoanType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO để user đăng ký khoản vay.
 */
@Data
public class LoanRequest {

    @NotNull(message = "Loại khoản vay không được để trống")
    private LoanType loanType;

    @NotNull(message = "Số tiền vay không được để trống")
    @DecimalMin(value = "1000000", message = "Số tiền vay tối thiểu là 1,000,000 VNĐ")
    @DecimalMax(value = "5000000000", message = "Số tiền vay tối đa là 5,000,000,000 VNĐ")
    private BigDecimal principalAmount;

    @NotNull(message = "Lãi suất không được để trống")
    @DecimalMin(value = "0.1", message = "Lãi suất tối thiểu là 0.1%/năm")
    @DecimalMax(value = "36.0", message = "Lãi suất tối đa là 36%/năm")
    private BigDecimal interestRatePerAnnum;

    @NotNull(message = "Số kỳ vay không được để trống")
    @Min(value = 1, message = "Số kỳ vay tối thiểu là 1 tháng")
    @Max(value = 360, message = "Số kỳ vay tối đa là 360 tháng (30 năm)")
    private Integer termMonths;

    @Size(max = 500, message = "Mục đích vay tối đa 500 ký tự")
    private String purpose;
}
