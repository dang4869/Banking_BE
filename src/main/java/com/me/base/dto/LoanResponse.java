package com.me.base.dto;

import com.me.base.enums.LoanStatus;
import com.me.base.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO trả về thông tin khoản vay.
 */
@Data
public class LoanResponse {

    private Long id;
    private Long userId;
    private String userFullName;
    private LoanType loanType;
    private BigDecimal principalAmount;
    private BigDecimal interestRatePerAnnum;
    private Integer termMonths;
    private BigDecimal monthlyPayment;
    private BigDecimal totalInterest;
    private BigDecimal totalPayment;
    private String purpose;
    private LoanStatus status;
    private String adminNote;
    private LocalDate approvedAt;
    private LocalDate disbursedAt;
    private LocalDate closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
