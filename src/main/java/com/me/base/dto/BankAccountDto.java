package com.me.base.dto;

import com.me.base.enums.AccountStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankAccountDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
