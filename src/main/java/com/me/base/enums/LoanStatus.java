package com.me.base.enums;

/**
 * Trạng thái đơn vay vốn.
 */
public enum LoanStatus {
    PENDING,    // Chờ duyệt
    APPROVED,   // Đã duyệt
    REJECTED,   // Từ chối
    DISBURSED,  // Đã giải ngân
    ACTIVE,     // Đang hoạt động (có lịch trả nợ)
    CLOSED      // Đã tất toán
}
