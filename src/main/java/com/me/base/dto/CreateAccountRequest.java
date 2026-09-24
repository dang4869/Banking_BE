package com.me.base.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAccountRequest {
    
    @NotBlank(message = "Currency is required (e.g., VND, USD)")
    private String currency;
    
}
