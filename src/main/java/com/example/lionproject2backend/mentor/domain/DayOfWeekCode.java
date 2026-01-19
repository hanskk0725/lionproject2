package com.example.lionproject2backend.mentor.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;

@Getter
@RequiredArgsConstructor
public enum DayOfWeekCode {
    MONDAY("월요일", DayOfWeek.MONDAY),
    TUESDAY("화요일", DayOfWeek.TUESDAY),
    WEDNESDAY("수요일", DayOfWeek.WEDNESDAY),
    THURSDAY("목요일", DayOfWeek.THURSDAY),
    FRIDAY("금요일", DayOfWeek.FRIDAY),
    SATURDAY("토요일", DayOfWeek.SATURDAY),
    SUNDAY("일요일", DayOfWeek.SUNDAY);

    private final String description;
    private final DayOfWeek dayOfWeek;

    /**
     * java.time.DayOfWeek을 DayOfWeekCode로 변환
     */
    public static DayOfWeekCode from(DayOfWeek dayOfWeek) {
        for (DayOfWeekCode code : values()) {
            if (code.dayOfWeek == dayOfWeek) {
                return code;
            }
        }
        throw new IllegalArgumentException("Unknown DayOfWeek: " + dayOfWeek);
    }

    /**
     * 주어진 java.time.DayOfWeek과 일치하는지 확인
     */
    public boolean matches(DayOfWeek dayOfWeek) {
        return this.dayOfWeek == dayOfWeek;
    }
}
