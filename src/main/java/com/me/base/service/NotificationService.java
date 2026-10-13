package com.me.base.service;

import java.math.BigDecimal;

public interface NotificationService {
    void sendTransferNotification(String accountNumber, BigDecimal amount, String type);
}
