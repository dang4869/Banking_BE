package com.me.base.entity;

import com.me.base.enums.SavingsStatus;
import com.me.base.enums.SavingsTerm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "savings_accounts")
public class SavingsAccount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_bank_account_id", nullable = false)
    private BankAccount linkedBankAccount;

    @Column(nullable = false, unique = true, length = 20)
    private String savingsAccountNumber;

    @Column(nullable = false)
    private BigDecimal principalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SavingsTerm term;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SavingsStatus status = SavingsStatus.ACTIVE;
    
    private BigDecimal earnedInterest;
}
