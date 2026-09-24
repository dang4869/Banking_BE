package com.me.base.enums;

/**
 * Trạng thái kỳ trả nợ.
 */
public enum RepaymentStatus {
    PENDING,    // Chưa đến hạn
    PAID,       // Đã thanh toán
    OVERDUE,    // Quá hạn
    WAIVED      // Được miễn (do ADMIN)
}
