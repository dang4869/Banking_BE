package com.me.base.dto;

import com.me.base.enums.RepaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO trả về thông tin một kỳ trả nợ.
 */
@Data
public class LoanRepaymentResponse {

    private Long id;
    private Long loanId;
    private Integer installmentNumber;
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingBalance;
    private RepaymentStatus status;
    private LocalDate paidAt;
    private LocalDateTime createdAt;
}
