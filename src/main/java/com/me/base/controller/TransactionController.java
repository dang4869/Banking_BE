package com.me.base.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.base.dto.TransferRequestDto;
import com.me.base.entity.Transaction;
import com.me.base.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequestDto request) {
        Transaction transaction = transactionService.transfer(request);
        
        // Use Map to avoid Jackson serialization errors with Hibernate LAZY proxies
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("transactionReference", transaction.getTransactionReference());
        response.put("amount", transaction.getAmount());
        response.put("type", transaction.getType());
        response.put("status", transaction.getStatus());
        response.put("description", transaction.getDescription());
        response.put("transactionDate", transaction.getTransactionDate());
        if (transaction.getSourceAccount() != null) {
            response.put("sourceAccountNumber", transaction.getSourceAccount().getAccountNumber());
            if (transaction.getSourceAccount().getUser() != null) {
                java.util.Map<String, Object> sourceUser = new java.util.HashMap<>();
                sourceUser.put("fullName", transaction.getSourceAccount().getUser().getFullName());
                sourceUser.put("email", transaction.getSourceAccount().getUser().getEmail());
                response.put("sourceUser", sourceUser);
            }
        }
        if (transaction.getDestinationAccount() != null) {
            response.put("destinationAccountNumber", transaction.getDestinationAccount().getAccountNumber());
            if (transaction.getDestinationAccount().getUser() != null) {
                java.util.Map<String, Object> destinationUser = new java.util.HashMap<>();
                destinationUser.put("fullName", transaction.getDestinationAccount().getUser().getFullName());
                destinationUser.put("email", transaction.getDestinationAccount().getUser().getEmail());
                response.put("destinationUser", destinationUser);
            }
        } else if (transaction.getExternalAccountNumber() != null) {
            response.put("destinationAccountNumber", transaction.getExternalAccountNumber());
        }
        if (transaction.getRecipientName() != null) {
            response.put("destinationAccountName", transaction.getRecipientName());
        }
        
        return ResponseEntity.ok(response);
    }
}
