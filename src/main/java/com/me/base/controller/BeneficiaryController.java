package com.me.base.controller;

import com.me.base.dto.BeneficiaryRequestDto;
import com.me.base.dto.BeneficiaryResponseDto;
import com.me.base.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @PostMapping
    public ResponseEntity<BeneficiaryResponseDto> addBeneficiary(@Valid @RequestBody BeneficiaryRequestDto request) {
        return new ResponseEntity<>(beneficiaryService.addBeneficiary(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponseDto>> getMyBeneficiaries() {
        return ResponseEntity.ok(beneficiaryService.getMyBeneficiaries());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeneficiaryResponseDto> updateBeneficiary(
            @PathVariable Long id, 
            @Valid @RequestBody BeneficiaryRequestDto request) {
        return ResponseEntity.ok(beneficiaryService.updateBeneficiary(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.noContent().build();
    }
}
