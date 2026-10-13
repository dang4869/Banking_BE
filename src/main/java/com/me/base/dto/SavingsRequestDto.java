package com.me.base.dto;

import com.me.base.enums.SavingsTerm;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SavingsRequestDto {

    @NotBlank(message = "Source account number is required")
    private String sourceAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000000.0", message = "Minimum savings amount is 1,000,000 VND")
    private BigDecimal amount;

    @NotNull(message = "Savings term is required")
    private SavingsTerm term;
}
