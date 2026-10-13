package com.me.base.entity;

import com.me.base.enums.TransactionStatus;
import com.me.base.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private BankAccount sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private BankAccount destinationAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(length = 255)
    private String description;

    @Column(name = "external_bank_code", length = 20)
    private String externalBankCode;

    @Column(name = "external_account_number", length = 50)
    private String externalAccountNumber;

    @Column(name = "recipient_name", length = 100)
    private String recipientName;

    @Column(nullable = false)
    private LocalDateTime transactionDate;
}
