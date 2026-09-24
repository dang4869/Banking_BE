package com.me.base.service.impl;

import com.me.base.dto.CardDto;
import com.me.base.dto.CreateCardRequest;
import com.me.base.dto.UpdateCardStatusRequest;
import com.me.base.entity.BankAccount;
import com.me.base.entity.Card;
import com.me.base.enums.CardType;
import com.me.base.exception.ResourceNotFoundException;
import com.me.base.repository.BankAccountRepository;
import com.me.base.repository.CardRepository;
import com.me.base.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final BankAccountRepository bankAccountRepository;

    @Override
    @Transactional
    public CardDto createCard(Long userId, CreateCardRequest request) {
        BankAccount account = bankAccountRepository.findById(request.getBankAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount not found with id: " + request.getBankAccountId()));

        // Check if account belongs to user (in a real app)
        if (!account.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bank Account does not belong to the user");
        }

        Card card = new Card();
        card.setBankAccount(account);
        card.setCardNumber(generateUniqueCardNumber());
        card.setCardHolderName(request.getCardHolderName().toUpperCase());
        
        // Generate Expiry Date (e.g. 4 years from now)
        LocalDate expiryDate = LocalDate.now().plusYears(4);
        card.setExpiryDate(expiryDate.format(DateTimeFormatter.ofPattern("MM/yy")));
        
        // Generate random CVV
        card.setCvv(String.format("%03d", new Random().nextInt(1000)));
        
        card.setCardType(request.getCardType());
        
        if (request.getCardType() == CardType.CREDIT) {
            card.setCreditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : new BigDecimal("10000000")); // default 10M
        }

        Card savedCard = cardRepository.save(card);
        return mapToDto(savedCard);
    }

    @Override
    public CardDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        return mapToDto(card);
    }

    @Override
    public List<CardDto> getCardsByBankAccountId(Long bankAccountId) {
        return cardRepository.findByBankAccountId(bankAccountId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardDto updateCardStatus(Long id, UpdateCardStatusRequest request) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + id));
        
        card.setStatus(request.getStatus());
        return mapToDto(cardRepository.save(card));
    }

    private String generateUniqueCardNumber() {
        Random random = new Random();
        String cardNumber;
        do {
            // Generate 16 digit card number (for testing purposes, start with a dummy BIN)
            long prefix = 4000000000000000L;
            long number = prefix + (long)(random.nextDouble() * 999999999999999L);
            cardNumber = String.valueOf(number);
        } while (cardRepository.existsByCardNumber(cardNumber));
        return cardNumber;
    }

    private CardDto mapToDto(Card card) {
        CardDto dto = new CardDto();
        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setCardHolderName(card.getCardHolderName());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setCardType(card.getCardType());
        dto.setStatus(card.getStatus());
        dto.setCreditLimit(card.getCreditLimit());
        dto.setBankAccountId(card.getBankAccount().getId());
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());
        return dto;
    }
}
