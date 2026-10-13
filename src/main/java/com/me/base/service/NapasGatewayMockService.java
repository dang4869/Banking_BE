package com.me.base.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import java.math.BigDecimal;

@Service
@Slf4j
public class NapasGatewayMockService {
    
    // Mock external database lookup (BankCode-AccountNumber -> AccountName)
    private final Map<String, String> externalAccounts = Map.of(
        "VCB-123456", "NGUYEN VAN A (Vietcombank)",
        "TCB-654321", "TRAN THI B (Techcombank)",
        "BIDV-111111", "LE VAN C (BIDV)"
    );

    public String resolveAccountName(String bankCode, String accountNumber) {
        log.info("Sending request to NAPAS Gateway to resolve account: {} at bank: {}", accountNumber, bankCode);
        String key = bankCode + "-" + accountNumber;
        if (externalAccounts.containsKey(key)) {
            String name = externalAccounts.get(key);
            log.info("NAPAS Gateway resolved name: {}", name);
            return name;
        }
        log.warn("NAPAS Gateway could not find the account!");
        throw new BusinessException(ErrorCode.ACCOUNT_NOT_FOUND, "Tài khoản không tồn tại trên hệ thống NAPAS");
    }

    public boolean processInterbankTransfer(String bankCode, String accountNumber, BigDecimal amount) {
        log.info("Sending transfer request to NAPAS: {} VND to {} at {}", amount, accountNumber, bankCode);
        // Simulate network delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("NAPAS processed transfer successfully!");
        // Always return success for mock
        return true;
    }
}
