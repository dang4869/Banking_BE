package com.me.base.entity;

import com.me.base.enums.KycStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "kyc_requests")
public class KycRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String idNumber;

    @Column(nullable = false, length = 500)
    private String idCardFrontUrl;

    @Column(nullable = false, length = 500)
    private String idCardBackUrl;

    @Column(nullable = false, length = 500)
    private String selfieUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KycStatus status = KycStatus.PENDING;

    @Column(length = 500)
    private String rejectionReason;
}
