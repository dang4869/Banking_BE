package com.me.base.repository;

import com.me.base.entity.Loan;
import com.me.base.entity.LoanRepayment;
import com.me.base.enums.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository cho LoanRepayment entity.
 */
@Repository
public interface ILoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {

    /** Toàn bộ lịch trả nợ của một khoản vay, sắp xếp theo kỳ. */
    List<LoanRepayment> findByLoanOrderByInstallmentNumber(Loan loan);

    /** Các kỳ trả nợ quá hạn (để job tự động cập nhật). */
    List<LoanRepayment> findByStatusAndDueDateBefore(RepaymentStatus status, LocalDate date);

    /** Đếm số kỳ chưa thanh toán của khoản vay. */
    long countByLoanAndStatus(Loan loan, RepaymentStatus status);
}
