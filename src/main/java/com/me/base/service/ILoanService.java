package com.me.base.service;

import com.me.base.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface định nghĩa các nghiệp vụ của module vay.
 */
public interface ILoanService {

    /** User nộp đơn đăng ký vay. */
    LoanResponse applyForLoan(String userEmail, LoanRequest request);

    /** Lấy danh sách khoản vay của chính user đang login. */
    List<LoanResponse> getMyLoans(String userEmail);

    /** Lấy chi tiết một khoản vay (user chỉ xem được của mình). */
    LoanResponse getLoanById(Long loanId, String userEmail);

    /** Lấy lịch trả nợ của khoản vay. */
    List<LoanRepaymentResponse> getRepaymentSchedule(Long loanId, String userEmail);

    /** User thanh toán một kỳ trả nợ. */
    LoanRepaymentResponse payInstallment(Long repaymentId, String userEmail);

    // ---- ADMIN ----

    /** ADMIN xem tất cả đơn vay (phân trang). */
    Page<LoanResponse> getAllLoans(Pageable pageable);

    /** ADMIN duyệt hoặc từ chối đơn vay. */
    LoanResponse reviewLoan(Long loanId, LoanReviewRequest request);

    /** ADMIN giải ngân khoản vay đã được duyệt. */
    LoanResponse disburseLoan(Long loanId);

    /** ADMIN tất toán khoản vay (đóng sớm). */
    LoanResponse closeLoan(Long loanId);
}
