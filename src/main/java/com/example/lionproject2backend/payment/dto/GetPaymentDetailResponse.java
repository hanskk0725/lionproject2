package com.example.lionproject2backend.payment.dto;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetPaymentDetailResponse {

    private Long paymentId;
    private Long tutorialId;
    private String tutorialTitle;
    private Long mentorId;
    private String mentorName;
    private int amount;
    private int count;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public static GetPaymentDetailResponse from(Payment payment) {
        return GetPaymentDetailResponse.builder()
                .paymentId(payment.getId())
                .tutorialId(payment.getTutorial().getId())
                .tutorialTitle(payment.getTutorial().getTitle())
                .mentorId(payment.getTutorial().getMentor().getId())
                .mentorName(payment.getTutorial().getMentor().getUser().getNickname())
                .amount(payment.getAmount())
                .count(payment.getCount())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}

