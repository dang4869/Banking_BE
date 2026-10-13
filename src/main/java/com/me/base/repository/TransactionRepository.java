package com.me.base.repository;

import com.me.base.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionReference(String transactionReference);
    List<Transaction> findBySourceAccountIdOrDestinationAccountIdOrderByTransactionDateDesc(Long sourceAccountId, Long destinationAccountId);
}
