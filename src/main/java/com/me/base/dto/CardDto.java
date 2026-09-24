package com.me.base.dto;

import com.me.base.enums.CardStatus;
import com.me.base.enums.CardType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CardDto {
    private Long id;
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private CardType cardType;
    private CardStatus status;
    private BigDecimal creditLimit;
    private Long bankAccountId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
