package com.me.base.controller;

import com.me.base.dto.BankAccountDto;
import com.me.base.dto.CreateAccountRequest;
import com.me.base.dto.UpdateAccountStatusRequest;
import com.me.base.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    // In a real application, userId would be extracted from SecurityContext (JWT token)
    @PostMapping("/user/{userId}")
    public ResponseEntity<BankAccountDto> createAccount(
            @PathVariable Long userId,
            @Valid @RequestBody CreateAccountRequest request) {
        return new ResponseEntity<>(bankAccountService.createAccount(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BankAccountDto>> getAccountsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(bankAccountService.getAccountsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDto> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountService.getAccountById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BankAccountDto> updateAccountStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountStatusRequest request) {
        return ResponseEntity.ok(bankAccountService.updateAccountStatus(id, request));
    }
}
