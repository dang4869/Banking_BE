package com.me.base.repository;

import com.me.base.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {
    List<SavingsAccount> findByUserId(Long userId);
    Optional<SavingsAccount> findByIdAndUserId(Long id, Long userId);
}
