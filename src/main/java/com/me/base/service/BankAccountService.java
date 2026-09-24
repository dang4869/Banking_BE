package com.me.base.service;

import com.me.base.dto.BankAccountDto;
import com.me.base.dto.CreateAccountRequest;
import com.me.base.dto.UpdateAccountStatusRequest;

import java.util.List;

public interface BankAccountService {
    
    BankAccountDto createAccount(Long userId, CreateAccountRequest request);
    
    BankAccountDto getAccountById(Long id);
    
    List<BankAccountDto> getAccountsByUserId(Long userId);
    
    BankAccountDto updateAccountStatus(Long id, UpdateAccountStatusRequest request);
}
