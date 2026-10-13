package com.me.base.service.impl;

import com.me.base.dto.BeneficiaryRequestDto;
import com.me.base.dto.BeneficiaryResponseDto;
import com.me.base.entity.Beneficiary;
import com.me.base.entity.User;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import com.me.base.repository.BeneficiaryRepository;
import com.me.base.repository.IUserRepository;
import com.me.base.service.BeneficiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BeneficiaryServiceImpl implements BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final IUserRepository userRepository;

    private Long getCurrentUserId() {
        String currentUserIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(currentUserIdStr);
    }

    @Override
    @Transactional
    public BeneficiaryResponseDto addBeneficiary(BeneficiaryRequestDto request) {
        Long userId = getCurrentUserId();

        if (beneficiaryRepository.existsByUserIdAndAccountNumber(userId, request.getAccountNumber())) {
            throw new BusinessException(ErrorCode.BENEFICIARY_ALREADY_EXISTS, "You have already saved this beneficiary");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(user);
        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setAccountName(request.getAccountName());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setNickname(request.getNickname());

        beneficiary = beneficiaryRepository.save(beneficiary);

        return mapToDto(beneficiary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeneficiaryResponseDto> getMyBeneficiaries() {
        Long userId = getCurrentUserId();
        List<Beneficiary> beneficiaries = beneficiaryRepository.findByUserId(userId);
        return beneficiaries.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BeneficiaryResponseDto updateBeneficiary(Long id, BeneficiaryRequestDto request) {
        Long userId = getCurrentUserId();

        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BENEFICIARY_NOT_FOUND, "Beneficiary not found"));

        // If they change the account number, we need to check if it already exists for another beneficiary
        if (!beneficiary.getAccountNumber().equals(request.getAccountNumber()) && 
            beneficiaryRepository.existsByUserIdAndAccountNumber(userId, request.getAccountNumber())) {
            throw new BusinessException(ErrorCode.BENEFICIARY_ALREADY_EXISTS, "You have already saved a beneficiary with this account number");
        }

        beneficiary.setAccountNumber(request.getAccountNumber());
        beneficiary.setAccountName(request.getAccountName());
        beneficiary.setBankName(request.getBankName());
        beneficiary.setNickname(request.getNickname());

        beneficiary = beneficiaryRepository.save(beneficiary);
        return mapToDto(beneficiary);
    }

    @Override
    @Transactional
    public void deleteBeneficiary(Long id) {
        Long userId = getCurrentUserId();
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BENEFICIARY_NOT_FOUND, "Beneficiary not found"));

        beneficiaryRepository.delete(beneficiary);
    }

    private BeneficiaryResponseDto mapToDto(Beneficiary beneficiary) {
        BeneficiaryResponseDto dto = new BeneficiaryResponseDto();
        dto.setId(beneficiary.getId());
        dto.setAccountNumber(beneficiary.getAccountNumber());
        dto.setAccountName(beneficiary.getAccountName());
        dto.setBankName(beneficiary.getBankName());
        dto.setNickname(beneficiary.getNickname());
        return dto;
    }
}
