package com.me.base.service;

import com.me.base.dto.TransferRequestDto;
import com.me.base.entity.Transaction;

public interface TransactionService {
    Transaction transfer(TransferRequestDto transferRequest);
}
