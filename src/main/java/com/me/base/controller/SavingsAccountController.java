package com.me.base.controller;

import com.me.base.dto.SavingsRequestDto;
import com.me.base.dto.SavingsResponseDto;
import com.me.base.service.SavingsAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping
    public ResponseEntity<SavingsResponseDto> openSavingsAccount(@Valid @RequestBody SavingsRequestDto request) {
        return new ResponseEntity<>(savingsAccountService.openSavingsAccount(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SavingsResponseDto>> getMySavingsAccounts() {
        return ResponseEntity.ok(savingsAccountService.getMySavingsAccounts());
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<SavingsResponseDto> closeSavingsAccount(@PathVariable Long id) {
        return ResponseEntity.ok(savingsAccountService.closeSavingsAccount(id));
    }
}
