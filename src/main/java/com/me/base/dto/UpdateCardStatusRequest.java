package com.me.base.dto;

import com.me.base.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCardStatusRequest {
    
    @NotNull(message = "Status is required")
    private CardStatus status;
    
}
