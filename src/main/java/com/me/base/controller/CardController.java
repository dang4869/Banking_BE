package com.me.base.controller;

import com.me.base.dto.CardDto;
import com.me.base.dto.CreateCardRequest;
import com.me.base.dto.UpdateCardStatusRequest;
import com.me.base.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // In a real application, userId would be extracted from SecurityContext (JWT token)
    @PostMapping("/user/{userId}")
    public ResponseEntity<CardDto> createCard(
            @PathVariable Long userId,
            @Valid @RequestBody CreateCardRequest request) {
        return new ResponseEntity<>(cardService.createCard(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardDto>> getCardsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(cardService.getCardsByBankAccountId(accountId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCardById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CardDto> updateCardStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCardStatusRequest request) {
        return ResponseEntity.ok(cardService.updateCardStatus(id, request));
    }
}
