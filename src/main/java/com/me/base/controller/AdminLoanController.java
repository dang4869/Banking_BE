package com.me.base.controller;

import com.me.base.dto.*;
import com.me.base.service.ILoanService;
import com.me.base.service.IMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller cho các nghiệp vụ quản lý vay của ADMIN.
 * <p>
 * Base path: /api/admin/loans
 * Tất cả endpoints yêu cầu ADMIN role.
 *
 * @author Banking Project
 * @version 1.0
 */
@Tag(name = "Admin - Loans", description = "Admin loan management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/admin/loans")
@RequiredArgsConstructor
public class AdminLoanController {

    private final ILoanService loanService;
    private final IMessageService messageService;

    @Operation(summary = "[ADMIN] Tất cả đơn vay",
               description = "Lấy danh sách tất cả khoản vay với phân trang và sắp xếp theo ngày tạo mới nhất.")
    @GetMapping
    public ResponseEntity<BaseResponse<Page<LoanResponse>>> getAllLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<LoanResponse> loans = loanService.getAllLoans(pageable);
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.loans.retrieved"), loans));
    }

    @Operation(summary = "[ADMIN] Duyệt / Từ chối đơn vay",
               description = "Duyệt hoặc từ chối đơn vay đang ở trạng thái PENDING. Có thể kèm theo ghi chú.")
    @PutMapping("/{loanId}/review")
    public ResponseEntity<BaseResponse<LoanResponse>> reviewLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody LoanReviewRequest request) {

        LoanResponse loan = loanService.reviewLoan(loanId, request);
        String msg = request.isApproved()
                ? messageService.getMessage("success.loan.approved")
                : messageService.getMessage("success.loan.rejected");
        return ResponseEntity.ok(BaseResponse.success(msg, loan));
    }

    @Operation(summary = "[ADMIN] Giải ngân khoản vay",
               description = "Giải ngân khoản vay đã được APPROVED. Hệ thống sẽ tự động tạo lịch trả nợ và chuyển sang ACTIVE.")
    @PutMapping("/{loanId}/disburse")
    public ResponseEntity<BaseResponse<LoanResponse>> disburseLoan(@PathVariable Long loanId) {
        LoanResponse loan = loanService.disburseLoan(loanId);
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.loan.disbursed"), loan));
    }

    @Operation(summary = "[ADMIN] Tất toán khoản vay",
               description = "Đóng khoản vay đang ACTIVE trước hạn (ví dụ: user trả toàn bộ số dư còn lại).")
    @PutMapping("/{loanId}/close")
    public ResponseEntity<BaseResponse<LoanResponse>> closeLoan(@PathVariable Long loanId) {
        LoanResponse loan = loanService.closeLoan(loanId);
        return ResponseEntity.ok(BaseResponse.success(messageService.getMessage("success.loan.closed"), loan));
    }
}
