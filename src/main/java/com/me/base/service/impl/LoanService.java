package com.me.base.service.impl;

import com.me.base.dto.*;
import com.me.base.entity.Loan;
import com.me.base.entity.LoanRepayment;
import com.me.base.entity.User;
import com.me.base.enums.LoanStatus;
import com.me.base.enums.RepaymentStatus;
import com.me.base.exception.BusinessException;
import com.me.base.exception.ErrorCode;
import com.me.base.repository.ILoanRepaymentRepository;
import com.me.base.repository.ILoanRepository;
import com.me.base.repository.IUserRepository;
import com.me.base.service.ILoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

/**
 * Triển khai nghiệp vụ module vay.
 * <p>
 * Công thức tính góp đều tháng (EMI - Equated Monthly Installment):
 * EMI = P * r * (1+r)^n / ((1+r)^n - 1)
 * Trong đó:
 *   P = Principal (Số tiền vay)
 *   r = Lãi suất tháng = annualRate / 12 / 100
 *   n = Số kỳ (tháng)
 *
 * @author Banking Project
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LoanService implements ILoanService {

    private final ILoanRepository loanRepository;
    private final ILoanRepaymentRepository repaymentRepository;
    private final IUserRepository userRepository;

    // ============================================================
    // USER OPERATIONS
    // ============================================================

    @Override
    public LoanResponse applyForLoan(String userEmail, LoanRequest request) {
        User user = findUserByEmail(userEmail);

        BigDecimal principal = request.getPrincipalAmount();
        BigDecimal annualRate = request.getInterestRatePerAnnum();
        int termMonths = request.getTermMonths();

        // Tính EMI, tổng lãi, tổng phải trả
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal emi = calculateEMI(principal, monthlyRate, termMonths);
        BigDecimal totalPayment = emi.multiply(BigDecimal.valueOf(termMonths)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalPayment.subtract(principal).setScale(2, RoundingMode.HALF_UP);

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLoanType(request.getLoanType());
        loan.setPrincipalAmount(principal);
        loan.setInterestRatePerAnnum(annualRate);
        loan.setTermMonths(termMonths);
        loan.setMonthlyPayment(emi);
        loan.setTotalInterest(totalInterest);
        loan.setTotalPayment(totalPayment);
        loan.setPurpose(request.getPurpose());
        loan.setStatus(LoanStatus.PENDING);

        Loan saved = loanRepository.save(loan);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanResponse> getMyLoans(String userEmail) {
        User user = findUserByEmail(userEmail);
        return loanRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanById(Long loanId, String userEmail) {
        Loan loan = findLoanById(loanId);
        // User chỉ xem được loan của chính mình
        if (!loan.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return mapToResponse(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanRepaymentResponse> getRepaymentSchedule(Long loanId, String userEmail) {
        Loan loan = findLoanById(loanId);
        if (!loan.getUser().getEmail().equals(userEmail)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return repaymentRepository.findByLoanOrderByInstallmentNumber(loan)
                .stream()
                .map(this::mapRepaymentToResponse)
                .toList();
    }

    @Override
    public LoanRepaymentResponse payInstallment(Long repaymentId, String userEmail) {
        LoanRepayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_REPAYMENT_NOT_FOUND));

        // Kiểm tra quyền sở hữu
        if (!repayment.getLoan().getUser().getEmail().equals(userEmail)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // Kiểm tra trạng thái khoản vay
        if (repayment.getLoan().getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.LOAN_NOT_ACTIVE);
        }

        // Kiểm tra kỳ đã thanh toán chưa
        if (repayment.getStatus() == RepaymentStatus.PAID) {
            throw new BusinessException(ErrorCode.LOAN_REPAYMENT_ALREADY_PAID);
        }

        repayment.setStatus(RepaymentStatus.PAID);
        repayment.setPaidAt(LocalDate.now());
        LoanRepayment saved = repaymentRepository.save(repayment);

        // Kiểm tra nếu đã thanh toán hết tất cả kỳ → tự động đóng loan
        long unpaidCount = repaymentRepository.countByLoanAndStatus(repayment.getLoan(), RepaymentStatus.PENDING)
                + repaymentRepository.countByLoanAndStatus(repayment.getLoan(), RepaymentStatus.OVERDUE);
        if (unpaidCount == 0) {
            Loan loan = repayment.getLoan();
            loan.setStatus(LoanStatus.CLOSED);
            loan.setClosedAt(LocalDate.now());
            loanRepository.save(loan);
        }

        return mapRepaymentToResponse(saved);
    }

    // ============================================================
    // ADMIN OPERATIONS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public Page<LoanResponse> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public LoanResponse reviewLoan(Long loanId, LoanReviewRequest request) {
        Loan loan = findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_PROCESSED);
        }

        if (request.isApproved()) {
            loan.setStatus(LoanStatus.APPROVED);
            loan.setApprovedAt(LocalDate.now());
        } else {
            loan.setStatus(LoanStatus.REJECTED);
        }
        loan.setAdminNote(request.getAdminNote());

        return mapToResponse(loanRepository.save(loan));
    }

    @Override
    public LoanResponse disburseLoan(Long loanId) {
        Loan loan = findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new BusinessException(ErrorCode.LOAN_NOT_APPROVED);
        }

        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursedAt(LocalDate.now());
        Loan saved = loanRepository.save(loan);

        // Tạo lịch trả nợ tự động
        generateRepaymentSchedule(saved);

        // Chuyển sang ACTIVE
        saved.setStatus(LoanStatus.ACTIVE);
        return mapToResponse(loanRepository.save(saved));
    }

    @Override
    public LoanResponse closeLoan(Long loanId) {
        Loan loan = findLoanById(loanId);

        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new BusinessException(ErrorCode.LOAN_ALREADY_CLOSED);
        }
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.LOAN_NOT_ACTIVE);
        }

        loan.setStatus(LoanStatus.CLOSED);
        loan.setClosedAt(LocalDate.now());
        return mapToResponse(loanRepository.save(loan));
    }

    // ============================================================
    // PRIVATE HELPERS
    // ============================================================

    /**
     * Tạo lịch trả nợ theo phương pháp tính góp đều (amortization).
     * Mỗi kỳ: tiền gốc tăng dần, tiền lãi giảm dần, tổng trả bằng nhau.
     */
    private void generateRepaymentSchedule(Loan loan) {
        BigDecimal principal = loan.getPrincipalAmount();
        BigDecimal annualRate = loan.getInterestRatePerAnnum();
        int termMonths = loan.getTermMonths();
        BigDecimal emi = loan.getMonthlyPayment();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        BigDecimal remainingBalance = principal;
        LocalDate disbursedDate = loan.getDisbursedAt();

        for (int i = 1; i <= termMonths; i++) {
            BigDecimal interestForPeriod = remainingBalance
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalForPeriod;
            if (i == termMonths) {
                // Kỳ cuối: trả hết số dư còn lại (tránh sai số làm tròn)
                principalForPeriod = remainingBalance;
            } else {
                principalForPeriod = emi.subtract(interestForPeriod).setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal totalForPeriod = principalForPeriod.add(interestForPeriod).setScale(2, RoundingMode.HALF_UP);
            remainingBalance = remainingBalance.subtract(principalForPeriod).setScale(2, RoundingMode.HALF_UP);

            LoanRepayment repayment = new LoanRepayment();
            repayment.setLoan(loan);
            repayment.setInstallmentNumber(i);
            repayment.setDueDate(disbursedDate.plusMonths(i));
            repayment.setPrincipalAmount(principalForPeriod);
            repayment.setInterestAmount(interestForPeriod);
            repayment.setTotalAmount(totalForPeriod);
            repayment.setRemainingBalance(remainingBalance.max(BigDecimal.ZERO));
            repayment.setStatus(RepaymentStatus.PENDING);

            repaymentRepository.save(repayment);
        }
    }

    /**
     * Tính EMI theo công thức: P * r * (1+r)^n / ((1+r)^n - 1)
     */
    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal monthlyRate, int termMonths) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }
        MathContext mc = new MathContext(15, RoundingMode.HALF_UP);
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal factor = onePlusR.pow(termMonths, mc);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(factor);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Loan findLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOAN_NOT_FOUND));
    }

    private LoanResponse mapToResponse(Loan loan) {
        LoanResponse res = new LoanResponse();
        res.setId(loan.getId());
        res.setUserId(loan.getUser().getId());
        res.setUserFullName(loan.getUser().getFullName());
        res.setLoanType(loan.getLoanType());
        res.setPrincipalAmount(loan.getPrincipalAmount());
        res.setInterestRatePerAnnum(loan.getInterestRatePerAnnum());
        res.setTermMonths(loan.getTermMonths());
        res.setMonthlyPayment(loan.getMonthlyPayment());
        res.setTotalInterest(loan.getTotalInterest());
        res.setTotalPayment(loan.getTotalPayment());
        res.setPurpose(loan.getPurpose());
        res.setStatus(loan.getStatus());
        res.setAdminNote(loan.getAdminNote());
        res.setApprovedAt(loan.getApprovedAt());
        res.setDisbursedAt(loan.getDisbursedAt());
        res.setClosedAt(loan.getClosedAt());
        res.setCreatedAt(loan.getCreatedAt());
        res.setUpdatedAt(loan.getUpdatedAt());
        return res;
    }

    private LoanRepaymentResponse mapRepaymentToResponse(LoanRepayment r) {
        LoanRepaymentResponse res = new LoanRepaymentResponse();
        res.setId(r.getId());
        res.setLoanId(r.getLoan().getId());
        res.setInstallmentNumber(r.getInstallmentNumber());
        res.setDueDate(r.getDueDate());
        res.setPrincipalAmount(r.getPrincipalAmount());
        res.setInterestAmount(r.getInterestAmount());
        res.setTotalAmount(r.getTotalAmount());
        res.setRemainingBalance(r.getRemainingBalance());
        res.setStatus(r.getStatus());
        res.setPaidAt(r.getPaidAt());
        res.setCreatedAt(r.getCreatedAt());
        return res;
    }
}
