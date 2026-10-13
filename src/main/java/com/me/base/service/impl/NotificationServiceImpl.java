package com.me.base.service.impl;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import com.me.base.service.NotificationService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendTransferNotification(String accountNumber, BigDecimal amount, String type) {
        // Mock sending notification
        log.info("Sending {} notification to account {} for amount {}", type, accountNumber, amount);
    }
}
