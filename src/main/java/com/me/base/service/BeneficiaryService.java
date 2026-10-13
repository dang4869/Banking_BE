package com.me.base.service;

import com.me.base.dto.BeneficiaryRequestDto;
import com.me.base.dto.BeneficiaryResponseDto;

import java.util.List;

public interface BeneficiaryService {
    BeneficiaryResponseDto addBeneficiary(BeneficiaryRequestDto request);
    List<BeneficiaryResponseDto> getMyBeneficiaries();
    BeneficiaryResponseDto updateBeneficiary(Long id, BeneficiaryRequestDto request);
    void deleteBeneficiary(Long id);
}
