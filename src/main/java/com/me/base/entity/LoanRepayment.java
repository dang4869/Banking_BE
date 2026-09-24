package com.me.base.entity;

import com.me.base.enums.RepaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity đại diện cho một kỳ trả nợ trong lịch trả nợ.
 * Mỗi khoản vay có N kỳ tương ứng với termMonths.
 *
 * @author Banking Project
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "loan_repayments", indexes = {
    @Index(name = "idx_repayment_loan_id", columnList = "loan_id"),
    @Index(name = "idx_repayment_due_date", columnList = "due_date"),
    @Index(name = "idx_repayment_status", columnList = "status")
})
public class LoanRepayment extends BaseEntity {

    /**
     * Khoản vay liên kết.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    /**
     * Số thứ tự kỳ trả nợ (1, 2, 3...).
     */
    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    /**
     * Ngày đến hạn trả.
     */
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    /**
     * Số tiền gốc phải trả trong kỳ này.
     */
    @Column(name = "principal_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal principalAmount;

    /**
     * Số tiền lãi phải trả trong kỳ này.
     */
    @Column(name = "interest_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal interestAmount;

    /**
     * Tổng tiền phải trả kỳ này (gốc + lãi).
     */
    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Số dư gốc còn lại sau kỳ này.
     */
    @Column(name = "remaining_balance", nullable = false, precision = 18, scale = 2)
    private BigDecimal remainingBalance;

    /**
     * Trạng thái kỳ trả nợ.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private RepaymentStatus status = RepaymentStatus.PENDING;

    /**
     * Ngày thực tế thanh toán.
     */
    @Column(name = "paid_at")
    private LocalDate paidAt;
}
