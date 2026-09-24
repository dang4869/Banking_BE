package com.me.base.dto;

import com.me.base.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateCardRequest {
    
    @NotNull(message = "Bank Account ID is required")
    private Long bankAccountId;
    
    @NotBlank(message = "Card Holder Name is required")
    private String cardHolderName;
    
    @NotNull(message = "Card Type is required")
    private CardType cardType;
    
    private BigDecimal creditLimit; // Required if cardType is CREDIT
}
