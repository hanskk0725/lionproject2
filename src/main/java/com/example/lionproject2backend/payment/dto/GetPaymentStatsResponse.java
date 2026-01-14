package com.example.lionproject2backend.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetPaymentStatsResponse {

    private int totalAmount;  // 총 결제 금액
    private int monthlyAmount;  // 이번 달 지불액
    private long activeTutorialCount;  // 진행 중인 튜터링 수 (이용권이 남아있는 튜터링 수)

    public static GetPaymentStatsResponse of(
            int totalAmount,
            int monthlyAmount,
            long activeTutorialCount
    ) {
        return GetPaymentStatsResponse.builder()
                .totalAmount(totalAmount)
                .monthlyAmount(monthlyAmount)
                .activeTutorialCount(activeTutorialCount)
                .build();
    }

}

