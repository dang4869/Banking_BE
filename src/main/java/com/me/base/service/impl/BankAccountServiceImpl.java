package com.me.base.service.impl;

import com.me.base.dto.BankAccountDto;
import com.me.base.dto.CreateAccountRequest;
import com.me.base.dto.UpdateAccountStatusRequest;
import com.me.base.entity.BankAccount;
import com.me.base.entity.User;
import com.me.base.exception.ResourceNotFoundException;
import com.me.base.repository.BankAccountRepository;
import com.me.base.repository.IUserRepository;
import com.me.base.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final IUserRepository userRepository;

    @Override
    @Transactional
    public BankAccountDto createAccount(Long userId, CreateAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setAccountNumber(generateUniqueAccountNumber());
        account.setCurrency(request.getCurrency());
        account.setBalance(BigDecimal.ZERO);

        BankAccount savedAccount = bankAccountRepository.save(account);
        return mapToDto(savedAccount);
    }

    @Override
    public BankAccountDto getAccountById(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount not found with id: " + id));
        return mapToDto(account);
    }

    @Override
    public List<BankAccountDto> getAccountsByUserId(Long userId) {
        return bankAccountRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BankAccountDto updateAccountStatus(Long id, UpdateAccountStatusRequest request) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount not found with id: " + id));
        
        account.setStatus(request.getStatus());
        return mapToDto(bankAccountRepository.save(account));
    }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            // Generate 10 digit account number
            long number = 1000000000L + (long)(random.nextDouble() * 8999999999L);
            accountNumber = String.valueOf(number);
        } while (bankAccountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private BankAccountDto mapToDto(BankAccount account) {
        BankAccountDto dto = new BankAccountDto();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency());
        dto.setStatus(account.getStatus());
        dto.setUserId(account.getUser().getId());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }
}
