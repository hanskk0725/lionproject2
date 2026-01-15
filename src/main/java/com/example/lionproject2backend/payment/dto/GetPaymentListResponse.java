package com.example.lionproject2backend.payment.dto;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetPaymentListResponse {

    private Long paymentId;
    private Long tutorialId;
    private String tutorialTitle;
    private String mentorName;
    private int amount;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public static GetPaymentListResponse from(Payment payment) {
        return GetPaymentListResponse.builder()
                .paymentId(payment.getId())
                .tutorialId(payment.getTutorial().getId())
                .tutorialTitle(payment.getTutorial().getTitle())
                .mentorName(payment.getTutorial().getMentor().getUser().getNickname())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

