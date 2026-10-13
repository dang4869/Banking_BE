package com.me.base.controller;

import com.me.base.dto.BaseResponse;
import com.me.base.dto.KycResponseDto;
import com.me.base.entity.KycRequest;
import com.me.base.service.IKycService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kyc")
@RequiredArgsConstructor
public class KycController {

    private final IKycService kycService;

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<KycResponseDto>> submitKyc(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("idNumber") String idNumber,
            @RequestPart("idCardFront") MultipartFile idCardFront,
            @RequestPart("idCardBack") MultipartFile idCardBack,
            @RequestPart("selfie") MultipartFile selfie) {

        Long userId = Long.valueOf(userDetails.getUsername());
        KycRequest request = kycService.submitKyc(userId, idNumber, idCardFront, idCardBack, selfie);
        return ResponseEntity.ok(BaseResponse.success("KYC submitted successfully", mapToDto(request)));
    }

    @GetMapping("/status")
    public ResponseEntity<BaseResponse<KycResponseDto>> getKycStatus(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        KycRequest request = kycService.getKycStatus(userId);
        return ResponseEntity.ok(BaseResponse.success("KYC status fetched", mapToDto(request)));
    }

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<KycResponseDto>>> getAllPendingRequests() {
        List<KycResponseDto> pendingRequests = kycService.getAllPendingRequests()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(BaseResponse.success("Pending KYC requests", pendingRequests));
    }

    @PostMapping("/admin/approve/{kycId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<KycResponseDto>> approveKyc(@PathVariable Long kycId) {
        KycRequest request = kycService.approveKyc(kycId);
        return ResponseEntity.ok(BaseResponse.success("KYC approved", mapToDto(request)));
    }

    @PostMapping("/admin/reject/{kycId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<KycResponseDto>> rejectKyc(
            @PathVariable Long kycId,
            @RequestParam("reason") String reason) {
        KycRequest request = kycService.rejectKyc(kycId, reason);
        return ResponseEntity.ok(BaseResponse.success("KYC rejected", mapToDto(request)));
    }

    private KycResponseDto mapToDto(KycRequest request) {
        return KycResponseDto.builder()
                .id(request.getId())
                .username(request.getUser().getUsername())
                .idNumber(request.getIdNumber())
                .idCardFrontUrl(request.getIdCardFrontUrl())
                .idCardBackUrl(request.getIdCardBackUrl())
                .selfieUrl(request.getSelfieUrl())
                .status(request.getStatus())
                .rejectionReason(request.getRejectionReason())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
