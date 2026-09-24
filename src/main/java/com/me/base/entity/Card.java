package com.me.base.entity;

import com.me.base.enums.CardStatus;
import com.me.base.enums.CardType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cards")
public class Card extends BaseEntity {

    @Column(nullable = false, unique = true, length = 20)
    private String cardNumber;

    @Column(nullable = false, length = 100)
    private String cardHolderName;

    @Column(nullable = false, length = 5)
    private String expiryDate; // Format: MM/YY

    @Column(nullable = false, length = 3)
    private String cvv;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status = CardStatus.ACTIVE;

    @Column
    private BigDecimal creditLimit; // Only for CREDIT cards

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;
}
