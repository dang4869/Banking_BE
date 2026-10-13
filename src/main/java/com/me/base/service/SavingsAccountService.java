package com.me.base.service;

import com.me.base.dto.SavingsRequestDto;
import com.me.base.dto.SavingsResponseDto;

import java.util.List;

public interface SavingsAccountService {
    SavingsResponseDto openSavingsAccount(SavingsRequestDto request);
    List<SavingsResponseDto> getMySavingsAccounts();
    SavingsResponseDto closeSavingsAccount(Long id);
}
