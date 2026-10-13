package com.me.base.repository;

import com.me.base.entity.KycRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycRequestRepository extends JpaRepository<KycRequest, Long> {
    Optional<KycRequest> findByUserId(Long userId);
}
