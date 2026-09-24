package com.me.base.service;

import com.me.base.dto.CardDto;
import com.me.base.dto.CreateCardRequest;
import com.me.base.dto.UpdateCardStatusRequest;

import java.util.List;

public interface CardService {
    
    CardDto createCard(Long userId, CreateCardRequest request);
    
    CardDto getCardById(Long id);
    
    List<CardDto> getCardsByBankAccountId(Long bankAccountId);
    
    CardDto updateCardStatus(Long id, UpdateCardStatusRequest request);
}
