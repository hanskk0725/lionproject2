package com.example.lionproject2backend.payment.repository;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentCustomRepository {

    /**
     * 멘티의 결제 목록 조회 (페이지네이션, 상태 필터, 검색)
     */
    Page<Payment> findByMenteeIdWithFilters(
            Long menteeId,
            PaymentStatus status,
            String keyword,
            Pageable pageable
    );
}

