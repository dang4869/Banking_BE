package com.me.base.dto;

import com.me.base.enums.KycStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KycResponseDto {
    private Long id;
    private String username;
    private String idNumber;
    private String idCardFrontUrl;
    private String idCardBackUrl;
    private String selfieUrl;
    private KycStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
