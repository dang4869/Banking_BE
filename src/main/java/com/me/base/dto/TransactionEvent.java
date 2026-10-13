package com.me.base.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.me.base.entity.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionReference;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private LocalDateTime transactionDate;

    public TransactionEvent(Transaction transaction) {
        this.transactionReference = transaction.getTransactionReference();
        if (transaction.getSourceAccount() != null) {
            this.sourceAccountNumber = transaction.getSourceAccount().getAccountNumber();
        }
        if (transaction.getDestinationAccount() != null) {
            this.destinationAccountNumber = transaction.getDestinationAccount().getAccountNumber();
        }
        this.amount = transaction.getAmount();
        this.transactionDate = transaction.getTransactionDate();
    }
}
