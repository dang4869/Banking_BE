package com.me.base.dto;

import lombok.Data;

@Data
public class BeneficiaryResponseDto {
    private Long id;
    private String accountNumber;
    private String accountName;
    private String bankName;
    private String nickname;
}
