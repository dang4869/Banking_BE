package com.me.base.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.me.base.dto.TransactionEvent;
import com.me.base.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification-events", groupId = "banking-group")
    public void handleTransactionNotification(TransactionEvent event) {
        log.info("Received transaction event: {}", event.getTransactionReference());
        
        if (event.getSourceAccountNumber() != null) {
            notificationService.sendTransferNotification(event.getSourceAccountNumber(), event.getAmount(), "SENT");
        }
        
        if (event.getDestinationAccountNumber() != null) {
            notificationService.sendTransferNotification(event.getDestinationAccountNumber(), event.getAmount(), "RECEIVED");
        }
    }
}
