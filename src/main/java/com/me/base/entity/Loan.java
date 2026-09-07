package com.me.base.entity;

import com.me.base.enums.LoanStatus;
import com.me.base.enums.LoanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho khoản vay của người dùng.
 * <p>
 * Flow: PENDING → APPROVED/REJECTED → DISBURSED → ACTIVE → CLOSED
 *
 * @author Banking Project
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = "repaymentSchedules")
@Entity
@Table(name = "loans", indexes = {
    @Index(name = "idx_loan_user_id", columnList = "user_id"),
    @Index(name = "idx_loan_status", columnList = "status")
})
public class Loan extends BaseEntity {

    /**
     * Người dùng đăng ký khoản vay.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Loại khoản vay (cá nhân, nhà, xe...).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanType loanType;

    /**
     * Số tiền vay (VNĐ).
     */
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal principalAmount;

    /**
     * Lãi suất hàng năm (%).
     * Ví dụ: 12.5 tương đương 12.5%/năm
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRatePerAnnum;

    /**
     * Số kỳ trả nợ (tháng).
     */
    @Column(nullable = false)
    private Integer termMonths;

    /**
     * Số tiền phải trả mỗi tháng (tính tự động).
     */
    @Column(name = "monthly_payment", precision = 18, scale = 2)
    private BigDecimal monthlyPayment;

    /**
     * Tổng lãi phải trả trong toàn bộ kỳ vay.
     */
    @Column(name = "total_interest", precision = 18, scale = 2)
    private BigDecimal totalInterest;

    /**
     * Tổng số tiền phải trả (gốc + lãi).
     */
    @Column(name = "total_payment", precision = 18, scale = 2)
    private BigDecimal totalPayment;

    /**
     * Mục đích vay vốn.
     */
    @Column(length = 500)
    private String purpose;

    /**
     * Trạng thái đơn vay.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status = LoanStatus.PENDING;

    /**
     * Ghi chú từ ADMIN khi duyệt hoặc từ chối.
     */
    @Column(name = "admin_note", length = 500)
    private String adminNote;

    /**
     * Ngày được duyệt.
     */
    @Column(name = "approved_at")
    private LocalDate approvedAt;

    /**
     * Ngày giải ngân.
     */
    @Column(name = "disbursed_at")
    private LocalDate disbursedAt;

    /**
     * Ngày tất toán khoản vay.
     */
    @Column(name = "closed_at")
    private LocalDate closedAt;

    /**
     * Lịch trả nợ của khoản vay.
     */
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoanRepayment> repaymentSchedules = new ArrayList<>();
}
