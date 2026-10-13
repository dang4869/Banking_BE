package com.me.base.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeneficiaryRequestDto {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    private String nickname;
}
