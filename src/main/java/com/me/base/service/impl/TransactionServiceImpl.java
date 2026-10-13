package com.me.base.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.me.base.dto.TransferRequestDto;
import com.me.base.entity.BankAccount;
import com.me.base.entity.Transaction;
import com.me.base.enums.AccountStatus;
import com.me.base.enums.TransactionStatus;
import com.me.base.enums.TransactionType;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import com.me.base.repository.BankAccountRepository;
import com.me.base.repository.TransactionRepository;
import com.me.base.service.TransactionService;
import com.me.base.service.NapasGatewayMockService;
import com.me.base.dto.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NapasGatewayMockService napasGatewayMockService;

    @Override
    @Transactional
    public Transaction transfer(TransferRequestDto request) {
        String currentUserIdStr = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentUserId = Long.parseLong(currentUserIdStr);

        // Auto-fetch source account based on logged-in user
        java.util.List<BankAccount> userAccounts = bankAccountRepository.findByUserId(currentUserId);
        if (userAccounts.isEmpty()) {
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "You don't have any bank accounts");
        }

        BankAccount sourceAccount = userAccounts.stream()
                .filter(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE, "You don't have any active bank accounts"));

        if (sourceAccount.getAccountNumber().equals(request.getDestinationAccountNumber())) {
            throw new BusinessException(ErrorCode.SAME_ACCOUNT_TRANSFER);
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setSourceAccount(sourceAccount);
        transaction.setAmount(request.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(LocalDateTime.now());

        boolean isInternal = request.getDestinationBankCode() == null 
                || request.getDestinationBankCode().isBlank() 
                || "MY_BANK".equalsIgnoreCase(request.getDestinationBankCode());

        if (isInternal) {
            BankAccount destinationAccount = bankAccountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "Destination account not found"));

            if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {
                throw new BusinessException(ErrorCode.ACCOUNT_NOT_ACTIVE, "Destination account is not active");
            }

            if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
            }

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));

            bankAccountRepository.save(sourceAccount);
            bankAccountRepository.save(destinationAccount);
            
            transaction.setDestinationAccount(destinationAccount);
            transaction.setRecipientName(destinationAccount.getUser() != null ? destinationAccount.getUser().getFullName() : null);
            transaction.setStatus(TransactionStatus.COMPLETED);
        } else {
            // EXTERNAL TRANSFER (NAPAS MOCK)
            String externalBankCode = request.getDestinationBankCode();
            String externalAccountNo = request.getDestinationAccountNumber();
            
            // 1. Resolve Account Name via NAPAS
            String externalName = napasGatewayMockService.resolveAccountName(externalBankCode, externalAccountNo);
            
            if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_BALANCE);
            }

            // 2. Deduct money from source
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
            bankAccountRepository.save(sourceAccount);

            // 3. Process transfer via NAPAS
            boolean napasSuccess = napasGatewayMockService.processInterbankTransfer(externalBankCode, externalAccountNo, request.getAmount());

            transaction.setExternalBankCode(externalBankCode);
            transaction.setExternalAccountNumber(externalAccountNo);
            transaction.setRecipientName(externalName);
            transaction.setStatus(napasSuccess ? TransactionStatus.COMPLETED : TransactionStatus.FAILED);
            
            if (!napasSuccess) {
                // In real world, we need a compensation transaction (refund) if NAPAS fails
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "Giao dịch qua NAPAS thất bại");
            }
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        TransactionEvent event = new TransactionEvent(savedTransaction);
        kafkaTemplate.send("notification-events", event);

        return savedTransaction;
    }
}
