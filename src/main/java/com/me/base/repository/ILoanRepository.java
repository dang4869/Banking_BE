package com.me.base.repository;

import com.me.base.entity.Loan;
import com.me.base.entity.User;
import com.me.base.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho Loan entity.
 */
@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {

    /** Tất cả khoản vay của một user. */
    List<Loan> findByUser(User user);

    /** Tất cả khoản vay theo trạng thái (dùng cho ADMIN). */
    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);

    /** Tất cả khoản vay (ADMIN - phân trang). */
    Page<Loan> findAll(Pageable pageable);

    /** Đếm khoản vay đang ACTIVE của user. */
    long countByUserAndStatus(User user, LoanStatus status);
}
