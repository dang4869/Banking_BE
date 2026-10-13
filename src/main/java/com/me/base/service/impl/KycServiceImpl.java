package com.me.base.service.impl;

import com.me.base.entity.KycRequest;
import com.me.base.entity.User;
import com.me.base.enums.KycStatus;
import com.me.base.repository.KycRequestRepository;
import com.me.base.repository.IUserRepository;
import com.me.base.service.IFileStorageService;
import com.me.base.service.IKycService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KycServiceImpl implements IKycService {

    private final KycRequestRepository kycRequestRepository;
    private final IUserRepository userRepository;
    private final IFileStorageService fileStorageService;

    @Override
    @Transactional
    public KycRequest submitKyc(Long userId, String idNumber, MultipartFile idCardFront, MultipartFile idCardBack, MultipartFile selfie) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getKycStatus() == KycStatus.VERIFIED) {
            throw new RuntimeException("User is already KYC verified");
        }
        
        Optional<KycRequest> existingRequest = kycRequestRepository.findByUserId(user.getId());
        if (existingRequest.isPresent() && existingRequest.get().getStatus() == KycStatus.PENDING) {
            throw new RuntimeException("A KYC request is already pending");
        }

        String frontUrl = fileStorageService.storeFile(idCardFront);
        String backUrl = fileStorageService.storeFile(idCardBack);
        String selfieUrl = fileStorageService.storeFile(selfie);

        KycRequest kycRequest = existingRequest.orElse(new KycRequest());
        kycRequest.setUser(user);
        kycRequest.setIdNumber(idNumber);
        kycRequest.setIdCardFrontUrl(frontUrl);
        kycRequest.setIdCardBackUrl(backUrl);
        kycRequest.setSelfieUrl(selfieUrl);
        kycRequest.setStatus(KycStatus.PENDING);
        kycRequest.setRejectionReason(null);

        user.setKycStatus(KycStatus.PENDING);
        userRepository.save(user);

        return kycRequestRepository.save(kycRequest);
    }

    @Override
    public KycRequest getKycStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return kycRequestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No KYC request found for user"));
    }

    @Override
    public List<KycRequest> getAllPendingRequests() {
        return kycRequestRepository.findAll().stream()
                .filter(req -> req.getStatus() == KycStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KycRequest approveKyc(Long kycId) {
        KycRequest kycRequest = kycRequestRepository.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC request not found"));
        
        if (kycRequest.getStatus() != KycStatus.PENDING) {
            throw new RuntimeException("Can only approve PENDING requests");
        }

        kycRequest.setStatus(KycStatus.VERIFIED);
        
        User user = kycRequest.getUser();
        user.setKycStatus(KycStatus.VERIFIED);
        userRepository.save(user);

        return kycRequestRepository.save(kycRequest);
    }

    @Override
    @Transactional
    public KycRequest rejectKyc(Long kycId, String reason) {
        KycRequest kycRequest = kycRequestRepository.findById(kycId)
                .orElseThrow(() -> new RuntimeException("KYC request not found"));
        
        if (kycRequest.getStatus() != KycStatus.PENDING) {
            throw new RuntimeException("Can only reject PENDING requests");
        }

        kycRequest.setStatus(KycStatus.REJECTED);
        kycRequest.setRejectionReason(reason);
        
        User user = kycRequest.getUser();
        user.setKycStatus(KycStatus.REJECTED);
        userRepository.save(user);

        return kycRequestRepository.save(kycRequest);
    }
}
