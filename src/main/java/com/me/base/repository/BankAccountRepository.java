package com.me.base.repository;

import com.me.base.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    
    List<BankAccount> findByUserId(Long userId);
    
    boolean existsByAccountNumber(String accountNumber);
}
