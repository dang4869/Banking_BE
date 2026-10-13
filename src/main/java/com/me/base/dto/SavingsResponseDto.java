package com.me.base.dto;

import com.me.base.enums.SavingsStatus;
import com.me.base.enums.SavingsTerm;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SavingsResponseDto {
    private Long id;
    private String savingsAccountNumber;
    private BigDecimal principalAmount;
    private SavingsTerm term;
    private Double interestRate;
    private LocalDate maturityDate;
    private SavingsStatus status;
    private BigDecimal earnedInterest;
}
