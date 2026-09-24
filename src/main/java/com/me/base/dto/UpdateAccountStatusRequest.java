package com.me.base.dto;

import com.me.base.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAccountStatusRequest {
    
    @NotNull(message = "Status is required")
    private AccountStatus status;
    
}
