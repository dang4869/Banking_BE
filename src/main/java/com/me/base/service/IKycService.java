package com.me.base.service;

import com.me.base.entity.KycRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IKycService {
    KycRequest submitKyc(Long userId, String idNumber, MultipartFile idCardFront, MultipartFile idCardBack, MultipartFile selfie);
    
    KycRequest getKycStatus(Long userId);
    
    List<KycRequest> getAllPendingRequests();
    
    KycRequest approveKyc(Long kycId);
    
    KycRequest rejectKyc(Long kycId, String reason);
}
