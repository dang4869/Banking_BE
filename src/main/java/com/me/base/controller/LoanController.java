package com.me.base.controller;

import com.me.base.dto.*;
import com.me.base.service.ILoanService;
import com.me.base.service.IMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho các nghiệp vụ vay vốn của người dùng.
 * <p>
 * Base path: /api/loans
 * Tất cả endpoints yêu cầu xác thực JWT.
 *
 * @author Banking Project
 * @version 1.0
 */
@Tag(name = "Loans", description = "Loan management APIs for users")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService loanService;
    private final IMessageService messageService;

    @Operation(summary = "Đăng ký khoản vay",
               description = "Nộp đơn đăng ký vay vốn. Đơn sẽ ở trạng thái PENDING chờ ADMIN duyệt.")
    @PostMapping("/apply")
    public ResponseEntity<BaseResponse<LoanResponse>> applyForLoan(
            @Valid @RequestBody LoanRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        LoanResponse response = loanService.applyForLoan(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success(messageService.getMessage("success.loan.applied"), response));
    }

    @Operation(summary = "Danh sách khoản vay của tôi",
               description = "Trả về tất cả khoản vay của user đang đăng nhập.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<LoanResponse>>> getMyLoans(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<LoanResponse> loans = loanService.getMyLoans(userDetails.getUsername());
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.loans.retrieved"), loans));
    }

    @Operation(summary = "Chi tiết khoản vay",
               description = "Xem chi tiết một khoản vay. User chỉ xem được loan của chính mình.")
    @GetMapping("/{loanId}")
    public ResponseEntity<BaseResponse<LoanResponse>> getLoanById(
            @PathVariable Long loanId,
            @AuthenticationPrincipal UserDetails userDetails) {

        LoanResponse loan = loanService.getLoanById(loanId, userDetails.getUsername());
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.loan.retrieved"), loan));
    }

    @Operation(summary = "Lịch trả nợ",
               description = "Xem lịch trả nợ theo từng kỳ của khoản vay. Chỉ hiển thị khi khoản vay ở trạng thái ACTIVE.")
    @GetMapping("/{loanId}/repayments")
    public ResponseEntity<BaseResponse<List<LoanRepaymentResponse>>> getRepaymentSchedule(
            @PathVariable Long loanId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<LoanRepaymentResponse> schedule = loanService.getRepaymentSchedule(loanId, userDetails.getUsername());
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.repayments.retrieved"), schedule));
    }

    @Operation(summary = "Thanh toán kỳ trả nợ",
               description = "Thanh toán một kỳ trong lịch trả nợ theo ID của kỳ đó.")
    @PostMapping("/repayments/{repaymentId}/pay")
    public ResponseEntity<BaseResponse<LoanRepaymentResponse>> payInstallment(
            @PathVariable Long repaymentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        LoanRepaymentResponse response = loanService.payInstallment(repaymentId, userDetails.getUsername());
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.repayment.paid"), response));
    }
}
