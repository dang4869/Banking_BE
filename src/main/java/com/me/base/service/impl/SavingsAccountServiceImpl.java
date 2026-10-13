package com.me.base.service.impl;

import com.me.base.dto.SavingsRequestDto;
import com.me.base.dto.SavingsResponseDto;
import com.me.base.entity.BankAccount;
import com.me.base.entity.SavingsAccount;
import com.me.base.entity.Transaction;
import com.me.base.entity.User;
import com.me.base.enums.AccountStatus;
import com.me.base.enums.SavingsStatus;
import com.me.base.enums.TransactionStatus;
import com.me.base.enums.TransactionType;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import com.me.base.repository.BankAccountRepository;
import com.me.base.repository.SavingsAccountRepository;
import com.me.base.repository.TransactionRepository;
import com.me.base.repository.IUserRepository;
import com.me.base.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsAccountServiceImpl implements SavingsAccountService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final IUserRepository userRepository;

    private static final double DEMAND_INTEREST_RATE = 0.1; // 0.1% for premature closure

    private Long getCurrentUserId() {
        String currentUserIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(currentUserIdStr);
    }

    @Override
    @Transactional
    public SavingsResponseDto openSavingsAccount(SavingsRequestDto request) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        BankAccount sourceAccount = bankAccountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "Source account not found"));

        if (!sourceAccount.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCOUNT_ACCESS_DENIED, "You don't own this account");
        }

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE, "Source account is not active");
        }

        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        // Deduct balance
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        bankAccountRepository.save(sourceAccount);

        // Create savings account
        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setUser(user);
        savingsAccount.setLinkedBankAccount(sourceAccount);
        savingsAccount.setSavingsAccountNumber("SAV" + System.currentTimeMillis());
        savingsAccount.setPrincipalAmount(request.getAmount());
        savingsAccount.setTerm(request.getTerm());
        savingsAccount.setInterestRate(request.getTerm().getInterestRate());
        savingsAccount.setMaturityDate(LocalDate.now().plusMonths(request.getTerm().getMonths()));
        savingsAccount.setStatus(SavingsStatus.ACTIVE);
        
        savingsAccount = savingsAccountRepository.save(savingsAccount);

        // Record transaction
        createTransaction(sourceAccount, null, request.getAmount(), TransactionType.SAVINGS_DEPOSIT, 
                "Open Savings Account " + savingsAccount.getSavingsAccountNumber());

        return mapToDto(savingsAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsResponseDto> getMySavingsAccounts() {
        Long userId = getCurrentUserId();
        return savingsAccountRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SavingsResponseDto closeSavingsAccount(Long id) {
        Long userId = getCurrentUserId();
        SavingsAccount savingsAccount = savingsAccountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SAVINGS_NOT_FOUND, "Savings account not found"));

        if (savingsAccount.getStatus() != SavingsStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.SAVINGS_ALREADY_CLOSED, "Savings account is already closed or matured");
        }

        BankAccount linkedAccount = savingsAccount.getLinkedBankAccount();
        LocalDate now = LocalDate.now();
        
        double appliedInterestRate;
        SavingsStatus newStatus;

        if (now.isBefore(savingsAccount.getMaturityDate())) {
            // Premature closure
            appliedInterestRate = DEMAND_INTEREST_RATE;
            newStatus = SavingsStatus.CLOSED_PREMATURELY;
        } else {
            // Matured
            appliedInterestRate = savingsAccount.getInterestRate();
            newStatus = SavingsStatus.MATURED;
        }

        // Calculate days elapsed
        long daysElapsed = ChronoUnit.DAYS.between(savingsAccount.getCreatedAt().toLocalDate(), now);
        if (daysElapsed < 0) daysElapsed = 0; // Just in case
        
        // Formula: Principal * Rate / 100 * (Days / 365)
        BigDecimal interest = savingsAccount.getPrincipalAmount()
                .multiply(BigDecimal.valueOf(appliedInterestRate))
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(daysElapsed))
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = savingsAccount.getPrincipalAmount().add(interest);

        // Update linked account
        linkedAccount.setBalance(linkedAccount.getBalance().add(totalAmount));
        bankAccountRepository.save(linkedAccount);

        // Update savings account
        savingsAccount.setStatus(newStatus);
        savingsAccount.setEarnedInterest(interest);
        savingsAccount = savingsAccountRepository.save(savingsAccount);

        // Record transaction
        createTransaction(null, linkedAccount, totalAmount, TransactionType.SAVINGS_WITHDRAWAL, 
                "Close Savings Account " + savingsAccount.getSavingsAccountNumber());

        return mapToDto(savingsAccount);
    }

    private void createTransaction(BankAccount source, BankAccount destination, BigDecimal amount, 
                                   TransactionType type, String description) {
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setSourceAccount(source);
        transaction.setDestinationAccount(destination);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setDescription(description);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }

    private SavingsResponseDto mapToDto(SavingsAccount account) {
        SavingsResponseDto dto = new SavingsResponseDto();
        dto.setId(account.getId());
        dto.setSavingsAccountNumber(account.getSavingsAccountNumber());
        dto.setPrincipalAmount(account.getPrincipalAmount());
        dto.setTerm(account.getTerm());
        dto.setInterestRate(account.getInterestRate());
        dto.setMaturityDate(account.getMaturityDate());
        dto.setStatus(account.getStatus());
        dto.setEarnedInterest(account.getEarnedInterest());
        return dto;
    }
}
